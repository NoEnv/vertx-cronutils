package examples;

import com.noenv.cronutils.CronScheduler;
import io.vertx.core.Vertx;

/**
 * @author Lukas Prettenthaler
 */
public class CronSchedulerExamples {

  public void example1(Vertx vertx) {
    CronScheduler
      .create(vertx, "0/1 * * * * ?") //trigger every second
      .schedule(id ->
        System.out.println("timer "+id+" triggered")
      );
  }

  public void example2(Vertx vertx) {
    CronScheduler
      .create(vertx, "0 * * * * ?") //trigger every minute
      .schedule(s -> {
        s.cancel();
        System.out.println("timer triggered and canceled");
      });
  }
}
