package com.github.krystalics.d10.scheduler.common.constant;

import java.time.ZoneId;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
public class CommonConstants {
    public static final String SCHEDULER_USER_TOKEN = "schedulerUserToken";

    public static final String DEFAULT_CHARSET="UTF-8";

    /**
     * 系统统一时区设置、不依赖于机器本身的时区设置
     */
    public final static String SYSTEM_TIME_ZONE = "Asia/Shanghai";
    public final static ZoneId TIMEZONE_ASIA_SHANGHAI = ZoneId.of(SYSTEM_TIME_ZONE);

    public final static String DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public final static String HOUR_VERSION_NO_FORMAT = "yyyyMMddHH0000";
    public final static String DAY_VERSION_NO_FORMAT = "yyyyMMdd000000";

    //============================== cache-key ===================================

    public final static String LIVE_NODES = "liveNodes";
    public final static String ALL_NODES = "allNodes";
    public final static String SELF_NODE = "self";
    public final static String LEADER_NODE = "leader";
    public final static String LOST_STATE = "lostState";


    //============================== zk ===================================

    public final static String ZK_ELECTION = "/election";
    /**
     * 用于集群节点确定leader是哪个
     */
    public final static String ZK_LEADER = "/election/leader";

    public final static String ZK_LIVE_NODES = "/live";

    public final static String ZK_ALL_NODES = "/all";

}
