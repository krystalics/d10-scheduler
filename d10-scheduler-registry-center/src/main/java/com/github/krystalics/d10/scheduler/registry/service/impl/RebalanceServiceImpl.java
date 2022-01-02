package com.github.krystalics.d10.scheduler.registry.service.impl;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.registry.service.RebalanceService;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
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
public class RebalanceServiceImpl implements RebalanceService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ZookeeperServiceImpl zookeeperService;

    @Override
    public void rebalance(String address) throws Exception {
        log.info("scheduler system begin rebalanced!");
        log.info("1.to create /shard node");
        zookeeperService.createNodeIfNotExist(CommonConstants.ZK_SHARD_NODE, address, CreateMode.EPHEMERAL);
        log.info("2.assign the task to schedulers");

        final List<String> liveNodes = zookeeperService.liveNodes();
        log.info("rebalancing ,all live nodes are {}", liveNodes);
        TaskQM taskQM = new TaskQM();
        taskQM.setState(2);
        final int taskSize = taskMapper.count(taskQM);
        final int nodeSize = liveNodes.size();

        int step = 0;
        final int stepLong = taskSize / nodeSize;

        for (String liveNode : liveNodes) {
            step += stepLong;
//            ClusterInfo.setNodeRange(liveNode, step);
        }

        log.info("3.delete the /shard node");
        zookeeperService.deleteNode(CommonConstants.ZK_SHARD_NODE);

    }

    public void shard(String address) throws Exception {

    }

}
