package com.github.krystalics.d10.scheduler.core.cron;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.joda.time.DateTime;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.cronutils.model.CronType.QUARTZ;

/**
 * @author linjiabao001
 * @date 2021/10/1
 * @description UNIX:不包含秒和年的表达式
 * CRON4J:java版本的UNIX、表达式与之相同
 * QUARTZ:包含 秒 分 时 天 周 月 年等完整的时间周期概念
 * SPRING:也包含完整的周期概念，与quartz的书写方式可能略有差异
 * <p>
 * 本系统的crontab表达式选择的是：QUARTZ
 * 注意在表示周的时候 1 = 周日sunday
 * <p>
 * todo jdk 时间概念 zonedDatedTime 和 Joda 两个的融合
 */
public class CronUtils {

    private static final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(QUARTZ);
    private static final CronParser parser = new CronParser(cronDefinition);

    private String versionNo(DateTime dateTime, String crontab) {
        return "";
    }

    /**
     * @param dateTime 参照时间
     * @param crontab  目前crontab
     * @param flag     true为next,false为previous
     * @return 获得下次或者上次应该执行的时间
     */
    private static ZonedDateTime parse(ZonedDateTime dateTime, String crontab, boolean flag) {
        final Cron cron = parser.parse(crontab);
        cron.validate();
        ExecutionTime executionTime = ExecutionTime.forCron(cron);
        Optional<ZonedDateTime> execution;
        if (flag) {
            execution = executionTime.nextExecution(dateTime);
        } else {
            execution = executionTime.lastExecution(dateTime);
        }
        return execution.orElse(null);
    }

    public static ZonedDateTime nextExecutionDate(ZonedDateTime dateTime, String crontab) {
        return parse(dateTime, crontab, true);
    }

    public static ZonedDateTime prevExecutionDate(ZonedDateTime dateTime, String crontab) {
        return parse(dateTime, crontab, false);
    }

    public static List<ZonedDateTime> rangeExecutionDate(ZonedDateTime dateTime, String crontab, int offset, int cnt) {
        return null;
    }
}
