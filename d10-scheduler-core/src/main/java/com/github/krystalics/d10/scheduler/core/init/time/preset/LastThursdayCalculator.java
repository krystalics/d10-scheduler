package com.github.krystalics.d10.scheduler.core.init.time.preset;


import com.github.krystalics.d10.scheduler.core.init.time.preset.base.TimeParamCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.utils.DateUtils;
import org.joda.time.DateTime;

/**
 * @description: 上周四表达式获取
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class LastThursdayCalculator implements TimeParamCalculator {

    @Override
    public DateTime calculate(DateTime dateTime) {
        return dateTime.plusDays(-dateTime.getDayOfWeek()).withDayOfWeek(4);
    }

    @Override
    public String format(DateTime dateTime) {
        return dateTime.toString(DateUtils.COMMON_DATE_FORMAT);
    }
}
