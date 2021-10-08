package com.github.krystalics.d10.scheduler.core.zk.listener;

import com.github.krystalics.d10.scheduler.core.zk.ClusterInfo;
import com.github.krystalics.d10.scheduler.core.common.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CuratorFramework client;

    @Override
    public void isLeader() {
        log.info("i'm the leader");
        try {
            ClusterInfo.setMaster(ClusterInfo.getSelf());
            client.setData().forPath(Constant.ZK_LEADER, ClusterInfo.getSelf().getBytes(StandardCharsets.UTF_8));
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
