package com.github.krystalics.d10.scheduler.core.common;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description 任务周期频率
 */
public enum FrequencyGranularity {
    /**
     * frequency: 1 = 小时,2 = 天,3 = 周,4 = 月,5 = 年
     */
    HOUR(1),
    DAY(2),
    WEEK(3),
    MONTH(4),
    YEAR(5);

    private final int value;

    FrequencyGranularity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static FrequencyGranularity getFrequencyGranularity(int value) {
        FrequencyGranularity[] array = values();
        for (FrequencyGranularity frequency : array) {
            if (frequency.getValue() == value) {
                return frequency;
            }
        }
        return null;

    }
}
