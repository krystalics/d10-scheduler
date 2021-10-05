package com.github.krystalics.d10.scheduler.core.zk;

import com.github.krystalics.d10.scheduler.core.common.Constant;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class StartupRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Autowired
    private CuratorFramework client;

    /**
     * 作为leader的话、会将自身的id或者 ip 写进 /election节点中
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        final LeaderLatch leaderLatch = new LeaderLatch(client, Constant.ZK_ELECTION);

        leaderLatch.addListener(new LeaderLatchListener() {
            @Override
            public void isLeader() {
                logger.info("i'm leader");
                try {
                    client.setData().forPath(Constant.ZK_ELECTION, "128".getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notLeader() {
                logger.info("i'm not leader");
            }
        });
        leaderLatch.start();


    }
}
