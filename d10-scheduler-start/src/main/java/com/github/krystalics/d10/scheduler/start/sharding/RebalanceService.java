package com.github.krystalics.d10.scheduler.start.sharding;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
public interface RebalanceService {
    /**
     * 当节点发生变化，重新平衡各个节点上负责的taskId范围
     * @param address 发生变化的节点
     * @throws Exception
     */
    void rebalance(String address) throws Exception;

    /**
     * 停止shard
     */
    void stop();
}
