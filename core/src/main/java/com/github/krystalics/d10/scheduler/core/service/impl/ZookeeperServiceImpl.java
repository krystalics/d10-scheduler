package com.github.krystalics.d10.scheduler.core.service.impl;

import com.github.krystalics.d10.scheduler.core.common.Constant;
import com.github.krystalics.d10.scheduler.core.service.ZookeeperService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Service
@Slf4j
public class ZookeeperServiceImpl implements ZookeeperService {

    @Autowired
    private CuratorFramework client;

    /**
     * zookeeper 不适合类似于mysql的查询，这个只作为 项目启动时
     * rebalance的凭证，因为这时候 clusterInfo中的信息还不是完整的
     * @return
     * @throws Exception
     */
    @Override
    public List<String> liveNodes() throws Exception {
        return client.getChildren().forPath(Constant.ZK_LIVE_NODES);
    }
}
