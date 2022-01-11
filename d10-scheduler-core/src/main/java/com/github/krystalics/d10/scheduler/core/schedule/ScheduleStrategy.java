package com.github.krystalics.d10.scheduler.core.schedule;

/**
 * @author linjiabao001
 * @date 2022/1/11
 * @description 多任务调度策略的接口
 */
public interface ScheduleStrategy {
    /**
     * 开始check
     */
    void start();

    /**
     * 停止check
     */
    void stop();
}
