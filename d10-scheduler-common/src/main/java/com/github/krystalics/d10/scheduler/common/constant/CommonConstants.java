package com.github.krystalics.d10.scheduler.common.constant;

import java.time.ZoneId;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
public class CommonConstants {
    public static final String SCHEDULER_USER_TOKEN = "schedulerUserToken";

    public static final String SCHEDULING_THREAD_NAME = "scheduling";
    public static final String RE_DISPATCH_THREAD_NAME = "redispatch";

    /**
     * 在实践中系统中超过3天还未运行到终态的实例就不管了,属于垃圾数据
     */
    public static final int SYSTEM_SCHEDULING_DATE_LIMIT = 3;

    public static final String DEFAULT_CHARSET = "UTF-8";

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

    public final static String ZK_SHARD_NODE = "/shard";

    public final static String ZK_SHARD_RESULT_NODE = "/shard-result";

    public final static int REBALANCED_TRY_TIMES = 3;

    public final static String LOCK_INIT = "/lock-init";

    /**
     * 1000条一个分段，进入线程池分开执行
     */
    public final static int INSTANCE_PER_PARTITION = 1000;

    public final static int SHARD_ACK_WAITING = 1000;

}
