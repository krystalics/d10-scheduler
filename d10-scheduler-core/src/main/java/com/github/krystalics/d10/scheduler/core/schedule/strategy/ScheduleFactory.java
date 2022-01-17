package com.github.krystalics.d10.scheduler.core.schedule.strategy;

import com.github.krystalics.d10.scheduler.core.schedule.check.SchedulerCheckHelper;
import com.github.krystalics.d10.scheduler.core.schedule.check.DependencyCheck;
import com.github.krystalics.d10.scheduler.core.schedule.check.ReDispatchCheck;
import com.github.krystalics.d10.scheduler.core.schedule.check.ResourceCheck;
import com.github.krystalics.d10.scheduler.core.schedule.check.RunningCheck;
import com.github.krystalics.d10.scheduler.core.schedule.check.RoutingSchedulingCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linjiabao001
 * @date 2022/1/11
 * @description 留下多任务调度策略的入口、由于目前想到的方案可能不是最优的，可一留一个扩展。后续可以灵活的使用另一种调度策略
 */
public class ScheduleFactory {

    private static final Logger log = LoggerFactory.getLogger(ScheduleFactory.class);

    private final static SchedulerCheckHelper timeCheck = new SchedulerCheckHelper(new RoutingSchedulingCheck(), 60000, "time-check");
    private final static SchedulerCheckHelper dependencyCheck = new SchedulerCheckHelper(new DependencyCheck(), 60000, "dependency-check");
    private final static SchedulerCheckHelper resourceCheck = new SchedulerCheckHelper(new ResourceCheck(), 60000, "resource-check");
    private final static SchedulerCheckHelper runningCheck = new SchedulerCheckHelper(new RunningCheck(), 30000, "running-check");
    private final static SchedulerCheckHelper redispatchCheck = new SchedulerCheckHelper(new ReDispatchCheck(), 5 * 60000, "redispatch-check");

    public static List<ScheduleStrategy> getScheduleList(Strategy strategy) {
        List<ScheduleStrategy> strategies = new ArrayList<>();
        switch (strategy) {
            case CHECK:
                log.info("use schedule strategy check!");
                strategies.add(timeCheck);
                strategies.add(dependencyCheck);
                strategies.add(resourceCheck);
                strategies.add(runningCheck);
                strategies.add(redispatchCheck);
                break;
            default:

        }
        return strategies;
    }


}
