package com.github.krystalics.d10.scheduler.common.constant;

public enum ScheduledEnum {
    /**
     * 例行调度
     */
    SCHEDULING("scheduling"),
    REDISPATCH("redispatch");

    /**
     * 调度路径的描述
     */
    private final String desc;

    ScheduledEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
