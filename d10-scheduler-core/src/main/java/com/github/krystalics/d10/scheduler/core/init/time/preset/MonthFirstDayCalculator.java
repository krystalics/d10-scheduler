package com.github.krystalics.d10.scheduler.core.init.time.preset;

;
import com.github.krystalics.d10.scheduler.core.init.time.preset.base.TimeParamCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.utils.DateUtils;
import org.joda.time.DateTime;

/**
 * @description: 本月第一天表达式获取
 * @author: yueyunyue
 * @create: 2018-07-15
 **/
public class MonthFirstDayCalculator implements TimeParamCalculator {

    @Override
    public DateTime calculate(DateTime dateTime) {
        return dateTime.dayOfMonth().withMinimumValue();
    }

    @Override
    public String format(DateTime dateTime) {
        return dateTime.toString(DateUtils.COMMON_DATE_FORMAT);
    }
}
