package com.github.krystalics.d10.scheduler.core.schedule;

import com.github.krystalics.d10.scheduler.core.schedule.check.ResourceCheck;
import com.github.krystalics.d10.scheduler.core.schedule.check.RunningCheck;
import com.github.krystalics.d10.scheduler.core.schedule.check.ScheduledCheck;
import com.github.krystalics.d10.scheduler.core.schedule.check.TimeTriggerCheck;

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

    private final D10SchedulerHelper timeCheck = new D10SchedulerHelper(new TimeTriggerCheck(), 60000, "time-check");
    private final D10SchedulerHelper dependencyCheck = new D10SchedulerHelper(new TimeTriggerCheck(), 60000, "dependency-check");
    private final D10SchedulerHelper resourceCheck = new D10SchedulerHelper(new ResourceCheck(), 60000, "resource-check");
    private final D10SchedulerHelper runningCheck = new D10SchedulerHelper(new RunningCheck(), 30000, "running-check");
    private final D10SchedulerHelper redispatchCheck = new D10SchedulerHelper(new RunningCheck(), 5 * 60000, "redispatch-check");


    public void start() {
        timeCheck.start();
        dependencyCheck.start();
        resourceCheck.start();
        runningCheck.start();
        redispatchCheck.start();
    }

    public void stop() {
        timeCheck.stop();
        dependencyCheck.stop();
        resourceCheck.stop();
        runningCheck.stop();
        redispatchCheck.stop();
    }

}
