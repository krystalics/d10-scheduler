package com.github.krystalics.d10.scheduler.executor.common;

import com.github.krystalics.d10.scheduler.rpc.utils.PropertyUtils;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description
 */
public class Constants {
    public static final int EXECUTOR_NETTY_SERVER_PORT = PropertyUtils.getInt("executor.netty.server.port", 12347);
}
