package com.github.krystalics.d10.scheduler.core.utils;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
public class ClusterUtils {
    public static String address(Long taskId) {
        return ClusterInfo.getNode(taskId);
    }
}
