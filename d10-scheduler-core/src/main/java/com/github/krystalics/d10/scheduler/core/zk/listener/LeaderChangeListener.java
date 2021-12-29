package com.github.krystalics.d10.scheduler.core.zk.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.springframework.context.annotation.Configuration;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Configuration
@Slf4j
public class  LeaderChangeListener implements CuratorCacheListener {

    /**
     * 当项目启动、check到要监听到节点已经存在、会触发 NODE_CREATED 动作。
     *
     * @param type   节点变更类型
     * @param before 原始值
     * @param after  变更后的值
     */
    @Override
    public void event(Type type, ChildData before, ChildData after) {
        switch (type) {
            case NODE_CREATED:
                final String modifyData = new String(after.getData());
                log.info("the leader node is " + modifyData);
                ClusterInfo.setMaster(modifyData);
                break;
            case NODE_CHANGED:
                final String originData = new String(before.getData());
                final String afterData = new String(after.getData());
                log.info("leader has been changed,from [{}] to [{}] ", originData, afterData);
                ClusterInfo.setMaster(afterData);
                break;
            case NODE_DELETED:
                final String beforeData = new String(before.getData());
                log.info("the leader node has been deleted,before is " + beforeData);
                ClusterInfo.setMaster("");
                break;
            default:
                throw new RuntimeException("unknown node event type " + type.name());
        }
    }

}
