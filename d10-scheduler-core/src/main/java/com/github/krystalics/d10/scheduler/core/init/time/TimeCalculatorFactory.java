package com.github.krystalics.d10.scheduler.core.init.time;


import com.github.krystalics.d10.scheduler.core.init.time.calculator.DayTimeCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.calculator.HourTimeCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.calculator.MinuteTimeCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.calculator.MonthTimeCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.calculator.TimeCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.calculator.WeekTimeCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.calculator.YearTimeCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.exception.TimeCalculatorException;
import com.github.krystalics.d10.scheduler.core.init.time.model.TimeUnit;

import java.util.Date;

/**
 * @description: 时间计算器的工厂
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class TimeCalculatorFactory {

    /**
     * 获取时间计算器
     *
     * @param timeUnit
     * @return
     */
    public static TimeCalculator getCalculator(TimeUnit timeUnit) {
        TimeCalculator timeCalculator;
        switch (timeUnit) {
            case MINUTE:
                timeCalculator = new MinuteTimeCalculator();
                break;
            case HOUR:
                timeCalculator = new HourTimeCalculator();
                break;
            case DAY:
                timeCalculator = new DayTimeCalculator();
                break;
            case WEEK:
                timeCalculator = new WeekTimeCalculator();
                break;
            case MONTH:
                timeCalculator = new MonthTimeCalculator();
                break;
            case YEAR:
                timeCalculator = new YearTimeCalculator();
                break;
            default:
                throw new TimeCalculatorException("No such timeunit : " + timeUnit.toString());
        }

        return timeCalculator;
    }

    /**
     * 获取时间计算器
     *
     * @param timeUnit
     * @param time
     * @return
     */
    public static TimeCalculator getCalculator(TimeUnit timeUnit, Date time) {
        TimeCalculator timeCalculator;

        switch (timeUnit) {
            case MINUTE:
                timeCalculator = new MinuteTimeCalculator(time);
                break;
            case HOUR:
                timeCalculator = new HourTimeCalculator(time);
                break;
            case DAY:
                timeCalculator = new DayTimeCalculator(time);
                break;
            case WEEK:
                timeCalculator = new WeekTimeCalculator(time);
                break;
            case MONTH:
                timeCalculator = new MonthTimeCalculator(time);
                break;
            case YEAR:
                timeCalculator = new YearTimeCalculator(time);
                break;
            default:
                throw new TimeCalculatorException("No such timeunit : " + timeUnit.toString());
        }

        return timeCalculator;
    }

}
