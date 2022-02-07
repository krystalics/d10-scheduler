package com.github.krystalics.d10.scheduler.executor.utils;

import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.dao.entity.Node;
import com.github.krystalics.d10.scheduler.executor.common.Constants;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description
 */
public class ReportUtils {
    public static Node nodeInfo() {
        Node node = new Node();
        final String address = IPUtils.getHost() + ":" + Constants.EXECUTOR_NETTY_SERVER_PORT;
        node.setNodeAddress(address);
        node.setCpuUse(OSUtils.cpuUsage());
        node.setCpuCapacity(1.0 * OSUtils.cpuLogicalProcessorCount());
        node.setMemoryUse(OSUtils.memoryUsage());
        node.setMemoryCapacity(OSUtils.memoryLogicalMaxSize());
        final double[] loads = OSUtils.loadAverage();
        node.setAvgLoad1(loads[0]);
        node.setAvgLoad5(loads[1]);
        node.setAvgLoad15(loads[2]);
        node.setAlive(true);
        return node;
    }
}
