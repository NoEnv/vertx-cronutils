package examples;

import com.cronutils.builder.CronBuilder;
import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.noenv.cronutils.CronScheduler;
import io.vertx.core.Vertx;

import static com.cronutils.model.CronType.UNIX;
import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.between;

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

  public void example3(Vertx vertx) {
    Cron cronExpression = CronBuilder
      .cron(CronDefinitionBuilder.instanceDefinitionFor(UNIX))
      .withDoM(between(1, 3))
      .withMonth(always())
      .withDoW(always())
      .withHour(always())
      .withMinute(always()).instance();

    CronScheduler
      .create(vertx, cronExpression.asString())
      .schedule(s -> {
        System.out.println("timer triggered");
      });
  }
}
