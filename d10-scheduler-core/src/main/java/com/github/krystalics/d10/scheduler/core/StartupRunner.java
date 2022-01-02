package com.github.krystalics.d10.scheduler.core;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.core.schedule.DistributedScheduler;
import com.github.krystalics.d10.scheduler.registry.service.impl.RebalanceServiceImpl;
import com.github.krystalics.d10.scheduler.registry.service.impl.ZookeeperServiceImpl;
import com.github.krystalics.d10.scheduler.registry.zk.listener.AllNodesChangeListener;
import com.github.krystalics.d10.scheduler.registry.zk.listener.ElectionListener;
import com.github.krystalics.d10.scheduler.registry.zk.listener.LeaderChangeListener;
import com.github.krystalics.d10.scheduler.registry.zk.listener.LiveNodesChangeListener;
import com.github.krystalics.d10.scheduler.registry.zk.listener.ShardListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    private ElectionListener electionListener;

    @Autowired
    private ZookeeperServiceImpl zookeeperService;

    @Autowired
    private RebalanceServiceImpl rebalanceService;

    @Autowired
    private LeaderLatch leaderLatch;

    @Autowired
    private DistributedScheduler distributedScheduler;

    /**
     * 作为leader的话、会将自身的id或者 ip 写进 /election节点中
     * 阻塞至成为新的leader后会进行 shard 重新分片的工作
     * 再然后进行实例化的工作
     * 不过不成为leader也不应该影响服务的正常启动，所以会额外放在一个线程去执行leader的选举
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                String address = IPUtils.getHost() + ":" + port;
                log.info("init action begin! this node address is {}", address);
                zookeeperService.initZkPaths(address);
                zookeeperService.initCuratorCaches();
                log.info("try to be a leader!");
                leaderLatch.addListener(electionListener);
                leaderLatch.start();

                leaderLatch.await();
                rebalanceService.rebalance(address);

//                distributedScheduler.init();
            }
        }, "election").start();
    }



}
