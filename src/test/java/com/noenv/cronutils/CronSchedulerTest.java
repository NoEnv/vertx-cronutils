package com.noenv.cronutils;

import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronConstraintsFactory;
import com.cronutils.model.definition.CronDefinitionBuilder;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.time.YearMonth;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CronSchedulerTest extends VertxTestBase {
  @Test
  public void testCronExpression() throws IllegalArgumentException, InterruptedException {
    final CountDownLatch latch = new CountDownLatch(3);
    CronScheduler scheduler = CronScheduler
      .create(vertx, "0/1 * * * * ?", CronType.QUARTZ)
      .schedule(s -> latch.countDown());
    latch.await(5, TimeUnit.SECONDS);
    assertTrue(scheduler.active());
    scheduler.cancel();
    assertEquals(0, latch.getCount());
  }

  @Test(expected=NullPointerException.class)
  public void testNoHandler() throws IllegalArgumentException {
    CronScheduler
      .create(vertx, "0 * * * * ?")
      .schedule(null);
  }

  @Test(expected=NullPointerException.class)
  public void testNoExpression() throws IllegalArgumentException {
    CronScheduler
      .create(vertx, null);
  }

  @Test(expected=IllegalArgumentException.class)
  public void testFaultyExpression() throws IllegalArgumentException {
    CronScheduler
      .create(vertx, "broken expression");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testDoubleRegistration() throws IllegalArgumentException {
    CronScheduler
      .create(vertx, "0/1 * * * * ?")
      .schedule(s -> System.out.println("register once"))
      .schedule(s -> System.out.println("register twice"))
      .cancel();
  }

  @Test
  public void testCronCancel() throws IllegalArgumentException, InterruptedException {
    final CountDownLatch latch = new CountDownLatch(1);
    CronScheduler scheduler = CronScheduler
      .create(vertx, "0/3 * * * * ?", CronType.QUARTZ)
      .schedule(s -> latch.countDown());
    MILLISECONDS.sleep(100L);
    assertTrue(scheduler.active());
    scheduler.cancel();
    latch.await(4, TimeUnit.SECONDS);
    assertEquals(1, latch.getCount());
    assertFalse(scheduler.active());
  }

  @Test
  public void testCronFromThePast() throws IllegalArgumentException, InterruptedException {
    CronScheduler scheduler = CronScheduler
      .create(vertx, "0 * * * * ? 1980", CronType.QUARTZ)
      .schedule(s -> {
        //do nothing
      });
    MILLISECONDS.sleep(100L);
    assertFalse(scheduler.active());
  }

  @Test
  public void testCronNotScheduled() throws IllegalArgumentException, InterruptedException {
    CronScheduler scheduler = CronScheduler
      .create(vertx, "0 0 * * * ?", CronType.QUARTZ);
    MILLISECONDS.sleep(100L);
    assertFalse(scheduler.active());
  }

  @Test
  public void testCronCustomDefinition() throws IllegalArgumentException, InterruptedException {
    final CountDownLatch latch = new CountDownLatch(3);
    final int maxYear = YearMonth.now().getYear() + 1000;
    CronScheduler scheduler = CronScheduler
      .create(vertx, "0/1 * * * * ? 2000-" + maxYear, CronDefinitionBuilder.defineCron()
        .withSeconds().withValidRange(0, 59).and()
        .withMinutes().withValidRange(0, 59).and()
        .withHours().withValidRange(0, 23).and()
        .withDayOfMonth().withValidRange(1, 31).supportsL().supportsW().supportsLW().supportsQuestionMark().and()
        .withMonth().withValidRange(1, 12).and()
        .withDayOfWeek().withValidRange(1, 7).withMondayDoWValue(2).supportsHash().supportsL().supportsQuestionMark().and()
        .withYear().withValidRange(1970, maxYear).withStrictRange().optional().and()
        .withCronValidation(CronConstraintsFactory.ensureEitherDayOfWeekOrDayOfMonth())
        .instance())
      .schedule(s -> latch.countDown());
    latch.await(5, TimeUnit.SECONDS);
    assertTrue(scheduler.active());
    scheduler.cancel();
    assertEquals(0, latch.getCount());
  }
}
