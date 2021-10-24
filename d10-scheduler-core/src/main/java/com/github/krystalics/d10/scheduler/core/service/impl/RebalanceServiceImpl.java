package com.github.krystalics.d10.scheduler.core.service.impl;

import com.github.krystalics.d10.scheduler.core.ClusterInfo;
import com.github.krystalics.d10.scheduler.core.service.RebalanceService;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import lombok.extern.slf4j.Slf4j;
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
    public void rebalance() throws Exception {
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
            ClusterInfo.setNodeRange(liveNode, step);
        }

    }
}
