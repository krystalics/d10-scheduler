package com.github.krystalics.d10.scheduler.start.sharding.impl;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.utils.JSONUtils;
import com.github.krystalics.d10.scheduler.common.utils.RetryerUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.core.schedule.D10Scheduler;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import com.github.krystalics.d10.scheduler.start.sharding.ShardService;
import com.github.krystalics.d10.scheduler.start.sharding.ShardingStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    private volatile Thread ackThread;


    /**
     * fixme 在切换了leader后会发生两次rebalance 要注意这个，但是两次rebalance；一次在be leader后，一次在/live 节点变化后
     * 刚成为是需要进行rebalance的，防止/live节点的事件先发生，这时候节点还不是leader；而成为了leader后确不进行rebalance。
     * <p>
     * 这里加锁是为了防止连续多个节点变化，同时有多个节点在进行rebalance
     *
     * @param address 发生变化的节点地址
     */
    @Override
    public synchronized void shard(String address) throws Exception {

        log.info("scheduler system begin rebalanced!");
        log.info("1.to create /shard node");
        zookeeperService.createNodeIfNotExist(CommonConstants.ZK_SHARD_NODE, address, CreateMode.PERSISTENT);
        RetryerUtils.retryCall(() -> zookeeperService.exists(CommonConstants.ZK_SHARD_NODE), true);
        log.info("2.assign the task to schedulers");
        shard();
        log.info("wait to receive all live node response!");
        shardCheckAck();
    }

    /**
     * 将现有的live节点与shard节点下的子节点进行对比
     * 新建一个线程去做ack的确认，防止触发事件的这个线程一直阻塞在这里，无法响应/shard节点的创建
     *
     * @throws Exception
     */
    private void shardCheckAck() throws Exception {
        ackThread = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    try {
                        //防止网络抖动，访问zk出异常 然后导致check结束
                        if (!ackThread.isInterrupted()) {
                            final List<String> liveNodes = zookeeperService.liveNodes();
                            final List<String> children = zookeeperService.getChildren(CommonConstants.ZK_SHARD_NODE);
                            log.info("check live node and accept the shard result's node. live nodes ={},ack nodes={}", liveNodes, children);
                            if (liveNodes.containsAll(children) && children.containsAll(liveNodes)) {
                                break;
                            }
                            Thread.sleep(CommonConstants.SHARD_ACK_WAITING);
                        }
                    } catch (Exception e) {
                    }
                }

                log.info("3.delete the /shard node");
                zookeeperService.deleteChildrenAndParent(CommonConstants.ZK_SHARD_NODE);
            }
        }, "shard-ack");

        ackThread.setDaemon(true);
        ackThread.start();

    }

    @Override
    public void stop() {
        if (ackThread != null) {
            if (ackThread.getState() != Thread.State.TERMINATED) {
                // interrupt and wait
                ackThread.interrupt();
                try {
                    ackThread.join();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }

            log.warn("shard ack has been stopped,the /shard node has not been delete,and scheduler won't restart!");
        }
    }

    /**
     * 使用shard策略将任务分片，然后将分片结果写入 /live 节点中
     * 不直接写入/shard 节点是因为，有时执行的太快，等其他节点启动时，shard已经结束，节点被删了
     * 方便其他节点接收到节点创建的信号，防止流程太快，其他节点接收不到node_change的信号 ，节点就被删除了
     * 所以将其结果写入 /live 中
     *
     * @throws Exception
     */
    public void shard() throws Exception {
        final List<String> liveNodes = zookeeperService.liveNodes();
        log.info("rebalanced ,all live nodes are {}", liveNodes);
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
        final List<JobInstance> sharding = shardingStrategy.sharding(jobInstances, taskSize);
        zookeeperService.setData(CommonConstants.ZK_SHARD_NODE, JSONUtils.toJSONStringWithoutCircleDetect(sharding));
    }

}
