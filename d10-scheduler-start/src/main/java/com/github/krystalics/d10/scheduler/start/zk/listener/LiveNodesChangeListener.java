package com.github.krystalics.d10.scheduler.start.zk.listener;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.RetryerUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.start.event.EventThreadPool;
import com.github.krystalics.d10.scheduler.start.event.EventType;
import com.github.krystalics.d10.scheduler.start.event.EventWorker;
import com.github.krystalics.d10.scheduler.start.sharding.ShardService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


/**
 * @author krysta
 * @description 当/live路径下的节点数量发生变化，leader会创建/shard 节点、开启sharding工作
 */
@Configuration
@Slf4j
public class LiveNodesChangeListener implements PathChildrenCacheListener {

    @Autowired
    private ShardService shardService;

    @Autowired
    public LeaderLatch leaderLatch;

    @Autowired
    private ZookeeperHelper zookeeperHelper;

    /**
     * 临时节点发生一些异常情况、就直接移除
     *
     * @param curatorFramework
     * @param pathChildrenCacheEvent
     * @throws Exception
     * @important 临时节点 没有CONNECTION_RECONNECTED事件
     */
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        switch (pathChildrenCacheEvent.getType()) {
            case CHILD_ADDED:
                EventThreadPool.submit(new EventWorker(EventType.LIVE_NODE_ADD, new String(pathChildrenCacheEvent.getData().getData())));
                break;
            case CHILD_UPDATED:
                // value not change cause IP not change in a session time
                break;
            case CONNECTION_LOST:
            case CHILD_REMOVED:
                EventThreadPool.submit(new EventWorker(EventType.LIVE_NODE_DEL, new String(pathChildrenCacheEvent.getData().getData())));
                break;
            case CONNECTION_SUSPENDED:
                //当进行 Leader 选举和 lock 锁等操作时，需要先挂起客户端的连接。注意这里的会话挂起并不等于关闭会话，也不会触发诸如删除临时节点等操作
                break;
            default:

        }
    }

    public void add(String node) throws Exception {
        log.info("new node is {}", node);
        checkLeaderExist();
        if (leaderLatch.hasLeadership()) {
            shardService.shard();
        }
    }

    public void delete(String node) throws Exception {

        log.info("node has been deleted {}", node);
        checkLeaderExist();
        if (leaderLatch.hasLeadership()) {
            shardService.shard();
        }

    }

    public void checkLeaderExist() throws Exception {
        log.info("check leader exist before shard!");
        boolean exist = RetryerUtils.retryCallLong(() -> zookeeperHelper.exists(CommonConstants.ZK_LEADER), true);
        if (!exist) {
            log.error("no leader ! system error");
            System.exit(-1);
        }
    }
}
