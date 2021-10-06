package com.github.krystalics.d10.scheduler.core.zk.listener;

import com.github.krystalics.d10.scheduler.core.zk.ClusterInfo;
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
public class LiveNodesChangeListener implements PathChildrenCacheListener {
    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        switch (pathChildrenCacheEvent.getType()) {
            case CHILD_ADDED:
                final String newNode = new String(pathChildrenCacheEvent.getData().getData());
                log.info("new node is {}" + newNode);
                ClusterInfo.addToLiveNodes(newNode);
                break;
            case CHILD_UPDATED:
                // value not change cause IP not change in a session time
                break;
            case CHILD_REMOVED:
                final String node = new String(pathChildrenCacheEvent.getData().getData());
                log.info("node has been deleted {}", node);
                ClusterInfo.removeFromLiveNodes(node);
                break;
            default:

        }
    }
}
