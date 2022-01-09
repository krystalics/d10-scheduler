package com.github.krystalics.d10.scheduler.start.sharding;



import com.github.krystalics.d10.scheduler.common.constant.JobInstance;

import java.util.List;

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
    List<JobInstance> sharding(List<JobInstance> jobInstances, int shardingTotalCount);

}
