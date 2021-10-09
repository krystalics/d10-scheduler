package com.github.krystalics.d10.scheduler.core.common;

import java.time.ZoneId;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */
public class Constant {
    /**
     * 系统高可用的策略选择
     */
    public final static String SCHEDULE_HA_POLICY_MASTER_SLAVE = "master-slave";
    public final static String SCHEDULE_HA_POLICY_MULTI_LIVE = "multi-live";


    /**
     * 系统统一时区设置、不依赖于机器本身的时区设置
     */
    public final static String SYSTEM_TIME_ZONE = "Asia/Shanghai";
    public final static ZoneId TIMEZONE_ASIA_SHANGHAI = ZoneId.of(SYSTEM_TIME_ZONE);

    public final static String DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public final static String HOUR_VERSION_NO_FORMAT = "yyyyMMddHH0000";
    public final static String DAY_VERSION_NO_FORMAT = "yyyyMMdd000000";


    //============================== zk ===================================

    public final static String ZK_ELECTION = "/election";
    /**
     * 用于集群节点确定leader是哪个
     */
    public final static String ZK_LEADER = "/election/leader";

    public final static String ZK_LIVE_NODES = "/live";

    public final static String ZK_ALL_NODES = "/all";


}
