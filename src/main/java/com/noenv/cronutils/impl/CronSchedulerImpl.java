package com.noenv.cronutils.impl;

import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.noenv.cronutils.CronScheduler;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.time.Duration;
import java.time.ZoneId;
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
  private final ZoneId zoneId;
  private Handler<CronScheduler> handler;
  private long timerId;
  private ZonedDateTime executionTime;

  public CronSchedulerImpl(final Vertx vertx, final String cronExpression, final CronDefinition definition, final ZoneId zoneId) {
    this.vertx = vertx;
    this.timerId = -1L;
    this.zoneId = zoneId;

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
    scheduleNextTimer(0L);
    return this;
  }

  @Override
  public void cancel() {
    if (vertx.cancelTimer(timerId)) {
      this.handler = null;
    }
    timerId = -1L;
  }

  @Override
  public final void handle(final Long id) {
    timerId = -1L;
    if (handler == null) {
      return;
    }
    scheduleNextTimer(20L);
    handler.handle(this);
  }

  @Override
  public boolean active() {
    return timerId >= 0L;
  }

  @Override
  public String zoneId() {
    return zoneId.getId();
  }

  private void scheduleNextTimer(final long addMilliseconds) {
    final ZonedDateTime now = ZonedDateTime.now(zoneId);
    if(executionTime == null) {
      executionTime = now;
    }
    expression.nextExecution(executionTime).ifPresent(next -> {
      executionTime = next;
      final long delay = getNextDelay(now);
      timerId = vertx.setTimer(delay + addMilliseconds, this);
    });
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
