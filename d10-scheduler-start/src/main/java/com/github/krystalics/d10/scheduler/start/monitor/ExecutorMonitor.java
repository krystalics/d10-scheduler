package com.github.krystalics.d10.scheduler.start.monitor;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.dao.entity.Node;
import com.github.krystalics.d10.scheduler.dao.mapper.NodeMapper;
import com.github.krystalics.d10.scheduler.dao.qm.NodeQM;
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
public class ExecutorMonitor {

    @Autowired
    private NodeMapper nodeMapper;

    /**
     * 每分钟check1次
     */
    @Scheduled(cron = "0 * * * * *")
    public void checkAlive() {
        final List<Node> nodes = nodeMapper.list(new NodeQM());

        DateTime now = DateTime.now();
        for (Node node : nodes) {
            final Date updateTime = node.getMtime();
            //如果超过5min、没有变更过。判断为死亡节点
            if (now.minusMinutes(CommonConstants.EXECUTOR_ALIVE_CHECK_LIMIT_TIME).isAfter(updateTime.getTime())) {
                node.setAlive(false);
                nodeMapper.update(node);
            }
        }

        //对所有alive=false的节点进行处理
        for (Node node : nodes) {
            //todo 报警给管理员
        }

    }
}
