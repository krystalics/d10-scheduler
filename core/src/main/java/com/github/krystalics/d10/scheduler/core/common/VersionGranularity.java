package com.github.krystalics.d10.scheduler.core.common;

/**
 * @author krysta
 * 与任务的周期挂钩。最低到小时版本，分钟级别的有些频繁了。表示任务在周期内具体在哪一天(或者小时)、所以天就是最大的粒度了
 * @see FrequencyGranularity
 * frequency = 1 ---> hour
 * frequency > 1 ---> day
 */

public enum VersionGranularity {
    /**
     * 版本粒度
     */
    HOUR(1),
    DAY(2);

    private final int value;

    VersionGranularity(int value) {
        this.value = value;
    }

    public static VersionGranularity getGranularity(int value) {
        if (value > 1) {
            return DAY;
        } else {
            return HOUR;
        }
    }

}
