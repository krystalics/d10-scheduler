package com.github.krystalics.d10.scheduler.common.utils;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.VersionGranularity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author linjiabao001
 * @date 2021/10/1
 * @description 主要是Date和version number相互转换的地方
 */
public class DateUtils {
    private final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DATE_TIME_FORMAT);
    private final static DateTimeFormatter HOUR_VERSION_NO_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.HOUR_VERSION_NO_FORMAT);
    private final static DateTimeFormatter DAY_VERSION_NO_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DAY_VERSION_NO_FORMAT);

    private static DateTimeFormatter getDateTimeFormatter(int frequency) {
        VersionGranularity granularity = VersionGranularity.getGranularity(frequency);
        DateTimeFormatter date;
        if (granularity == VersionGranularity.HOUR) {
            date = HOUR_VERSION_NO_FORMATTER;
        } else {
            date = DAY_VERSION_NO_FORMATTER;
        }
        return date;
    }

    public static ZonedDateTime versionDateTime(String versionNo) {
        final LocalDateTime parse = LocalDateTime.parse(versionNo, DATE_TIME_FORMATTER);
        return ZonedDateTime.of(parse, ZoneId.of(CommonConstants.SYSTEM_TIME_ZONE));
    }

    public static String versionNo(ZonedDateTime dateTime, int frequency) {
        return getDateTimeFormatter(frequency).format(dateTime);
    }

}
