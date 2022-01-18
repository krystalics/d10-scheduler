package com.github.krystalics.d10.scheduler.start.zk;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.start.zk.listener.AllNodesChangeListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.ConnectionStateChangeListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.LeaderChangeListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.LiveNodesChangeListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.ShardResultListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.ShardListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author linjiabao001
 * @date 2022/1/18
 * @description
 */
@Component
@Slf4j
public class StartHelper {
    @Autowired
    private CuratorFramework client;

    @Autowired
    private ShardListener shardListener;

    @Autowired
    private LeaderChangeListener leaderChangeListener;

    @Autowired
    private AllNodesChangeListener allNodesChangeListener;

    @Autowired
    private LiveNodesChangeListener liveNodesChangeListener;

    @Autowired
    private ShardResultListener shardResultListener;

    @Autowired
    private ConnectionStateChangeListener connectionStateChangeListener;

    @Autowired
    private ZookeeperHelper zookeeperHelper;

    @Value("${server.port}")
    private int port;

    @Bean
    public LeaderLatch leaderLatch() {
        String address = IPUtils.getHost() + ":" + port;
        return new LeaderLatch(client, CommonConstants.ZK_ELECTION, address, LeaderLatch.CloseMode.NOTIFY_LEADER);
    }



    /**
     * curator cache中的 afterInitialized 可以让listener在节点初始化完后再加入 监听，
     * <p>
     * 没加它之前：之前存在的node、都是会触发对应的 child_add事件
     * 这里加了afterInitialized，之前存在的节点对它来说不影响。避免触发对应的事件
     */
    public void initCuratorCaches() {
        log.info("create listeners for nodes changed!");
        client.getConnectionStateListenable().addListener(connectionStateChangeListener);

        CuratorCache leaderChangeCache = CuratorCache.build(client, CommonConstants.ZK_LEADER);
        leaderChangeCache.listenable().addListener(leaderChangeListener);

        CuratorCache shardCache = CuratorCache.build(client, CommonConstants.ZK_SHARD_NODE);
        shardCache.listenable().addListener(shardListener);

        CuratorCache allNodesCache = CuratorCache.build(client, CommonConstants.ZK_ALL_NODES);
        CuratorCacheListener allNodesCacheListener = CuratorCacheListener.builder().afterInitialized().forPathChildrenCache(CommonConstants.ZK_ALL_NODES, client, allNodesChangeListener).build();
        allNodesCache.listenable().addListener(allNodesCacheListener);

        CuratorCache liveNodesCache = CuratorCache.build(client, CommonConstants.ZK_LIVE_NODES);
        CuratorCacheListener liveNodesCacheListener = CuratorCacheListener.builder().afterInitialized().forPathChildrenCache(CommonConstants.ZK_LIVE_NODES, client, liveNodesChangeListener).build();
        liveNodesCache.listenable().addListener(liveNodesCacheListener);
        //由于shard的结果是写进/live节点，所以就绑定在这个cache下
        liveNodesCache.listenable().addListener(shardResultListener);

        client.start();
        leaderChangeCache.start();
        shardCache.start();
        allNodesCache.start();
        liveNodesCache.start();
    }

    public void initZkPaths(String address) throws Exception {
        log.info("create zk init paths if need!");
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_LEADER, "leader ip", CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_LIVE_NODES, "cluster live ips", CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_ALL_NODES, "cluster all ips", CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_ALL_NODES + "/" + address, address, CreateMode.PERSISTENT);
        //在live中为临时节点
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_LIVE_NODES + "/" + address, address, CreateMode.EPHEMERAL);
    }
}
