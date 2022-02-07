package com.github.krystalics.d10.scheduler.executor.common;

import com.github.krystalics.d10.scheduler.rpc.utils.PropertyUtils;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description
 */
public class Constants {
    public static final int EXECUTOR_NETTY_SERVER_PORT = PropertyUtils.getInt("executor.netty.server.port", 12347);
    public static final double CPU_USAGE_LIMIT = 0.8;
    public static final double MEMORY_USAGE_LIMIT = 0.8;

    public static final String LOG_FILE_ADDRESS_PREFIX = "/tmp/executor/";
}
