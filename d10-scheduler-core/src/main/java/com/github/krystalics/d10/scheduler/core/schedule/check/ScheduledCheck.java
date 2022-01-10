package com.github.krystalics.d10.scheduler.core.schedule.check;

/**
 * @author linjiabao001
 * @date 2022/1/9
 * @description 用于任务调度的检测体系
 */
public interface ScheduledCheck {
    /**
     * 开始check
     */
    void start();

    /**
     * 停止check
     */
    void stop();
}
