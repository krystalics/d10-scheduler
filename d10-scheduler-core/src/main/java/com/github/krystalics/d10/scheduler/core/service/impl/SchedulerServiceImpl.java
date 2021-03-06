package com.github.krystalics.d10.scheduler.core.service.impl;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.common.constant.ScheduledEnum;
import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.core.load.balance.LoadBalancer;
import com.github.krystalics.d10.scheduler.core.load.balance.LoadBalancerFactory;
import com.github.krystalics.d10.scheduler.core.service.SchedulerService;
import com.github.krystalics.d10.scheduler.dao.entity.Node;
import com.github.krystalics.d10.scheduler.dao.mapper.NodeMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.SchedulerMapper;
import com.github.krystalics.d10.scheduler.dao.qm.NodeQM;
import com.github.krystalics.d10.scheduler.rpc.api.ITaskRunnerService;
import com.github.krystalics.d10.scheduler.rpc.client.RpcClient;
import com.github.krystalics.d10.scheduler.rpc.utils.Host;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author linjiabao001
 * @Date 2022/1/26
 * @Description 提供调度器需要的功能、例如取数，选节点等
 */
@Service
@Slf4j
public class SchedulerServiceImpl implements SchedulerService {
    @Autowired
    private SchedulerMapper schedulerMapper;

    @Autowired
    private JobInstance jobInstance;

    @Autowired
    private NodeMapper nodeMapper;

    @Override
    public List<VersionInstance> fetchData(ScheduledEnum scheduledEnum, Date now) {
        List<VersionInstance> list = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(now.getTime()), ZoneId.systemDefault());
        LocalDateTime startDateTime = localDateTime.minusDays(CommonConstants.SYSTEM_SCHEDULING_DATE_LIMIT).with(LocalTime.MAX);
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        final Pair<Long, Long> taskIdScope = jobInstance.getTaskIds();

        log.info("{} start! this scheduler's scope is {}", scheduledEnum.getDesc(), taskIdScope.toString());

        switch (scheduledEnum) {
            case SCHEDULING:
                list = schedulerMapper.routingSchedulingInstances(taskIdScope.getL(), taskIdScope.getR(), startDate);
                break;
            case REDISPATCH:
                list = schedulerMapper.redispatchSchedulingInstances(taskIdScope.getL(), taskIdScope.getR(), startDate);
                break;
            default:
        }

        //按照priority、biz priority 进行降序排列，保证优先级高的先调度
        Collections.sort(list);

        return list;
    }

    /**
     * 节点轮询选择，使用rpc进行分发任务
     * 如果被拒绝再试下一个
     *
     * @param instance instance具体的信息
     * @return
     * @throws Exception
     */
    @Override
    public boolean dispatch(VersionInstance instance) {
        NodeQM nodeQM = new NodeQM();
        nodeQM.setAlive(true);
        List<Node> nodes = nodeMapper.list(nodeQM);
        Collections.shuffle(nodes);

        for (Node node : nodes) {
            try {
                RpcClient client = new RpcClient();
                final ITaskRunnerService taskRunnerService = client.create(ITaskRunnerService.class, new Host(node.getNodeAddress()));
                boolean success = taskRunnerService.addInstance(instance);
                if (success) {
                    return true;
                }
            } catch (Exception e) {
                log.error("dispatch instance {} to {} error: {}", instance.getInstanceId(), node.getNodeAddress(), e);
            }
        }

        return false;
    }

}
