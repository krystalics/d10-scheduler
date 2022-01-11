package com.github.krystalics.d10.scheduler.core.schedule;

import java.util.List;

/**
 * @Author linjiabao001
 * @Date 2022/1/10 11:13
 * @Description 任务调度的入口
 */
public class D10Scheduler {
    private D10Scheduler() {
    }

    private final static D10Scheduler INSTANCE = new D10Scheduler();

    public static D10Scheduler getInstance() {
        return INSTANCE;
    }

    /**
     * keypoint 修改任务调度的策略，就修改下面的Strategy.CHECK参数
     */
    private final static List<ScheduleStrategy> SCHEDULE_LIST = ScheduleFactory.getScheduleList(Strategy.CHECK);

    public void start() {
        for (ScheduleStrategy scheduleStrategy : SCHEDULE_LIST) {
            scheduleStrategy.start();
        }
    }

    public void stop() {
        for (ScheduleStrategy scheduleStrategy : SCHEDULE_LIST) {
            scheduleStrategy.stop();
        }
    }

}
