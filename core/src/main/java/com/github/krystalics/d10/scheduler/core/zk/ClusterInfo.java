package com.github.krystalics.d10.scheduler.core.zk;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    private final Set<String> liveNodes = new CopyOnWriteArraySet<>();

    /**
     * these will be persistent znodes，线程安全考虑
     */
    private final Set<String> allNodes = new CopyOnWriteArraySet<>();

    /**
     * node address -> task_id 上界
     */
    private final Map<String, Long> nodeTaskRange = new ConcurrentHashMap<>();

    private String master;

    private String selfAddress;

    private AtomicBoolean lost = new AtomicBoolean(false);

    public static void setSelf(String address) {
        CLUSTER_INFO.selfAddress = address;
    }

    public static void setMaster(String master) {
        CLUSTER_INFO.master = master;
    }

    public static void setLiveNodes(Set<String> liveNodes) {
        CLUSTER_INFO.liveNodes.clear();
        CLUSTER_INFO.liveNodes.addAll(liveNodes);
    }

    public static void setAllNodes(Set<String> allNodes) {
        CLUSTER_INFO.allNodes.clear();
        CLUSTER_INFO.allNodes.addAll(allNodes);
    }

    public static String getSelf() {
        return CLUSTER_INFO.selfAddress;
    }

    public static String getMaster() {
        return CLUSTER_INFO.master;
    }

    public static Set<String> getLiveNodes() {
        return CLUSTER_INFO.liveNodes;
    }

    public static Set<String> getAllNodes() {
        return CLUSTER_INFO.allNodes;
    }

    public static void addToLiveNodes(String node) {
        CLUSTER_INFO.liveNodes.add(node);
    }

    public static void addToLiveNodes(List<String> nodes) {
        CLUSTER_INFO.liveNodes.addAll(nodes);
    }

    public static boolean isNewHere(String node) {
        return !CLUSTER_INFO.liveNodes.contains(node);
    }

    public static void addToAllNodes(String node) {
        CLUSTER_INFO.allNodes.add(node);
    }

    public static void removeFromLiveNodes(String node) {
        CLUSTER_INFO.liveNodes.remove(node);
    }

    public static void removeFromAllNodes(String node) {
        CLUSTER_INFO.allNodes.remove(node);
    }

    public static void setNodeRange(String address, long upperLimit) {
        CLUSTER_INFO.nodeTaskRange.put(address, upperLimit);
    }

    public static long getNodeUpperLimit(String address) {
        return CLUSTER_INFO.nodeTaskRange.getOrDefault(address, -1L);
    }

    public static String getNode(long taskId) {
        final String[] node = {""};

        CLUSTER_INFO.nodeTaskRange.forEach((k, v) -> {
            if (taskId < v) {
                node[0] = k;
            }
        });

        return node[0];
    }

    public static void setLost() {
        CLUSTER_INFO.lost.compareAndSet(false, true);
    }

    public static void connected() {
        CLUSTER_INFO.lost.compareAndSet(true, false);
    }

    /**
     * 并发中出现一些漏网之鱼 进入节点是可以接受的，概率比较小。
     * @return
     */
    public static boolean getLostState(){
        return CLUSTER_INFO.lost.get();
    }

    @Override
    public String toString() {
        return "ClusterInfo{" +
                "liveNodes=" + liveNodes +
                ", allNodes=" + allNodes +
                ", nodeTaskRange=" + nodeTaskRange +
                ", master='" + master + '\'' +
                ", selfAddress='" + selfAddress + '\'' +
                ", lost='" + lost.get() + '\'' +
                '}';
    }
}
