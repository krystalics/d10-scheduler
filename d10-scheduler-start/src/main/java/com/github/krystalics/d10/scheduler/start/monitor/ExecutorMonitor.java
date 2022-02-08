package com.github.krystalics.d10.scheduler.start.monitor;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.dao.entity.Node;
import com.github.krystalics.d10.scheduler.dao.mapper.InstanceMapper;
import com.github.krystalics.d10.scheduler.dao.mapper.NodeMapper;
import com.github.krystalics.d10.scheduler.dao.qm.NodeQM;
import com.github.krystalics.d10.scheduler.rpc.api.INodeService;
import com.github.krystalics.d10.scheduler.rpc.api.ITaskRunnerService;
import com.github.krystalics.d10.scheduler.rpc.client.RpcClient;
import com.github.krystalics.d10.scheduler.rpc.utils.Host;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description 监控未执行定期收集系统信息的节点 5min为界限；超出5min则进行调用沟通
 */
@Component
@Slf4j
public class ExecutorMonitor {

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private InstanceMapper instanceMapper;

    /**
     * 每分钟check1次
     */
    @Scheduled(cron = "0 * * * * *")
    public void checkAlive() throws Exception {
        final List<Node> nodes = nodeMapper.list(new NodeQM());

        DateTime now = DateTime.now();
        for (Node node : nodes) {
            final Date updateTime = node.getMtime();
            //如果超过5min、没有变更过。判断为死亡节点
            if (now.minusMinutes(CommonConstants.EXECUTOR_ALIVE_CHECK_LIMIT_TIME).isAfter(updateTime.getTime())) {
                if (!checkNodeAlive(node.getNodeAddress())) {
                    node.setAlive(false);
                    nodeMapper.update(node);

                    //将死亡节点的instance更新为redispatch状态
                    instanceMapper.updateDownNodeInstances(node.getNodeAddress());
                }
            }
        }

        //对所有alive=false的节点进行处理
        for (Node node : nodes) {
            //todo 报警给管理员
        }

    }

    /**
     * 异常后3s再试一次，重复3次
     *
     * @param address 探测的地址
     * @return true=存活有响应
     * @throws Exception error
     */
    private boolean checkNodeAlive(String address) throws Exception {
        boolean alive = false;
        for (int i = 0; i < CommonConstants.COMMON_RETRY_TIMES; i++) {
            try {
                Host host = new Host(address);
                RpcClient client = new RpcClient();
                final INodeService nodeService = client.create(INodeService.class, host);
                alive = nodeService.isAlive();
                break;
            } catch (Exception e) {
                log.error("check {} alive error : {}", address, e);
                Thread.sleep(3000);
            }
        }

        return alive;
    }
}
