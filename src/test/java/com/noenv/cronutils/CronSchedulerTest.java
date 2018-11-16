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
}
