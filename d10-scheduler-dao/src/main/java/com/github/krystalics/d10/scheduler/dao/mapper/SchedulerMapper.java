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
     * 更新实例
     */
    void updateInstance(VersionInstance instance);

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
