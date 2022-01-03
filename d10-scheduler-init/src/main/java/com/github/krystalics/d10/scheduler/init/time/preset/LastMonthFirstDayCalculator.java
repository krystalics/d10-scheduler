package com.github.krystalics.d10.scheduler.init.time.preset;



import com.github.krystalics.d10.scheduler.init.time.preset.base.TimeParamCalculator;
import com.github.krystalics.d10.scheduler.init.time.utils.DateUtils;
import org.joda.time.DateTime;

/**
 * @description: 上月第一天表达式获取
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class LastMonthFirstDayCalculator implements TimeParamCalculator {

    @Override
    public DateTime calculate(DateTime dateTime) {
        return dateTime.plusMonths(-1).dayOfMonth().withMinimumValue();
    }

    @Override
    public String format(DateTime dateTime) {
        return dateTime.toString(DateUtils.COMMON_DATE_FORMAT);
    }
}
