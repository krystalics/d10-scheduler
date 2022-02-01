package com.github.krystalics.d10.scheduler.rpc.api;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.rpc.base.Rpc;

/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
public interface ITaskRunnerService {
    /**
     * 同步调用executor端
     *
     * @param instance 添加的任务
     * @return 添加成功则返回true
     */
    @Rpc
    boolean addInstance(VersionInstance instance);
}
