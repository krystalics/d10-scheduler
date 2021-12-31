package com.github.krystalics.d10.scheduler.common.utils;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.github.krystalics.d10.scheduler.common.exception.IllegalCntException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author linjiabao001
 * @date 2021/10/1
 * @description UNIX:不包含秒和年的表达式
 * CRON4J:java版本的UNIX、表达式与之相同
 * QUARTZ:包含 秒 分 时 天 周 月 年等完整的时间周期概念
 * SPRING:也包含完整的周期概念，与quartz的书写方式可能略有差异
 * <p>本系统的crontab表达式选择的是：QUARTZ
 * 注意在表示周的时候 1 = 周日sunday<p>
 * todo jdk 时间概念 zonedDatedTime 和 Joda 两个的融合
 */
public class CronUtils {

    private static final CronDefinition CRON_DEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.SPRING);
    private static final CronParser PARSER = new CronParser(CRON_DEFINITION);


    /**
     * @param dateTime 参照时间
     * @param crontab  目前crontab
     * @param flag     true为next,false为previous
     * @return 获得下次或者上次应该执行的时间
     */
    private static ZonedDateTime parse(ZonedDateTime dateTime, String crontab, boolean flag) {
        final Cron cron = PARSER.parse(crontab);
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

    /**
     * @param dateTime
     * @param crontab
     * @param offset   =0时 意味着next的开始、实际是1、
     * @return
     */
    private static List<ZonedDateTime> rangeExecutionDate(ZonedDateTime dateTime, String crontab, int offset) {
        List<ZonedDateTime> times = new ArrayList<>();
        int start = Math.abs(offset);

        if (offset < 0) {
            for (int i = 0; i < start; i++) {
                dateTime = prevExecutionDate(dateTime, crontab);
                times.add(dateTime);
            }
        } else {
            for (int i = 0; i <= start; i++) {
                dateTime = nextExecutionDate(dateTime, crontab);
                times.add(dateTime);
            }
        }

        return times;
    }

    /**
     * offset=-3 cnt=4 => 三个prev一个next
     * 以0为分界、分开执行; 下面-1是因为包含了0这个原点
     *
     * @param dateTime 参考时间
     * @param crontab 任务的周期参数
     * @param offset 依赖的原点
     * @param cnt 依赖的数量
     * @return 具体依赖任务的哪几个执行版本
     */
    public static List<ZonedDateTime> rangeExecutionDate(ZonedDateTime dateTime, String crontab, int offset, int cnt) {
        if (cnt <= 0) {
            throw new IllegalCntException("task rely's count [cnt] can't <= 0, now the [cnt] is " + cnt);
        }

        List<ZonedDateTime> dateTimes;
        ZonedDateTime origin = ZonedDateTime.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(), dateTime.getSecond(), dateTime.getNano(), dateTime.getZone());

        if (offset < 0) {
            dateTimes = rangeExecutionDate(dateTime, crontab, offset);
            if (offset + cnt > 0) {
                dateTimes.addAll(rangeExecutionDate(origin, crontab, cnt + offset - 1));
            } else {
                dateTimes = dateTimes.subList(dateTimes.size() - cnt, dateTimes.size());
            }
        } else if (offset == 0) {
            dateTimes = rangeExecutionDate(dateTime, crontab, offset);
        } else {
            dateTimes = rangeExecutionDate(dateTime, crontab, offset - 1);
            dateTimes = rangeExecutionDate(dateTimes.get(dateTimes.size() - 1), crontab, cnt - 1);
        }

        return dateTimes;
    }


}
