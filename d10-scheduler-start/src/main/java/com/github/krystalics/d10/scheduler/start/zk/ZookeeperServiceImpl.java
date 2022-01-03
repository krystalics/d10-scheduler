package com.github.krystalics.d10.scheduler.start.zk;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.start.zk.listener.AllNodesChangeListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.LeaderChangeListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.LiveNodesChangeListener;
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
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Service
@Slf4j
public class ZookeeperServiceImpl  {

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

    @Value("${server.port}")
    private int port;

    @Bean
    public LeaderLatch leaderLatch() {
        String address = IPUtils.getHost() + ":" + port;
        return new LeaderLatch(client, CommonConstants.ZK_ELECTION, address, LeaderLatch.CloseMode.NOTIFY_LEADER);
    }

    /**
     * zookeeper 不适合类似于mysql的查询，这个只作为 项目启动时
     * rebalance的凭证，因为这时候 clusterInfo中的信息还不是完整的
     * @return
     * @throws Exception
     */
    public List<String> liveNodes() throws Exception {
        return client.getChildren().forPath(CommonConstants.ZK_LIVE_NODES);
    }

    public void setData(String path,String data) throws Exception {
        client.setData().forPath(path, data.getBytes(StandardCharsets.UTF_8));
    }

    public void createNodeIfNotExist(String path, String data, CreateMode mode) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            return;
        }
        client.create().creatingParentsIfNeeded()
                .withMode(mode)
                .forPath(path, data.getBytes(StandardCharsets.UTF_8));
    }

    public void deleteNode(String path) throws Exception {
        client.delete().forPath(path);
    }



    /**
     * curator cache中的 afterInitialized 可以让listener在节点初始化完后再加入 监听，
     * <p>
     * 没加它之前：之前存在的node、都是会触发对应的 child_add事件
     * 这里加了afterInitialized，之前存在的节点对它来说不影响。避免触发对应的事件
     */
    public void initCuratorCaches() {
        log.info("create listeners for nodes changed!");

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

        leaderChangeCache.start();
        shardCache.start();
        allNodesCache.start();
        liveNodesCache.start();
    }

    public void initZkPaths(String address) throws Exception {
        log.info("create zk init paths if need!");
        createNodeIfNotExist(CommonConstants.ZK_LEADER, "leader ip", CreateMode.PERSISTENT);
        createNodeIfNotExist(CommonConstants.ZK_LIVE_NODES, "cluster live ips", CreateMode.PERSISTENT);
        createNodeIfNotExist(CommonConstants.ZK_ALL_NODES, "cluster all ips", CreateMode.PERSISTENT);
        createNodeIfNotExist(CommonConstants.ZK_ALL_NODES + "/" + address, address, CreateMode.PERSISTENT);
        //在live中为临时节点
        createNodeIfNotExist(CommonConstants.ZK_LIVE_NODES + "/" + address, address, CreateMode.EPHEMERAL);
    }

}
