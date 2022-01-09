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
     * @return 调度器需要调度的任务的instanceId
     */
    List<Long> schedulerReadVersionInstance(@Param("left") long left, @Param("right") long right);
}
