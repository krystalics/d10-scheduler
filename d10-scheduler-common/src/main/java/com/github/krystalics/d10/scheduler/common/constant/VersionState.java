package com.github.krystalics.d10.scheduler.common.constant;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
public enum VersionState {
    /**
     * 版本实例生命周期、各个状态
     * 1.初始化，需要上游完成或者时间到达
     * 2.等待，等待资源
     * 3.排队，准备分发(可以用于分发的状态)
     * 4.运行
     * ...
     * 8.可用于重新分发，executor容错的状态
     */
    INIT(1),
    WAITING(2),
    PENDING(3),
    RUNNING(4),
    SUCCESS(5),
    FAILED(6),
    KILLED(7),
    ReDispatch(7);
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
            if (As[i].getState() == id) {
                return As[i];
            }
        }
        return null;
    }

}
