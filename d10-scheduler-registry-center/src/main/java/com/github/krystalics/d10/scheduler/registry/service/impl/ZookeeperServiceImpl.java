package com.github.krystalics.d10.scheduler.registry.service.impl;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * zookeeper 不适合类似于mysql的查询，这个只作为 项目启动时
     * rebalance的凭证，因为这时候 clusterInfo中的信息还不是完整的
     * @return
     * @throws Exception
     */
    public List<String> liveNodes() throws Exception {
        return client.getChildren().forPath(CommonConstants.ZK_LIVE_NODES);
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

}
