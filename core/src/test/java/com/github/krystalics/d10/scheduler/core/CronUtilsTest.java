package com.github.krystalics.d10.scheduler.core;

import com.cronutils.builder.CronBuilder;
import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.mapper.ConstantsMapper;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.field.value.SpecialChar;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;

import static com.cronutils.model.CronType.QUARTZ;
import static com.cronutils.model.field.expression.FieldExpressionFactory.always;
import static com.cronutils.model.field.expression.FieldExpressionFactory.between;
import static com.cronutils.model.field.expression.FieldExpressionFactory.on;
import static com.cronutils.model.field.expression.FieldExpressionFactory.questionMark;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author linjiabao001
 * @date 2021/10/1
 * @description
 */
public class CronUtilsTest {

    CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
    CronParser parser = new CronParser(cronDefinition);

    String crontab = "0 23 * ? * 1-5 *";


    @Test
    public void test() {
// Define your own cron: arbitrary fields are allowed and last field can be optional
// 自定义系统自己的 cron的表达式、一般用不到、用通用现成的就行了。
//        CronDefinition cronDefinition =
//                CronDefinitionBuilder.defineCron()
//                        .withSeconds().and()
//                        .withMinutes().and()
//                        .withHours().and()
//                        .withDayOfMonth()
//                        .supportsHash().supportsL().supportsW().and()
//                        .withMonth().and()
//                        .withDayOfWeek()
//                        .withIntMapping(7, 0) //we support non-standard non-zero-based numbers!
//                        .supportsHash().supportsL().supportsW().and()
//                        .withYear().optional().and()
//                        .instance();


        Cron cron = CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ))
                .withYear(always())
                .withDoM(between(SpecialChar.L, 3))
                .withMonth(always())
                .withDoW(questionMark())
                .withHour(always())
                .withMinute(always())
                .withSecond(on(0))
                .instance();
        // Obtain the string expression 0 * * L-3 * ? *
        String cronAsString = cron.asString();
        assertEquals("0 * * L-3 * ? *", cronAsString);
    }

    @Test
    public void parseDescribe() {
        // Create a parser based on provided definition
        Cron quartzCron = parser.parse(crontab);

        // Create a descriptor for a specific Locale
        CronDescriptor descriptor = CronDescriptor.instance(Locale.UK);
        String description = descriptor.describe(quartzCron);
        assertEquals("every hour at minute 23 every day between Sunday and Thursday", description);

    }

    @Test
    public void calculateTime() {
        // Get date for last execution
        ZonedDateTime now = ZonedDateTime.now();
        ExecutionTime executionTime = ExecutionTime.forCron(parser.parse(crontab));

        final Optional<ZonedDateTime> lastExecution = executionTime.lastExecution(now);
        System.out.println(lastExecution.get());
        // Get date for next execution
        final Optional<ZonedDateTime> nextExecution = executionTime.nextExecution(now);
        System.out.println(nextExecution.get());

        // Time from last execution
        final Optional<Duration> timeFromLastExecution = executionTime.timeFromLastExecution(now);
        System.out.println(timeFromLastExecution.get().toHours());

        // Time to next execution
        final Optional<Duration> timeToNextExecution = executionTime.timeToNextExecution(now);
        System.out.println(timeToNextExecution.get().toHours());

    }


}
