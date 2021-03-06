package com.noenv.cronutils.impl;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.noenv.cronutils.CronScheduler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The implementation of the {@link com.noenv.cronutils.CronScheduler}. This implementation is based on the cron-utils
 * library provided by Joze Martin Rozanec
 *
 * @author Lukas Prettenthaler
 */
public class CronSchedulerImpl implements CronScheduler, Handler<Long> {

  private final Vertx vertx;
  private final ExecutionTime expression;
  private Handler<CronScheduler> handler;
  private long timerId;
  private ZonedDateTime executionTime;

  public CronSchedulerImpl(final Vertx vertx, final String cronExpression, final CronType type) {
    this.vertx = vertx;

    final CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(type);
    final CronParser parser = new CronParser(definition);
    this.expression = ExecutionTime.forCron(parser.parse(cronExpression));
  }

  @Override
  public CronScheduler schedule(final Handler<CronScheduler> handler) {
    Objects.requireNonNull(handler);
    if (this.handler != null) {
      throw new IllegalArgumentException("Scheduler is already registered.");
    }
    this.handler = handler;
    final ZonedDateTime now = ZonedDateTime.now();
    expression.nextExecution(now).ifPresent(next -> {
      executionTime = next;
      final long delay = getNextDelay(now);
      timerId = vertx.setTimer(delay, this);
    });
    return this;
  }

  @Override
  public void cancel() {
    if (vertx.cancelTimer(timerId)) {
      this.handler = null;
    }
  }

  @Override
  public final void handle(final Long id) {
    if (handler == null) {
      return;
    }
    final ZonedDateTime now = ZonedDateTime.now();
    expression.nextExecution(executionTime).ifPresent(next -> {
      executionTime = next;
      final long delay = getNextDelay(now);
      timerId = vertx.setTimer(delay + 20, this);
    });
    handler.handle(this);
  }

  private long getNextDelay(final ZonedDateTime time) {
    final Duration timeToNextExecution = Duration.between(time, executionTime);
    // If the program is paused for long enough (debugging) and a task is scheduled to run frequently,
    // the newly calculated execution time can be less than current time, that could cause negative value here.
    // With this solution the missed runs will happen in a burst after the pause until it catches up.
    // (Vertx does not allow to set a timer with delay < 1 ms)
    return Math.max(1, timeToNextExecution.toMillis());
  }
}
