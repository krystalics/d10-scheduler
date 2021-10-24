package com.github.krystalics.d10.scheduler.common.common;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
public enum VersionState {
    /**
     * 版本生命周期、各个状态
     */
    INIT(1),
    WAITING(2),
    SCHEDULING(3),
    RUNNING(4),
    SUCCESS(5),
    FAILED(6),
    KILLED(7);
    private final int state;

    VersionState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public static VersionState fromInt(int id) {
        VersionState[] As = VersionState.values();
        for (int i = 0; i < As.length; i++) {
            if (As[i].getState() == id)
                return As[i];
        }
        return null;
    }

}
