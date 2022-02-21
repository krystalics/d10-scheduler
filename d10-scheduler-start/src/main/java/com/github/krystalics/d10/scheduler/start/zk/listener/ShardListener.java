package com.github.krystalics.d10.scheduler.start.zk.listener;

import com.alibaba.fastjson.JSON;
import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.constant.JobInstance;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import com.github.krystalics.d10.scheduler.core.schedule.D10Scheduler;
import com.github.krystalics.d10.scheduler.start.event.EventThreadPool;
import com.github.krystalics.d10.scheduler.start.event.EventType;
import com.github.krystalics.d10.scheduler.start.event.EventWorker;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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
    @SneakyThrows
    @Override
    public void event(CuratorCacheListener.Type type, ChildData before, ChildData after) {
        switch (type) {
            case NODE_CREATED:
                EventThreadPool.submit(new EventWorker(EventType.SHARD_ADD, new String(after.getData())));
                break;
            case NODE_CHANGED:
                EventThreadPool.submit(new EventWorker(EventType.SHARD_CHANGE, new String(after.getData())));
                break;
            case NODE_DELETED:
                EventThreadPool.submit(new EventWorker(EventType.SHARD_DEL, new String(before.getData())));
                break;
            default:
                throw new RuntimeException("unknown node event type " + type.name());
        }
    }

}
