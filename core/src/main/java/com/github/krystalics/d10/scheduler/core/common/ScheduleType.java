package com.github.krystalics.d10.scheduler.core.common;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */
public enum ScheduleType {
    /**
     * 1表示为时间触发
     * 2表示为依赖触发
     */
    TIME(1),
    TRIGGER(2);

    private final int value;

    ScheduleType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ScheduleType getScheduleType(int value) {
        ScheduleType[] array = values();
        for (ScheduleType type : array) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;

    }
}
