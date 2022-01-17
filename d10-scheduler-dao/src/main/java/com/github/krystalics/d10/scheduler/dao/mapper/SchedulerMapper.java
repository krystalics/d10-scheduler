package com.github.krystalics.d10.scheduler.dao.mapper;

import com.github.krystalics.d10.scheduler.dao.biz.VersionInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author linjiabao001
 * @date 2022/1/3
 * @description
 */
@Mapper
public interface SchedulerMapper {
    /**
     * 调度器从db中获取 时间触发且理论执行时间小于现在的版本实例
     *
     * @param left  该调度器负责的分片范围下界
     * @param right 该调度器负责的分片范围上界
     * @return 调度器需要调度的任务的instance
     */
    List<VersionInstance> readCouldTimeTriggered(@Param("left") long left, @Param("right") long right);

    /**
     * 获取可以调度的任务实例: state=3 进行dispatch
     * state=7 redispatch
     *
     * @param left  该调度器负责的分片范围下界
     * @param right 该调度器负责的分片范围上界
     * @param state 需要获取的instance状态
     * @return 调度器需要调度的任务的instance实例
     */
    List<VersionInstance> readCouldScheduledTasks(@Param("left") long left, @Param("right") long right, @Param("state") int state);

    /**
     * 获取需要进行状态check的instanceId:
     * state=1 进行依赖检查
     * state=2 进行资源检查
     * state=6 进行重试次数的检查
     *
     * @param left  该调度器负责的分片范围下界
     * @param right 该调度器负责的分片范围上界
     * @param state 需要获取的instance状态
     * @return 调度器需要调度的任务的instance
     */
    List<VersionInstance> readNeedPreparedTasks(@Param("left") long left, @Param("right") long right, @Param("state") int state);

    /**
     * 批量更新版本实例的状态
     *
     * @param instances 实例列表
     */
    void batchUpdateState(List<VersionInstance> instances);

    /**
     * 将状态更新
     *
     * @param instanceId 实例Id
     */
    void updateState(@Param("state") int state, @Param("instanceId") long instanceId);

    /**
     * 检查该实例的所有上游是否都成功了
     *
     * @return 0表示都成功了、其他数字表示未成功的个数
     */
    int checkUpInstancesAreSuccess(@Param("instanceId") long instanceId);

    /**
     * 获取例行调度的所有任务
     *
     * @param left  该调度器负责的分片范围下界
     * @param right 该调度器负责的分片范围上界
     * @return 所有的instance实例
     */
    List<VersionInstance> routingSchedulingInstances(@Param("left") long left, @Param("right") long right);
}
