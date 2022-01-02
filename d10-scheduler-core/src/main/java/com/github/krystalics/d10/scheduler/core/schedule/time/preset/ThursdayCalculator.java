package com.github.krystalics.d10.scheduler.core.schedule.time.preset;


import com.github.krystalics.d10.scheduler.core.schedule.time.preset.base.TimeParamCalculator;
import com.github.krystalics.d10.scheduler.core.schedule.time.utils.DateUtils;
import org.joda.time.DateTime;

/**
 * Created by ly on 2018/9/19.
 */
public class ThursdayCalculator implements TimeParamCalculator {

    @Override
    public DateTime calculate(DateTime dateTime) {
        return dateTime.withDayOfWeek(4);
    }

    @Override
    public String format(DateTime dateTime) {
        return dateTime.toString(DateUtils.COMMON_DATE_FORMAT);
    }
}
