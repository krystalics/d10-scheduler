package com.github.krystalics.d10.scheduler.common.constant;

public enum TaskType {
    Shell(1,"ShellExecutor");

    TaskType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private final int type;
    private final String desc;

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskType fromInt(int id) {
        TaskType[] tt = TaskType.values();
        for (int i = 0; i < tt.length; i++) {
            if (tt[i].getType() == id) {
                return tt[i];
            }
        }
        return null;
    }
}
