package com.github.krystalics.d10.scheduler.core.init.time.model;

import lombok.Getter;

/**
 * 时间枚举
 */
@Getter
public enum TimeUnit {

    SECOND(1, "s", "秒"),

    MINUTE(2, "m", "分"),

    HOUR(3, "h", "时"),

    DAY(4, "d", "日"),

    WEEK(5, "w", "周"),

    MONTH(6, "M", "月"),

    YEAR(7, "y", "年");

    private int value;

    private String suffix;

    private String desc;

    TimeUnit(int value, String suffix, String desc) {
        this.value = value;
        this.suffix = suffix;
        this.desc = desc;
    }

    /**
     * 根据id
     * @return
     */
    public static TimeUnit getType(int id) {
        for (TimeUnit mediaType : TimeUnit.values()) {
            if (mediaType.getValue() == id) {
                return mediaType;
            }
        }
        return null;
    }

    /**
     * 根据value查询desc
     *
     * @param value     要查询的value值
     * @return
     */
    public static String findDescByValue(int value) {
        for (TimeUnit timeUnit : TimeUnit.values()) {
            if (timeUnit.getValue() == value) {
                return timeUnit.getDesc();
            }
        }
        return "";
    }
}
