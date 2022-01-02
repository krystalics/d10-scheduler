package com.github.krystalics.d10.scheduler.registry.zk.listener;


import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.registry.service.impl.ZookeeperServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Configuration
@Slf4j
public class ElectionListener implements LeaderLatchListener {
    @Autowired
    private ZookeeperServiceImpl zookeeperService;

    @Value("${server.port:8080}")
    private int port;

    @Override
    public void isLeader() {
        try {
            String address = IPUtils.getHost() + ":" + port;
            log.info("i'm the leader,and my address is {}", address);
            zookeeperService.setData(CommonConstants.ZK_LEADER, address);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 如果失去Leader则回调此方法，必须设置LeaderLatch.CloseMode.NOTIFY_LEADER才会触发，否则不触发
     * 失去Leader的场景：自身close退出，和zk server的连接断开
     * 注意这是失去leader后的调用的，一开始就没选上leader的是不回调用这个方法的
     */
    @Override
    public void notLeader() {
        log.info("i'm not the leader like before");
    }


}
