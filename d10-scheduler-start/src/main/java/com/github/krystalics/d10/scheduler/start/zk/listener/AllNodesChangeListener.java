package com.github.krystalics.d10.scheduler.start.zk.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.springframework.context.annotation.Configuration;


/**
 * @author krysta
 */
@Configuration
@Slf4j
public class AllNodesChangeListener implements PathChildrenCacheListener {

    /**
     * 在child_add事件下 原有的 initialData ==null 、且原来存在的节点都会触发一次这个事件
     * child_remove事件下 initialData ==null
     * child_update
     *
     * @param curatorFramework
     * @param pathChildrenCacheEvent
     * @throws Exception
     */
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) {
        switch (pathChildrenCacheEvent.getType()) {
            case CHILD_ADDED:
                final String newNode = new String(pathChildrenCacheEvent.getData().getData());
                log.info("new node is {}", newNode);
//                ClusterInfo.addToAllNodes(newNode);
                break;
            case CHILD_UPDATED:
                // value not change cause IP not change in a session time
                break;
            case CHILD_REMOVED:
                final String node = new String(pathChildrenCacheEvent.getData().getData());
                log.info("node has been deleted {}", node);
//                ClusterInfo.removeFromAllNodes(node);
                break;
            default:

        }
    }
}
