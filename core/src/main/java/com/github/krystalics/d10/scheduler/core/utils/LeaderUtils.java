package com.github.krystalics.d10.scheduler.core.utils;

import org.apache.curator.framework.recipes.leader.LeaderLatch;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
public class LeaderUtils {
    private static final LeaderLatch LEADER_LATCH = SpringUtils.getBean(LeaderLatch.class);

    public synchronized static boolean isLeader(){
        return LEADER_LATCH.hasLeadership();
    }

    public synchronized static boolean notLeader(){
        return !LEADER_LATCH.hasLeadership();
    }
}
