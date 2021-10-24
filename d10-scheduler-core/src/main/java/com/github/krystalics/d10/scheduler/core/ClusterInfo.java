package com.github.krystalics.d10.scheduler.core;

import com.github.krystalics.d10.scheduler.core.common.JobInstance;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ClusterInfo {

    /**
     * 系统全局唯一的静态变量，缓存集群信息
     * jvm保证在任何线程访问uniqueInstance静态变量之前一定先创建了此实例
     */
    private static final ClusterInfo CLUSTER_INFO = new ClusterInfo();

    public static ClusterInfo getClusterInfo() {
        return CLUSTER_INFO;
    }

    private ClusterInfo() {
    }

    /**
     * these will be ephemeral znodes、线程安全考虑
     */
    private final Set<JobInstance> liveNodes = new CopyOnWriteArraySet<>();

    /**
     * these will be persistent znodes，线程安全考虑
     */
    private final Set<JobInstance> allNodes = new CopyOnWriteArraySet<>();

    private JobInstance self;

    private JobInstance leader;

    private final AtomicBoolean lost = new AtomicBoolean(false);

    public static void setLiveNodes(Set<JobInstance> liveNodes) {
        CLUSTER_INFO.liveNodes.clear();
        CLUSTER_INFO.liveNodes.addAll(liveNodes);
    }

    public static void setAllNodes(Set<JobInstance> allNodes) {
        CLUSTER_INFO.allNodes.clear();
        CLUSTER_INFO.allNodes.addAll(allNodes);
    }

    public static String selfAddress() {
        return CLUSTER_INFO.self.getAddress();
    }

    public static String getLeader() {
        return CLUSTER_INFO.leader.getAddress();
    }

    public static Set<JobInstance> getLiveNodes() {
        return CLUSTER_INFO.liveNodes;
    }

    public static Set<JobInstance> getAllNodes() {
        return CLUSTER_INFO.allNodes;
    }

    public static void addToLiveNodes(JobInstance node) {
        CLUSTER_INFO.liveNodes.add(node);
    }

    public static void addToLiveNodes(List<JobInstance> nodes) {
        CLUSTER_INFO.liveNodes.addAll(nodes);
    }

    public static boolean isNewHere(JobInstance node) {
        return !CLUSTER_INFO.liveNodes.contains(node);
    }

    public static void addToAllNodes(JobInstance node) {
        CLUSTER_INFO.allNodes.add(node);
    }

    public static void removeFromLiveNodes(JobInstance node) {
        CLUSTER_INFO.liveNodes.remove(node);
    }

    public static void removeFromAllNodes(JobInstance node) {
        CLUSTER_INFO.allNodes.remove(node);
    }

    public static void setLost() {
        CLUSTER_INFO.lost.compareAndSet(false, true);
    }

    public static void connected() {
        CLUSTER_INFO.lost.compareAndSet(true, false);
    }

    /**
     * 并发中出现一些漏网之鱼 进入节点是可以接受的，概率比较小。
     *
     * @return
     */
    public static boolean getLostState() {
        return CLUSTER_INFO.lost.get();
    }

}
