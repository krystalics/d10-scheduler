package com.github.krystalics.d10.scheduler.start.sharding;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
public interface ShardService {

    /**
     * leader 进行shard
     */
    void shard() throws Exception;

    /**
     * follower 知道leader在shard后、进行以下动作
     * 1。将scheduler暂停
     * 2。变更负责的分片范围
     * 3。重新开始调度
     *
     * 这些动作节点需要和leader的的shard进行配合，比不是连贯的
     */
    void begin();

    void receiveShardResult(String result);

    void end();
}
