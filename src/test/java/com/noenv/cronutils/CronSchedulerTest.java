package com.noenv.cronutils;

import com.cronutils.model.CronType;
import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

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
}
