package com.github.krystalics.d10.scheduler.start.sharding.impl;

import com.alibaba.fastjson.JSON;
import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.common.utils.JSONUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.core.schedule.D10Scheduler;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import com.github.krystalics.d10.scheduler.start.sharding.ShardService;
import com.github.krystalics.d10.scheduler.start.sharding.ShardingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Service
@Slf4j
public class ShardServiceImpl implements ShardService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ZookeeperHelper zookeeperService;

    @Autowired
    private JobInstance jobInstance;

    @Value("${server.port}")
    private int port;

    private final Lock lock = new ReentrantLock();

    private final Condition begin = lock.newCondition();
    private final Condition ack = lock.newCondition();
    private final Condition end = lock.newCondition();
    /**
     * sequence
     * = 1 接受shard
     * = 2 接受ack
     * = 3 接受end
     */
    private int sequence = 1;


    /**
     * 将现有的live节点与shard节点下的子节点进行对比
     * 完全匹配后说明所有节点都完成了这个过程
     *
     * @throws Exception error
     */
    @Override
    public void shard() throws Exception {
        log.info("1.clear the before node,and create /shard node:scheduler system begin shard!");

        zookeeperService.deleteIfExists(CommonConstants.ZK_SHARD);
        zookeeperService.createNodeIfNotExist(CommonConstants.ZK_SHARD, "shard-result", CreateMode.PERSISTENT);
        List<JobInstance> jobInstances = splitTasks();
        zookeeperService.setData(CommonConstants.ZK_SHARD, JSONUtils.toJSONStringWithoutCircleDetect(jobInstances));

        log.info("2.wait to receive all live node response!");
        while (true) {
            try {
                //防止网络抖动，访问zk出异常 然后导致check结束
                if (!Thread.currentThread().isInterrupted()) {
                    final List<String> liveNodes = zookeeperService.liveNodes();
                    final List<String> children = zookeeperService.getChildren(CommonConstants.ZK_SHARD);
                    log.info("check live node and accept the shard result's node. live nodes ={},ack nodes={}", liveNodes, children);
                    if (liveNodes.containsAll(children) && children.containsAll(liveNodes)) {
                        break;
                    }
                    Thread.sleep(CommonConstants.SHARD_ACK_WAITING);
                }
            } catch (Exception e) {
            }
        }

        log.info("3.delete the /shard node & restart the scheduler system.");
        zookeeperService.deleteChildrenAndParent(CommonConstants.ZK_SHARD);
        //todo check all nodes start?
    }

    /**
     * 使用shard策略将任务分片，然后将分片结果写入 /shard 节点中
     *
     * @throws Exception error
     */
    public List<JobInstance> splitTasks() throws Exception {
        final List<String> liveNodes = zookeeperService.liveNodes();
        List<JobInstance> jobInstances = new ArrayList<>();
        for (String liveNode : liveNodes) {
            JobInstance instance = new JobInstance();
            instance.setAddress(liveNode);
            jobInstances.add(instance);
        }
        TaskQM taskQM = new TaskQM();
        taskQM.setState(2);
        final int taskSize = taskMapper.count(taskQM);

        ShardingStrategy shardingStrategy = new ScopeStrategy();
        return shardingStrategy.sharding(jobInstances, taskSize);
    }


    /**
     * 1。通知停止调度，2。分片变更，3。重新调度
     * - 事件的异步执行，顺序混乱影响比较大。===> 使用lock condition机制进行顺序化
     * - 多重shard时容易出现重复的
     */
    @Override
    public void begin() {
        lock.lock();
        try {
            while (sequence != 1) {
                begin.await();
            }
            D10Scheduler.getInstance().stop();
            sequence = 2;
            ack.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void receiveShardResult(String result) {
        lock.lock();
        try {
            while (sequence != 2) {
                ack.await();
            }

            log.info("sharding result is {}", result);
            final List<JobInstance> jobInstances = JSON.parseArray(result, JobInstance.class);
            for (JobInstance instance : jobInstances) {
                if (instance.getAddress().equals(jobInstance.getAddress())) {
                    jobInstance.setTaskIds(instance.getTaskIds());
                    if (D10Scheduler.getInstance().checkStop()) {
                        reportShardChange();
                    } else {
                        log.error("scheduler don't stop yet! try to stop by hand after 1000 ms!");
                        Thread.sleep(1000);
                        D10Scheduler.getInstance().stop();
                        if (!D10Scheduler.getInstance().checkStop()) {
                            log.error("stop scheduler failed! system error, exit !!!!!");
                            System.exit(1);
                        } else {
                            reportShardChange();
                        }
                    }

                    break;
                }
            }

            sequence = 3;
            end.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void reportShardChange() throws Exception {
        log.info("get new scope,{}", jobInstance);
        String address = IPUtils.getHost() + ":" + port;
        zookeeperService.createNodeWithoutParent(CommonConstants.ZK_SHARD + "/" + address, address, CreateMode.EPHEMERAL);
    }

    @Override
    public void end() {
        lock.lock();
        try {
            while (sequence != 3) {
                end.await();
            }

            D10Scheduler.getInstance().start();

            //重新开始进行下一轮的shard
            sequence = 1;
            begin.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
