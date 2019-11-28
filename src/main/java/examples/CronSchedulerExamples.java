package examples;

import com.cronutils.model.CronType;
import com.noenv.cronutils.CronScheduler;
import io.vertx.core.Vertx;

/**
 * @author Lukas Prettenthaler
 */
public class CronSchedulerExamples {

  public void example1(Vertx vertx) {
    CronScheduler
      .create(vertx, "0/1 * * * * ?", CronType.QUARTZ) //trigger every second
      .schedule(s ->
        System.out.println("timer triggered")
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
