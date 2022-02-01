package com.github.krystalics.d10.scheduler.core.service;

import com.github.krystalics.d10.scheduler.common.constant.ScheduledEnum;
import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;

import java.util.Date;
import java.util.List;

public interface SchedulerService {
    /**
     * 获取调度运转 instances 的接口
     *
     * @param scheduledEnum 调度路径
     * @param now           调度日期
     * @return 需要调度的instance
     */
    List<VersionInstance> fetchData(ScheduledEnum scheduledEnum, Date now);

    /**
     * 分发任务到executor中执行
     *
     * @param instance instance具体的信息
     * @return 分发成功=true
     */
    boolean dispatch(VersionInstance instance) throws Exception;
}
