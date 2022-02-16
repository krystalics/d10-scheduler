package com.github.krystalics.d10.scheduler.start.sharding.impl;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.utils.RetryerUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.core.schedule.D10Scheduler;
import com.github.krystalics.d10.scheduler.dao.mapper.TaskMapper;
import com.github.krystalics.d10.scheduler.dao.qm.TaskQM;
import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import com.github.krystalics.d10.scheduler.rpc.client.IRpcClient;
import com.github.krystalics.d10.scheduler.rpc.client.RpcClient;
import com.github.krystalics.d10.scheduler.rpc.utils.Host;
import com.github.krystalics.d10.scheduler.start.sharding.IRebalanceService;
import com.github.krystalics.d10.scheduler.start.sharding.IShardService;
import com.github.krystalics.d10.scheduler.start.sharding.ShardingStrategy;
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
//@RpcService("IShardService")
public class IShardServiceImpl implements IShardService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private ZookeeperHelper zookeeperService;

    private volatile boolean stopShard = false;

    @Autowired
    private JobInstance instance;

    IRebalanceService service;


    /**
     * 这里加锁是为了防止连续多个节点变化，同时有多个节点在进行rebalance、需要按顺序进行
     * <p>
     * todo 改变完全基于 zk watcher事件的思路，比如1。通知停止调度，2。分片变更，3。重新调度 使用rpc进行同步
     * - 事件的异步执行，顺序混乱影响比较大。
     */
    @Override
    public synchronized void shard() throws Exception {
        log.info("scheduler system begin shard");
        stopShard = false;
        zookeeperService.deleteIfExists(CommonConstants.ZK_SHARD);
        zookeeperService.createNodeIfNotExist(CommonConstants.ZK_SHARD, "shard-result", CreateMode.PERSISTENT);
        log.info("1.calculate the new shard in this system.");
        final List<JobInstance> jobInstances = shardWithStrategy();
        log.info("2.start shard at every live node");
        for (JobInstance jobInstance : jobInstances) {
//            if (jobInstance.getAddress().equals(instance.getAddress())) {
//                EventThreadPool.submit(new EventWorker(EventType.SHARD_BEGIN, instance));
//            } else {
            Host host = new Host(jobInstance.getAddress());
            IRpcClient client = new RpcClient();
            service = client.create(IRebalanceService.class, host);
            service.shardBegin(jobInstance);
//                RetryerUtils.retryCall(() -> service.shardBegin(jobInstance), true);
//            }

        }

        shardCheckAck();

        for (JobInstance jobInstance : jobInstances) {
            if (jobInstance.getAddress().equals(instance.getAddress())) {
                D10Scheduler.getInstance().start();
            } else {
                Host host = new Host(jobInstance.getAddress());
                IRpcClient client = new RpcClient();
                service = client.create(IRebalanceService.class, host);
                RetryerUtils.retryCall(service::shardEnd, true);
            }
        }

    }


    /**
     * 将现有的live节点与shard节点下的子节点进行对比
     * 新建一个线程去做ack的确认，防止触发事件的这个线程一直阻塞在这里，无法响应/shard节点的创建
     *
     * @throws Exception
     */
    private void shardCheckAck() throws Exception {
        log.info("check every node done for this!");
        while (true) {
            try {
                if (!stopShard) {
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

    }

    @Override
    public void stopShard() {
        stopShard = true;
    }

    /**
     * 使用shard策略将任务分片，然后将分片结果写入 /shard 节点中
     *
     * @throws Exception error
     */
    public List<JobInstance> shardWithStrategy() throws Exception {
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
        //todo 策略可选
        ShardingStrategy shardingStrategy = new ScopeStrategy();
        return shardingStrategy.sharding(jobInstances, taskSize);
    }

}
