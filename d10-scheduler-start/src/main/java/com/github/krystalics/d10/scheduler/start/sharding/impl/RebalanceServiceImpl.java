package com.github.krystalics.d10.scheduler.start.sharding.impl;

import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.core.schedule.D10Scheduler;
import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import com.github.krystalics.d10.scheduler.start.event.EventThreadPool;
import com.github.krystalics.d10.scheduler.start.event.EventType;
import com.github.krystalics.d10.scheduler.start.event.EventWorker;
import com.github.krystalics.d10.scheduler.start.sharding.IRebalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author linjiabao001
 * @date 2022/2/13
 * @description
 */

@RpcService("IRebalanceService")
@Service
@Slf4j
public class RebalanceServiceImpl implements IRebalanceService {
    @Override
    public boolean shardBegin(JobInstance instance) {
        log.info("receive shard begin request from leader!");
        EventThreadPool.submit(new EventWorker(EventType.SHARD_BEGIN, instance));
        return true;
    }

    /**
     * todo scheduler仅能启动一次
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean shardEnd() throws InterruptedException {
        D10Scheduler.getInstance().start();
        return true;
    }
}
