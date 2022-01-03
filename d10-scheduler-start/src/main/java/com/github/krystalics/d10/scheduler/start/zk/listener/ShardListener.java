package com.github.krystalics.d10.scheduler.start.zk.listener;

import com.github.krystalics.d10.scheduler.start.D10SchedulerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.springframework.stereotype.Component;

/**
 * @author linjiabao001
 * @date 2022/1/2
 * @description
 */
@Component
@Slf4j
public class ShardListener implements CuratorCacheListener {

    /**
     * shard节点会由leader在 /live 节点数量发生变化的时候进行创建，其他节点需要在shard期间暂停工作
     *
     * @param type   节点变更类型
     * @param before 原始值
     * @param after  变更后的值
     */
    @Override
    public void event(CuratorCacheListener.Type type, ChildData before, ChildData after) {
        switch (type) {
            case NODE_CREATED:
                log.info("the shard node is created、to stop the scheduler");
                D10SchedulerHelper.getInstance().stop();
                break;
            case NODE_CHANGED:
                break;
            case NODE_DELETED:
                log.info("the shard node is deleted、to start the scheduler");
                D10SchedulerHelper.getInstance().start();
                break;
            default:
                throw new RuntimeException("unknown node event type " + type.name());
        }
    }

}
