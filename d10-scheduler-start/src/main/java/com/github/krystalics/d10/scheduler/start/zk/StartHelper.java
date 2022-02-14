package com.github.krystalics.d10.scheduler.start.zk;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.common.utils.RetryerUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.core.schedule.D10Scheduler;
import com.github.krystalics.d10.scheduler.start.event.EventThreadPool;
import com.github.krystalics.d10.scheduler.start.event.EventType;
import com.github.krystalics.d10.scheduler.start.event.EventWorker;
import com.github.krystalics.d10.scheduler.start.zk.listener.AllNodesChangeListener;
import com.github.krystalics.d10.scheduler.start.zk.listener.ConnectionStateChangeListener;
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
    private ConnectionStateChangeListener connectionStateChangeListener;

    @Autowired
    private ZookeeperHelper zookeeperHelper;

    @Value("${server.port}")
    private int port;

    @Autowired
    private JobInstance instance;

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

//        CuratorCache leaderChangeCache = CuratorCache.build(client, CommonConstants.ZK_LEADER);
//        leaderChangeCache.listenable().addListener(leaderChangeListener);

        CuratorCache shardCache = CuratorCache.build(client, CommonConstants.ZK_SHARD);
        CuratorCacheListener shardListener = CuratorCacheListener.builder()
                .forAll((type, oldNode, newNode) -> {
                    switch (type) {
                        case NODE_CREATED:
                            EventThreadPool.submit(new EventWorker(EventType.SHARD_BEGIN, new String(newNode.getData())));
                            break;
                        case NODE_CHANGED:
                            EventThreadPool.submit(new EventWorker(EventType.SHARD_CHANGE, new String(newNode.getData())));
                            break;
                        case NODE_DELETED:
                            EventThreadPool.submit(new EventWorker(EventType.SHARD_DEL, new String(oldNode.getData())));
                            break;
                        default:
                            throw new RuntimeException("unknown node event type " + type.name());
                    }
                }).afterInitialized().build();
        shardCache.listenable().addListener(shardListener);

        CuratorCache allNodesCache = CuratorCache.build(client, CommonConstants.ZK_ALL_NODES);
        CuratorCacheListener allNodesCacheListener = CuratorCacheListener.builder().afterInitialized().forPathChildrenCache(CommonConstants.ZK_ALL_NODES, client, allNodesChangeListener).build();
        allNodesCache.listenable().addListener(allNodesCacheListener);

        CuratorCache liveNodesCache = CuratorCache.build(client, CommonConstants.ZK_LIVE_NODES);
        CuratorCacheListener liveNodesCacheListener = CuratorCacheListener.builder().afterInitialized().forPathChildrenCache(CommonConstants.ZK_LIVE_NODES, client, liveNodesChangeListener).build();
        liveNodesCache.listenable().addListener(liveNodesCacheListener);

        client.start();
//        leaderChangeCache.start();
        shardCache.start();
        allNodesCache.start();
        liveNodesCache.start();
    }

    public void initZkPaths(String address) throws Exception {
        log.info("create zk init paths if need!");
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_LIVE_NODES, "cluster live ips", CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_ALL_NODES, "cluster all ips", CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_ALL_NODES + "/" + address, address, CreateMode.PERSISTENT);
        zookeeperHelper.createNodeIfNotExist(CommonConstants.ZK_LIVE_NODES + "/" + address, address, CreateMode.EPHEMERAL);
    }

    public void registerEventTypes() {
        EventThreadPool.register(EventType.LIVE_NODE_ADD, (param) -> {
            try {
                liveNodesChangeListener.add((String) param);
            } catch (Exception e) {
                log.error("live node add error: ", e);
            }
        });

        EventThreadPool.register(EventType.LIVE_NODE_DEL, (param) -> {
            try {
                liveNodesChangeListener.delete((String) param);
            } catch (Exception e) {
                log.error("live node delete error: ", e);
            }
        });

        EventThreadPool.register(EventType.CONNECTION_LOST, (param) -> {
            log.info("stop the scheduler because of {}", EventType.CONNECTION_LOST);
            D10Scheduler.getInstance().stop();
        });


        EventThreadPool.register(EventType.SHARD_BEGIN, (param) -> {
            log.info("stop the scheduler because of {}", EventType.SHARD_BEGIN);
            D10Scheduler.getInstance().stop();
            instance.setTaskIds(((JobInstance) param).getTaskIds());
            try {
                RetryerUtils.retryCall(() -> shardListener.ack(),false);
            } catch (Exception e) {
                log.error("shard ack error! ",e);
            }
        });

        EventThreadPool.register(EventType.SHARD_DEL, (param) -> {
            try {
                log.info("start the scheduler because of {}", EventType.SHARD_DEL);
                D10Scheduler.getInstance().start();
            } catch (InterruptedException e) {
                log.error("shard delete error ", e);
            }
        });
    }
}
