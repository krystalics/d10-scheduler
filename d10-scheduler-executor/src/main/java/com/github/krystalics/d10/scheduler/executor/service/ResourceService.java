package com.github.krystalics.d10.scheduler.executor.service;

import org.springframework.stereotype.Service;

/**
 * @Author linjiabao001
 * @Date 2022/2/7
 * @Description
 */
public interface ResourceService {
    /**
     * 在机器的资源池上申请部分的资源
     *
     * @param cpuApply    cpu
     * @param memoryApply memory
     * @return 资源成功更新后 返回true
     */
    boolean resourceApply(double cpuApply, double memoryApply);

    /**
     *
     * @param cpuApply
     * @param memoryApply
     * @return
     */
    boolean resourceReturn(double cpuApply, double memoryApply);
}
