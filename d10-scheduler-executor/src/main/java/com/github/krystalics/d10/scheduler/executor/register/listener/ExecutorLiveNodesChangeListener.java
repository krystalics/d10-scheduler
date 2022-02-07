package com.github.krystalics.d10.scheduler.executor.register.listener;

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
public class ExecutorLiveNodesChangeListener implements PathChildrenCacheListener {


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
//          case CONNECTION_RECONNECTED: 临时节点 没有CONNECTION_RECONCTED事件
                add(pathChildrenCacheEvent);
                break;
            case CHILD_UPDATED:
                // value not change cause IP not change in a session time
                break;
            case CONNECTION_LOST:
            case CHILD_REMOVED:
                delete(pathChildrenCacheEvent);
                break;
            case CONNECTION_SUSPENDED:
                //当进行 Leader 选举和 lock 锁等操作时，需要先挂起客户端的连接。注意这里的会话挂起并不等于关闭会话，也不会触发诸如删除临时节点等操作
                break;
            default:

        }
    }

    /**
     * 当
     *
     * @param pathChildrenCacheEvent
     * @throws Exception
     */
    public void add(PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        final String newNode = new String(pathChildrenCacheEvent.getData().getData());
        log.info("new node is {}", newNode);

    }

    public void delete(PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        final String node = new String(pathChildrenCacheEvent.getData().getData());
        log.info("node has been deleted {}", node);


    }
}
