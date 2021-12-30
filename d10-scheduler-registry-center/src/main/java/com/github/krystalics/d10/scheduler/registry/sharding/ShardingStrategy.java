package com.github.krystalics.d10.scheduler.registry.sharding;

import com.github.krystalics.d10.scheduler.core.common.JobInstance;

import java.util.List;
import java.util.Map;

/**
 * @author linjiabao001
 * @date 2021/10/19
 * @description
 */
public interface ShardingStrategy {

    /**
     *
     * @param jobInstances 所有线上live的节点
     * @param shardingTotalCount 任务总数，需要进行分片的count
     * @return
     */
    Map<JobInstance, List<Integer>> sharding(List<JobInstance> jobInstances, int shardingTotalCount);

}
