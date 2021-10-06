package com.github.krystalics.d10.scheduler.core.zk;

import com.github.krystalics.d10.scheduler.core.common.ClusterInfo;
import com.github.krystalics.d10.scheduler.core.common.Constant;
import com.github.krystalics.d10.scheduler.core.utils.IPUtils;
import com.github.krystalics.d10.scheduler.core.zk.listener.AllNodesChangeListener;
import com.github.krystalics.d10.scheduler.core.zk.listener.ElectionListener;
import com.github.krystalics.d10.scheduler.core.zk.listener.LeaderChangeListener;
import com.github.krystalics.d10.scheduler.core.zk.listener.LiveNodesChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author krysta
 */
@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    @Value("${server.port}")
    private int port;

    @Value("${schedule.ha.policy}")
    private String haPolicy;

    @Autowired
    private CuratorFramework client;

    @Autowired
    private ElectionListener electionListener;

    @Autowired
    private LeaderChangeListener leaderChangeListener;

    @Autowired
    private AllNodesChangeListener allNodesChangeListener;

    @Autowired
    private LiveNodesChangeListener liveNodesChangeListener;

    String address = "";

    /**
     * 作为leader的话、会将自身的id或者 ip 写进 /election节点中
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        address = IPUtils.getHost() + ":" + port;
        log.info("init action begin! this node address is {}",address);
        initZkPaths();
        initCuratorCaches();
        ClusterInfo.setSelf(address);
        log.info("try to be a leader!");
        final LeaderLatch leaderLatch = new LeaderLatch(client, Constant.ZK_ELECTION, address, LeaderLatch.CloseMode.NOTIFY_LEADER);
        leaderLatch.addListener(electionListener);
        leaderLatch.start();

        if(Constant.SCHEDULE_HA_POLICY_MASTER_SLAVE.equals(haPolicy)){
            /**
             * 阻塞至成为新的leader
             */
            System.out.println("master");
            leaderLatch.await();
            System.out.println("new leader");

        }else {
            System.out.println("multi");

        }


    }

    public void initZkPaths() throws Exception {
        log.info("create zk init paths if need!");
        createNode(Constant.ZK_LEADER, "leader ip", CreateMode.PERSISTENT);
        createNode(Constant.ZK_LIVE_NODES, "cluster live ips", CreateMode.PERSISTENT);
        createNode(Constant.ZK_ALL_NODES, "cluster all ips", CreateMode.PERSISTENT);

        createNode(Constant.ZK_ALL_NODES + "/" + address, address, CreateMode.PERSISTENT);

        //在live中为临时节点
        createNode(Constant.ZK_LIVE_NODES + "/" + address, address, CreateMode.EPHEMERAL);
    }

    public void initCuratorCaches() {
        log.info("create listeners for nodes changed!");

        CuratorCache leaderChangeCache = CuratorCache.build(client, Constant.ZK_LEADER);
        leaderChangeCache.listenable().addListener(leaderChangeListener);

        CuratorCache allNodesCache = CuratorCache.build(client, Constant.ZK_ALL_NODES);
        CuratorCacheListener allNodesCacheListener = CuratorCacheListener.builder().forPathChildrenCache(Constant.ZK_ALL_NODES, client, allNodesChangeListener).build();
        allNodesCache.listenable().addListener(allNodesCacheListener);

        CuratorCache liveNodesCache = CuratorCache.build(client, Constant.ZK_LIVE_NODES);
        CuratorCacheListener liveNodesCacheListener = CuratorCacheListener.builder().forPathChildrenCache(Constant.ZK_LIVE_NODES, client, liveNodesChangeListener).build();
        liveNodesCache.listenable().addListener(liveNodesCacheListener);

        leaderChangeCache.start();
        allNodesCache.start();
        liveNodesCache.start();
    }

    public void createNode(String path, String data, CreateMode mode) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            return;
        }
        client.create().creatingParentsIfNeeded()
                .withMode(mode)
                .forPath(path, data.getBytes(StandardCharsets.UTF_8));
    }


}
