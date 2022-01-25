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
     * 2.等待，等待并法度
     * 3.等待，等待资源
     * 4.排队，准备分发(可以用于分发的状态)
     * 5.运行
     * ...
     * 9.可用于重新分发，executor容错的状态
     */
    INIT(1),
    WAITING_CON(2),
    WAITING_RES(3),
    PENDING(4),
    RUNNING(5),
    SUCCESS(6),
    FAILED(7),
    KILLED(8),
    ReDispatch(9);
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
