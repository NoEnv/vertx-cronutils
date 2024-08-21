package com.noenv.cronutils;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.noenv.cronutils.impl.CronSchedulerImpl;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;

import java.time.ZoneId;

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
    final CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
    return new CronSchedulerImpl(vertx, cron, definition, ZoneId.systemDefault());
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
    final CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(type);
    return new CronSchedulerImpl(vertx, cron, definition, ZoneId.systemDefault());
  }

  /**
   * Create a CronScheduler.
   *
   * @param vertx  the Vert.x instance
   * @param cron   the cron expression
   * @param zoneId the timezone id
   * @return the instance
   */
  static CronScheduler create(Vertx vertx, String cron, String zoneId) {
    final CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
    return new CronSchedulerImpl(vertx, cron, definition, ZoneId.of(zoneId));
  }

  /**
   * Create a CronScheduler.
   *
   * @param vertx  the Vert.x instance
   * @param cron   the cron expression
   * @param type   the cron type
   * @param zoneId the timezone id
   * @return the instance
   */
  static CronScheduler create(Vertx vertx, String cron, CronType type, String zoneId) {
    final CronDefinition definition = CronDefinitionBuilder.instanceDefinitionFor(type);
    return new CronSchedulerImpl(vertx, cron, definition, ZoneId.of(zoneId));
  }

  /**
   * Create a CronScheduler.
   *
   * @param vertx       the Vert.x instance
   * @param cron        the cron expression
   * @param definition  the cron definition
   * @return the instance
   */
  @GenIgnore
  static CronScheduler create(Vertx vertx, String cron, CronDefinition definition, ZoneId zoneId) {
    return new CronSchedulerImpl(vertx, cron, definition, zoneId);
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

  String zoneId();
}
