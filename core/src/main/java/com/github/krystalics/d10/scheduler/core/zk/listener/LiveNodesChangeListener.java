package com.github.krystalics.d10.scheduler.core.zk.listener;

import com.github.krystalics.d10.scheduler.core.common.ClusterInfo;
import com.github.krystalics.d10.scheduler.core.service.RebalanceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;


/**
 * @author krysta
 */
@Configuration
@Slf4j
public class LiveNodesChangeListener implements PathChildrenCacheListener {

    @Autowired
    private RebalanceService rebalanceService;

    /**
     * 临时节点发生一些异常情况、就直接移除
     * @important 临时节点 没有CONNECTION_RECONCTED事件
     * @param curatorFramework
     * @param pathChildrenCacheEvent
     * @throws Exception
     */
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        switch (pathChildrenCacheEvent.getType()) {
            case CHILD_ADDED:
//            case CONNECTION_RECONNECTED: 临时节点 没有CONNECTION_RECONCTED事件
                add(pathChildrenCacheEvent);
                break;
            case CHILD_UPDATED:
                // value not change cause IP not change in a session time
                break;
            case CONNECTION_LOST:
            case CHILD_REMOVED:
                final String node = new String(pathChildrenCacheEvent.getData().getData());
                log.info("node has been deleted {}", node);
                ClusterInfo.removeFromLiveNodes(node);
                break;
            case CONNECTION_SUSPENDED:
                //当进行 Leader 选举和 lock 锁等操作时，需要先挂起客户端的连接。注意这里的会话挂起并不等于关闭会话，也不会触发诸如删除临时节点等操作
                break;
            default:

        }
    }

    public void add(PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        final String newNode = new String(pathChildrenCacheEvent.getData().getData());
        log.info("new node is {}", newNode);
        if (ClusterInfo.isNewHere(newNode)) {
            ClusterInfo.addToLiveNodes(newNode);
//          todo leader 判断？
            rebalanceService.rebalance();
        }
    }
}
