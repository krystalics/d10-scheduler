package com.github.krystalics.d10.scheduler.resource.manager;

public interface ResourceScheduler {
    /**
     * 资源分配的接口、默认开启资源争抢模式 scramble=true
     * 参数说明 -> resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply, boolean scramble)
     */
    String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply) throws Exception;


    /**
     * @param instanceId  申请资源的任务
     * @param queueName   预设想要申请资源的队列
     * @param cpuApply    需要的cpu
     * @param memoryApply 需要的内存
     * @param scramble    true开启争抢模式，false则不争抢
     * @return 资源来源的真正队列、因为争抢模式，可能出资源的不是原来预定的队列。如果返回""说明没有获得资源
     */
    String resourceAllocator(long instanceId, String queueName, double cpuApply, double memoryApply, boolean scramble) throws Exception;


}
