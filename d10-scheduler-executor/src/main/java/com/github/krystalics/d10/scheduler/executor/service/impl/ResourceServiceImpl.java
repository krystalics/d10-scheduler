package com.github.krystalics.d10.scheduler.executor.service.impl;

import com.github.krystalics.d10.scheduler.executor.common.Constants;
import com.github.krystalics.d10.scheduler.executor.service.ResourceService;
import com.github.krystalics.d10.scheduler.executor.utils.OSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author linjiabao001
 * @Date 2022/2/7
 * @Description
 */
@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {
    /**
     * 机器的资源限制直接写在代码中、没写在db的原因是不需要
     * 因为整个分发体系需要实时check机器的资源使用，所以在check的时候由本机自己进行判断即可
     * 不会存在其他节点访问的分布式锁的问题
     * <p>
     * 节点重启后，资源会重新进行初始化
     */
    private double memory = OSUtils.memoryLogicalMaxSize() * Constants.MEMORY_USAGE_LIMIT;
    private double cpu = OSUtils.cpuLogicalProcessorCount() * Constants.CPU_USAGE_LIMIT;


    /**
     * keypoint 目前只考虑内存，cpu属于可压缩资源暂时不考虑
     *
     * @param cpuApply    cpu
     * @param memoryApply memory
     * @return
     */
    @Override
    public synchronized boolean resourceApply(double cpuApply, double memoryApply) {
        double mv = memory - memoryApply;
        if (mv <= 0) {
            return false;
        }

        memory = mv;
        cpu = cpu - cpuApply;
        return true;
    }

    @Override
    public synchronized boolean resourceReturn(double cpuReturn, double memoryReturn) {
        cpu = cpu + cpuReturn;
        memory = memory + memoryReturn;
        return true;
    }
}
