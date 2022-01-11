package com.github.krystalics.d10.scheduler.start.sharding;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.JSONUtils;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import com.github.krystalics.d10.scheduler.start.sharding.impl.ScopeStrategy;
import com.github.krystalics.d10.scheduler.start.zk.ZookeeperServiceImpl;
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
    private ZookeeperServiceImpl zookeeperService;

    /**
     * 当新leader替换老leader上位，就会在startRunner和live node delete重复执行 rebalance
     * 所以需要加一个trylock
     */
    private final Lock lock = new ReentrantLock();

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
                    //todo
                    log.info("3.delete the /shard node");
                    zookeeperService.deleteNode(CommonConstants.ZK_SHARD_NODE);
                    lock.unlock();
                    break;
                } else {
                    log.warn("the former one has get the lock, node = {}", address);
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

    private void shardAck() throws Exception {
        final List<String> liveNodes = zookeeperService.liveNodes();

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
