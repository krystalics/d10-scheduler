package com.github.krystalics.d10.scheduler.start;

import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.core.init.Initiation;
import com.github.krystalics.d10.scheduler.start.sharding.JobInstance;
import com.github.krystalics.d10.scheduler.start.sharding.RebalanceServiceImpl;
import com.github.krystalics.d10.scheduler.start.zk.ZookeeperServiceImpl;
import com.github.krystalics.d10.scheduler.start.zk.listener.ElectionListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * @author linjiabao001
 * @date 2021/10/5
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.github.krystalics.d10.scheduler"})
public class ScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }

    @Component
    @Slf4j
    static class Startup implements CommandLineRunner {

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
        private Initiation initiation;

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

//                  initiation.init();
                }
            }, "election").start();
        }

        @Bean
        public JobInstance jobInstance() {
            String address = IPUtils.getHost() + ":" + port;
            final JobInstance instance = new JobInstance();
            instance.setAddress(address);
            instance.setTaskIds(Pair.of(0L, 0L));
            return instance;
        }

    }


}
