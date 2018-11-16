package com.noenv.cronutils;

import io.vertx.test.core.VertxTestBase;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CronSchedulerTest extends VertxTestBase {
  @Test
  public void testCronExpression() throws IllegalArgumentException, InterruptedException {
    final CountDownLatch latch = new CountDownLatch(3);
    CronScheduler scheduler = CronScheduler
      .create(vertx, "0/1 * * * * ?")
      .schedule(s -> latch.countDown());
    latch.await(5, TimeUnit.SECONDS);
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
}
