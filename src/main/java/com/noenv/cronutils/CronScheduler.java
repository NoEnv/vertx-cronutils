package com.noenv.cronutils;

import com.cronutils.model.CronType;
import com.noenv.cronutils.impl.CronSchedulerImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

/**
 * Creates CronScheduler instances.
 *
 * @author Lukas Prettenthaler
 */
@VertxGen
public interface CronScheduler {

  /**
   * Create a CronScheduler.
   *
   * @param vertx the Vert.x instance
   * @param cron  the cron expression
   * @return the instance
   */
  static CronScheduler create(Vertx vertx, String cron) {
    return new CronSchedulerImpl(vertx, cron, CronType.QUARTZ);
  }

  /**
   * Create a CronScheduler.
   *
   * @param vertx the Vert.x instance
   * @param cron  the cron expression
   * @param type  the cron type
   * @return the instance
   */
  static CronScheduler create(Vertx vertx, String cron, CronType type) {
    return new CronSchedulerImpl(vertx, cron, type);
  }

  /**
   * Register a handler and start the scheduler
   *
   * @param handler the handler will be provided with a reference to the scheduler
   */
  @Fluent
  CronScheduler schedule(Handler<CronScheduler> handler);

  void cancel();

  boolean active();
}
