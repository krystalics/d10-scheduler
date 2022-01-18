package com.github.krystalics.d10.scheduler.start.sharding.impl;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.utils.JSONUtils;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.start.sharding.RebalanceService;
import com.github.krystalics.d10.scheduler.start.sharding.ShardingStrategy;
import com.github.krystalics.d10.scheduler.start.sharding.impl.ScopeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private ZookeeperHelper zookeeperService;

    /**
     * 当新leader替换老leader上位，就会在startRunner和live node delete重复执行 rebalance
     * 所以需要加一个trylock
     */
    private final Lock lock = new ReentrantLock();

    /**
     * todo 连续多次的shard 需要重新继续
     * @param address
     */
    @Override
    public void rebalance(String address) {
        for (int i = 0; i < CommonConstants.REBALANCED_TRY_TIMES; i++) {
            try {
                if (lock.tryLock()) {
                    log.info("scheduler system begin rebalanced!");
                    log.info("1.to create /shard node");
                    zookeeperService.createNodeIfNotExist(CommonConstants.ZK_SHARD_NODE, address, CreateMode.EPHEMERAL);
                    log.info("2.assign the task to schedulers");
                    shard();
                    log.info("wait to receive all live node response!");
                    shardCheckAck();
                    log.info("3.delete the /shard node");
                    zookeeperService.deleteNode(CommonConstants.ZK_SHARD_NODE);
                    zookeeperService.deleteChildrenAndParent(CommonConstants.ZK_SHARD_RESULT_NODE);
                    lock.unlock();
                    break;
                } else {
                    log.warn("the former one has get the lock, node = {}", address);
                    Thread.sleep(1000);
                    break;
                }

            } catch (Exception e) {
                //如果rebalance的时候发生异常，进行unlock、并重新尝试，几次之后会通知管理员进行查看
                log.error("rebalancing error,{}", e.toString());
                try {
                    lock.unlock();
                } catch (Exception ex) {
                    log.error("unlock error, {}", ex.toString());
                }
            }
        }


    }

    /**
     * 将现有的live节点与shard节点下的子节点进行对比
     * todo KeeperErrorCode = NoChildrenForEphemerals for /shard/127.0.0.1:8083
     * @throws Exception
     */
    private void shardCheckAck() throws Exception {
        while (true) {
            final List<String> liveNodes = zookeeperService.liveNodes();
            final List<String> children = zookeeperService.getChildren(CommonConstants.ZK_SHARD_RESULT_NODE);
            log.info("check live node and accept the shard result's node. live nodes ={},ack nodes={}", liveNodes, children);
            if (liveNodes.containsAll(children) && children.containsAll(liveNodes)) {
                return;
            }
            Thread.sleep(CommonConstants.SHARD_ACK_WAITING);
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
        zookeeperService.setData(CommonConstants.ZK_LIVE_NODES, JSONUtils.toJSONStringWithoutCircleDetect(sharding));
        log.info("sharding result is {}", sharding);
    }

}