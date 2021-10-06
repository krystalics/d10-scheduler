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

    @Override
    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
        switch (pathChildrenCacheEvent.getType()) {
            case CHILD_ADDED:
                final String newNode = new String(pathChildrenCacheEvent.getData().getData());
                log.info("new node is {}" , newNode);
                if (ClusterInfo.isNewHere(newNode)) {
                    ClusterInfo.addToLiveNodes(newNode);
//                  todo leader 判断？
                    rebalanceService.rebalance();
                }

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
