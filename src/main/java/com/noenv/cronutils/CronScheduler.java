package com.noenv.cronutils;

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
   * @param cron  the cron excpression
   * @return the instance
   */
  static CronScheduler create(Vertx vertx, String cron) {
    return new CronSchedulerImpl(vertx, cron);
  }

  /**
   * Register a handler and start the scheduler
   *
   * @param handler the handler will be provided with a reference to the scheduler
   */
  @Fluent
  CronScheduler schedule(Handler<CronScheduler> handler);

  /**
   * Cancel the scheduler
   */
  void cancel();
}
