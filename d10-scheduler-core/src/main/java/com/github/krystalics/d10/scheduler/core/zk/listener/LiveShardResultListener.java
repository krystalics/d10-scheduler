package com.github.krystalics.d10.scheduler.core.zk.listener;

import com.alibaba.fastjson.JSON;
import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.core.schedule.D10Scheduler;
import com.github.krystalics.d10.scheduler.core.zk.ZookeeperServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author linjiabao001
 * @date 2022/1/3
 * @description shard后的结果存放为 /live 节点的数据。并且各个节点进行了shard变更后会有一个简单的ack机制
 * 告诉leader节点它们已经进行了范围的变更
 * 这种ack机制需要确保上一轮的scheduler停止了，否则就会造成那种两个调度器上运行了重复的任务。
 */
@Component
@Slf4j
public class LiveShardResultListener implements CuratorCacheListener {

    @Autowired
    private ZookeeperServiceImpl zookeeperService;

    @Autowired
    private JobInstance jobInstance;

    @Value("${server.port}")
    private int port;


    @SneakyThrows
    @Override
    public void event(Type type, ChildData before, ChildData after) {
        switch (type) {
            case NODE_CREATED:
                //
                break;
            case NODE_CHANGED:
                final String afterData = new String(after.getData());
                log.info("sharding result is {}", afterData);
                final List<JobInstance> jobInstances = JSON.parseArray(afterData, JobInstance.class);
                for (JobInstance instance : jobInstances) {
                    if (instance.getAddress().equals(jobInstance.getAddress())) {
                        jobInstance.setTaskIds(instance.getTaskIds());
                        if (D10Scheduler.getInstance().checkStop()) {
                            log.info("check scheduler stopped!");
                        } else {
                            log.error("scheduler don't stop yet! try to stop by hand after 1000 ms!");
                            Thread.sleep(1000);
                            D10Scheduler.getInstance().stop();
                            if (D10Scheduler.getInstance().checkStop()) {
                                log.error("stop scheduler failed! system error, exit !!!!!");
                                System.exit(1);
                            } else {
                                log.info("get new scope,{}", jobInstance);
                                String address = IPUtils.getHost() + ":" + port;
                                zookeeperService.createNodeIfNotExist(CommonConstants.ZK_SHARD_NODE + "/" + address, address, CreateMode.EPHEMERAL);
                            }
                        }

                        break;
                    }
                }
                break;
            case NODE_DELETED:
                //
                break;
            default:
                throw new RuntimeException("unknown node event type " + type.name());
        }
    }

}
