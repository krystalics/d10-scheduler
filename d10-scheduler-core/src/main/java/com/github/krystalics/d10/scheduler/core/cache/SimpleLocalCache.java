package com.github.krystalics.d10.scheduler.core.cache;

import com.github.krystalics.d10.scheduler.core.common.Constant;
import com.github.krystalics.d10.scheduler.core.common.JobInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author linjiabao001
 * @date 2021/10/25
 * @description
 */

public class SimpleLocalCache {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        CACHE.put(key, value);
    }

    public static Object get(String key) {
        return CACHE.get(key);
    }

    public static void addLiveNode(JobInstance instance) {
        addNode(Constant.LIVE_NODES, instance);
    }

    public static void removeLiveNode(JobInstance instance) {
        removeNode(Constant.LIVE_NODES, instance);
    }

    public static void addAllTypeNode(JobInstance instance) {
        addNode(Constant.ALL_NODES, instance);
    }

    public static void removeAllTypeNode(JobInstance instance) {
        removeNode(Constant.ALL_NODES, instance);
    }

    private static void addNode(String key, JobInstance instance) {
        final CopyOnWriteArraySet<JobInstance> set = (CopyOnWriteArraySet<JobInstance>) CACHE.getOrDefault(key, new CopyOnWriteArraySet<JobInstance>());
        set.add(instance);
        CACHE.put(key, set);
    }

    private static void removeNode(String key, JobInstance instance) {
        final CopyOnWriteArraySet<JobInstance> set = (CopyOnWriteArraySet<JobInstance>) CACHE.getOrDefault(key, new CopyOnWriteArraySet<JobInstance>());
        set.remove(instance);
        CACHE.put(key, set);
    }


    public static void setSelf(JobInstance instance) {
        CACHE.put(Constant.SELF_NODE, instance);
    }

    public static JobInstance getSelf() {
        return (JobInstance) CACHE.getOrDefault(Constant.SELF_NODE, new JobInstance());
    }

    public static void setLeader(JobInstance instance) {
        CACHE.put(Constant.LEADER_NODE, instance);
    }

    public static JobInstance getLeader() {
        return (JobInstance) CACHE.getOrDefault(Constant.LEADER_NODE, new JobInstance());
    }


    public static void setLost(boolean lost) {
        CACHE.put(Constant.LOST_STATE, lost);
    }

    public static boolean getLost() {
        return (boolean) CACHE.getOrDefault(Constant.LOST_STATE, false);
    }

    public static String getCluster() {
        return CACHE.toString();
    }
//    /**
//     * these will be ephemeral znodes
//     * 线程安全考虑使用copyOnWrite
//     */
//    private final Set<JobInstance> liveNodes = new CopyOnWriteArraySet<>();
//
//    /**
//     * these will be persistent znodes，
//     * 线程安全考虑使用copyOnWrite
//     */
//    private final Set<JobInstance> allNodes = new CopyOnWriteArraySet<>();
//
//    /**
//     * 自己的信息
//     */
//    private JobInstance self;
//
//    /**
//     * leader节点
//     */
//    private JobInstance leader;
//
//    /**
//     * 节点与zk的连接状态、用于失联时与scheduler.stop同时做一些阻断工作
//     */
//    private final AtomicBoolean lost = new AtomicBoolean(false);

    public void clear() {
        CACHE.clear();
    }

}
