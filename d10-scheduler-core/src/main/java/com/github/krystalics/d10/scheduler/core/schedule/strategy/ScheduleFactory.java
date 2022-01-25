package com.github.krystalics.d10.scheduler.core.schedule.strategy;

import com.github.krystalics.d10.scheduler.core.schedule.check.SchedulerCheckHelper;
import com.github.krystalics.d10.scheduler.core.schedule.check.RedispatchScheduling;
import com.github.krystalics.d10.scheduler.core.schedule.check.RoutingScheduling;
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

    private final static SchedulerCheckHelper routingScheduling = new SchedulerCheckHelper(new RoutingScheduling(), 60000, "scheduling");
    private final static SchedulerCheckHelper redispatch = new SchedulerCheckHelper(new RedispatchScheduling(), 5 * 60000, "redispatch");

    public static List<ScheduleStrategy> getScheduleList(Strategy strategy) {
        List<ScheduleStrategy> strategies = new ArrayList<>();
        switch (strategy) {
            case CHECK:
                log.info("use schedule strategy check!");
                strategies.add(routingScheduling);
                strategies.add(redispatch);
                break;
            default:

        }
        return strategies;
    }


}
