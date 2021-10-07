package com.github.krystalics.d10.scheduler.core.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
public class LeaderLatchImpl extends LeaderLatch {

    public LeaderLatchImpl(CuratorFramework client, String latchPath) {
        super(client, latchPath);
    }

    public LeaderLatchImpl(CuratorFramework client, String latchPath, String id) {
        super(client, latchPath, id);
    }

    public LeaderLatchImpl(CuratorFramework client, String latchPath, String id, CloseMode closeMode) {
        super(client, latchPath, id, closeMode);
    }


}
