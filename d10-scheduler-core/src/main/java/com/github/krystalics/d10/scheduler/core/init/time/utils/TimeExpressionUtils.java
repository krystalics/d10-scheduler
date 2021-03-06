package com.github.krystalics.d10.scheduler.core.init.time.utils;

import com.github.krystalics.d10.scheduler.core.init.time.common.CommonConstants;
import com.github.krystalics.d10.scheduler.core.init.time.exception.ExpressionException;
import com.github.krystalics.d10.scheduler.core.init.time.exception.TimeCalculatorException;
import com.github.krystalics.d10.scheduler.core.init.time.model.TimeUnit;
import com.github.krystalics.d10.scheduler.core.init.time.model.Tuple;
import com.github.krystalics.d10.scheduler.core.init.time.preset.FridayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastFridayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastMondayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastMonthFirstDayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastMonthLastDayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastSaturdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastSundayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastThursdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastTuesdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.LastWednesdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.MondayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.MonthFirstDayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.MonthLastDayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.SaturdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.SundayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.ThursdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.TodayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.TomorrowCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.TuesdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.WednesdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.YesterdayCalculator;
import com.github.krystalics.d10.scheduler.core.init.time.preset.base.TimeParamCalculator;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * ?????????????????????
 */
public class TimeExpressionUtils {

    public static final Map<String, TimeParamCalculator> PRESET_PARMAS;

    static {

        PRESET_PARMAS = new HashMap<>();
        PRESET_PARMAS.put("today", new TodayCalculator());
        PRESET_PARMAS.put("yesterday", new YesterdayCalculator());
        PRESET_PARMAS.put("tomorrow", new TomorrowCalculator());
        /**
         * ?????????
         */
        PRESET_PARMAS.put("Monday", new MondayCalculator());
        PRESET_PARMAS.put("Tuesday", new TuesdayCalculator());
        PRESET_PARMAS.put("Wednesday", new WednesdayCalculator());
        PRESET_PARMAS.put("Thursday", new ThursdayCalculator());
        PRESET_PARMAS.put("Friday", new FridayCalculator());
        PRESET_PARMAS.put("Saturday", new SaturdayCalculator());
        PRESET_PARMAS.put("Sunday", new SundayCalculator());

        //??????
        PRESET_PARMAS.put("lastMonday", new LastMondayCalculator());
        PRESET_PARMAS.put("lastTuesday", new LastTuesdayCalculator());
        PRESET_PARMAS.put("lastWednesday", new LastWednesdayCalculator());
        PRESET_PARMAS.put("lastThursday", new LastThursdayCalculator());
        PRESET_PARMAS.put("lastFriday", new LastFridayCalculator());
        PRESET_PARMAS.put("lastSaturday", new LastSaturdayCalculator());
        PRESET_PARMAS.put("lastSunday", new LastSundayCalculator());

        /**
         * ???
         */
        PRESET_PARMAS.put("monthFirstDay", new MonthFirstDayCalculator());
        PRESET_PARMAS.put("monthLastDay", new MonthLastDayCalculator());

        //?????????
        PRESET_PARMAS.put("lastMonthFirstDay", new LastMonthFirstDayCalculator());
        PRESET_PARMAS.put("lastMonthLastDay", new LastMonthLastDayCalculator());

    }

    /**
     * ??????????????????????????????????????????????????????1d-><1,TimeUnit.DAY>
     * ????????????????????????
     *
     * @param expression ???????????????: ????????????+?????????????????? ???1d -1h???
     * @return <1,TimeUnit.DAY> ????????????????????????tuple
     * @see com.github.krystalics.d10.scheduler.core.init.time.model.TimeUnit
     */
    public static Tuple<Integer, TimeUnit> parseToNumAndTimeUnit(String expression) {

        TimeUnit[] timeUnits = TimeUnit.values();
        for (TimeUnit timeUnit : timeUnits) {
            if (StringUtils.contains(expression, timeUnit.getSuffix())) {
                int num = Integer.parseInt(StringUtils.replace(expression, timeUnit.getSuffix(), ""));
                return new Tuple<>(num, timeUnit);
            }
        }
        throw new ExpressionException("express error : " + expression + ", expression must consist of <number><timeUnit abbreviation>, such as 1d,-2h etc.");
    }

    /**
     * ?????????????????????????????????
     *
     * @param dateTime ????????????
     * @param n        ???????????????
     * @param timeUnit ????????????
     * @return ??????????????????
     */
    public static DateTime calculateTime(DateTime dateTime, int n, TimeUnit timeUnit) {
        DateTime addedDateTime = null;
        switch (timeUnit) {
            case SECOND:
                addedDateTime = dateTime.plusSeconds(n);
                break;
            case MINUTE:
                addedDateTime = dateTime.plusMinutes(n);
                break;
            case HOUR:
                addedDateTime = dateTime.plusHours(n);
                break;
            case DAY:
                addedDateTime = dateTime.plusDays(n);
                break;
            case WEEK:
                addedDateTime = dateTime.plusWeeks(n);
                break;
            case MONTH:
                addedDateTime = dateTime.plusMonths(n);
                break;
            case YEAR:
                addedDateTime = dateTime.plusYears(n);
                break;
            default:
                throw new TimeCalculatorException("Not support timeunit " + timeUnit);
        }

        return addedDateTime;
    }

    /**
     * ??????????????????????????????
     *
     * @param dateTime
     * @param pattern
     * @return
     */
    public static String formatDate(DateTime dateTime, String pattern) {
        return dateTime.toString(pattern);
    }

    /**
     * ??????????????????????????????????????????,???????????????
     *
     * @param expression ???????????????(Monday)???(yyyyMMdd,-1d)???(yyyyMMdd HH:mm:ss,Monday,-2d)
     * @param time       ????????????
     * @return ??????????????????
     */
    public static String calculateTimeExpression(String expression, String time) {

        String[] params = StringUtils.split(expression, CommonConstants.COMMA);

        if (params == null || params.length > 3 || params.length == 0) {
            throw new IllegalArgumentException("expression???" + expression + " error");
        }

        Date date = DateUtils.formatToDateTime(time);
        DateTime versionDateTime = new DateTime(date);

        //????????????????????????
        boolean isPreset = false;

        /**
         * ????????????3??????????????????????????????????????????
         * ????????????????????????????????????????????????????????????3
         */
        if (params.length == 3) {
            String dateFormat = StringUtils.trim(params[0]);
            String dateParam = StringUtils.trim(params[1]);
            String dateOperator = StringUtils.trim(params[2]);
            TimeParamCalculator calculator = PRESET_PARMAS.get(dateParam);
            if (calculator == null) {
                throw new IllegalArgumentException("expression error:" + expression);
            }
            DateTime calculatedDateTime = calculator.calculate(versionDateTime);
            Tuple<Integer, TimeUnit> timeUnitTuple = parseToNumAndTimeUnit(dateOperator);
            DateTime dateTime = calculateTime(calculatedDateTime, timeUnitTuple.getA(), timeUnitTuple.getB());
            return formatDate(dateTime, dateFormat);
        }
        /**
         * ??????????????????????????????
         */
        String dateParam = StringUtils.trim(params[0]);
        TimeParamCalculator calculator = PRESET_PARMAS.get(dateParam);
        if (calculator != null) {
            isPreset = true;
        }
        /**
         * ?????????2????????????????????????
         */
        if (params.length == 2) {
            /**
             * ?????????????????????????????????????????????
             */
            if (isPreset) {

                DateTime calculatedDateTime = calculator.calculate(versionDateTime);
                String dateOperator = StringUtils.trim(params[1]);
                Tuple<Integer, TimeUnit> timeUnitTuple = parseToNumAndTimeUnit(dateOperator);
                DateTime dateTime = calculateTime(calculatedDateTime, timeUnitTuple.getA(), timeUnitTuple.getB());
                return calculator.format(dateTime);


            } else {
                /**
                 * ??????????????????????????????????????????today=>20180101
                 */
                String dateOperator = StringUtils.trim(params[1]);
                TimeParamCalculator presetCal = PRESET_PARMAS.get(dateOperator);
                if (presetCal != null) {
                    DateTime dateTime = presetCal.calculate(versionDateTime);
                    return formatDate(dateTime, dateParam);
                } else {
                    Tuple<Integer, TimeUnit> timeUnitTuple = parseToNumAndTimeUnit(dateOperator);
                    DateTime dateTime = calculateTime(versionDateTime, timeUnitTuple.getA(), timeUnitTuple.getB());
                    return formatDate(dateTime, dateParam);
                }

            }
        }

        if (params.length == 1) {
            if (isPreset) {
                DateTime calculateTime = calculator.calculate(versionDateTime);
                return calculator.format(calculateTime);
            } else {
                return formatDate(versionDateTime, dateParam);
            }

        }
        return null;
    }

}
