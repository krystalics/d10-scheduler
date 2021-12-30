package com.github.krystalics.d10.scheduler.common.constant;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
public enum TaskState {
    /**
     * 任务上线或者下线
     */
    ONLINE(1),
    OFFLINE(0);

    private final int state;

    TaskState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}