package com.github.krystalics.d10.scheduler.core.init.time;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author linjiabao001
 * @date 2021/1/28
 * @description
 */
public class TimeCalculatorTest {
    @Test
    public void parameterReplace() {

        String monday = "${time:Monday}";
        String mondayMinusOneHour = "${time:yyyyMMdd HH:mm:ss,Monday,-1h}";
        String lastFriday = "${time:lastFriday}";
        String monthFirstDay = "${time:monthFirstDay}";
        String minusOne = "${time:yyyyMMdd,-1d}";
        String monthFirstDay5 = "${time:monthFirstDay,5d}";

        final String s = ParamExpressionUtils.handleTimeExpression(monday, "2020-11-05 00:00:00");
        Assertions.assertEquals("2020-11-02", s);

        final String mmo = ParamExpressionUtils.handleTimeExpression(mondayMinusOneHour, "2020-11-05 00:00:00");
        Assertions.assertEquals("20201101 23:00:00", mmo);

        final String s1 = ParamExpressionUtils.handleTimeExpression(lastFriday, "2020-11-05 00:00:00");
        Assertions.assertEquals("2020-10-30", s1);

        final String s2 = ParamExpressionUtils.handleTimeExpression(monthFirstDay, "2020-11-05 00:00:00");
        Assertions.assertEquals("2020-11-01", s2);

        final String s3 = ParamExpressionUtils.handleTimeExpression(minusOne, "2020-11-05 00:00:00");
        Assertions.assertEquals("20201104", s3);

        final String s4 = ParamExpressionUtils.handleTimeExpression(monthFirstDay5, "2020-11-05 00:00:00");
        Assertions.assertEquals("2020-11-06", s4);
    }
}
