package com.github.krystalics.d10.scheduler.executor.register;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.executor.register.listener.ExecutorAllNodesChangeListener;
import com.github.krystalics.d10.scheduler.executor.register.listener.ExecutorConnectionStateChangeListener;
import com.github.krystalics.d10.scheduler.executor.register.listener.ExecutorLiveNodesChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExecutorStartHelper {

    @Autowired
    private CuratorFramework client;

    @Autowired
    private ZookeeperHelper zookeeperHelper;

    @Autowired
    private ExecutorAllNodesChangeListener allNodesChangeListener;

    @Autowired
    private ExecutorLiveNodesChangeListener liveNodesChangeListener;

    @Autowired
    private ExecutorConnectionStateChangeListener connectionStateChangeListener;

    @Value("${server.port}")
    private String port;


    public void initZkPaths() throws Exception {
        log.info("create executor zk init paths if need!");
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_EXECUTOR_LIVE, "executor cluster live ips", CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_EXECUTOR_ALL, "executor cluster all ips", CreateMode.PERSISTENT);
        String address = IPUtils.getHost() + ":" + port;

        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_EXECUTOR_ALL + "/" + address, address, CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_EXECUTOR_LIVE + "/" + address, address, CreateMode.EPHEMERAL);
    }

    public void initCuratorCaches() {
        log.info("create executor listeners for nodes changed!");
        client.getConnectionStateListenable().addListener(connectionStateChangeListener);

        CuratorCache allNodesCache = CuratorCache.build(client, CommonConstants.ZK_EXECUTOR_ALL);
        CuratorCacheListener allNodesCacheListener = CuratorCacheListener.builder().afterInitialized().forPathChildrenCache(CommonConstants.ZK_EXECUTOR_ALL, client, allNodesChangeListener).build();
        allNodesCache.listenable().addListener(allNodesCacheListener);

        CuratorCache liveNodesCache = CuratorCache.build(client, CommonConstants.ZK_EXECUTOR_LIVE);
        CuratorCacheListener liveNodesCacheListener = CuratorCacheListener.builder().afterInitialized().forPathChildrenCache(CommonConstants.ZK_EXECUTOR_LIVE, client, liveNodesChangeListener).build();
        liveNodesCache.listenable().addListener(liveNodesCacheListener);

        client.start();
        allNodesCache.start();
        liveNodesCache.start();
    }

}