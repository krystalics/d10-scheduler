package com.github.krystalics.d10.scheduler.start.sharding;

import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.rpc.base.Rpc;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
public interface IShardService {
    /**
     * 当节点发生变化，重新平衡各个节点上负责的taskId范围
     * leader节点开启shard
     *
     * @throws Exception error
     */
    void shard() throws Exception;

    /**
     * 每个节点都需要接收leader划分的分片结果
     *
     * @param instance 新的节点信息
     * @return 成功开启自身的shard后为true
     */


    /**
     * 停止shard
     */
    void stopShard();
}
