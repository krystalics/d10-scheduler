package com.github.krystalics.d10.scheduler.core.zk.listener;

import com.github.krystalics.d10.scheduler.core.common.Constant;
import com.github.krystalics.d10.scheduler.core.service.impl.ZookeeperServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
@Configuration
@Slf4j
public class ConnectionStateChangeListener implements ConnectionStateListener {

    @Autowired
    private ZookeeperServiceImpl zookeeperService;

    /**
     * 当节点与zk的连接状态发生变化时、在这里处理
     * leaderLatch中 已经封装了ConnectionStateListener、但只有leader角色才会有这待遇。
     * 代码参考:阅读理解
     *
     * @param curatorFramework
     * @param connectionState
     * @see org.apache.curator.framework.CuratorFrameworkFactory.Builder
     * 在默认的 CuratorFramework client builder中采用的是StandardConnectionStateErrorPolicy()
     * 它对于错误状态的定义就是 Lost和SUSPENDED
     * @see org.apache.curator.framework.state.StandardConnectionStateErrorPolicy
     *
     * <p>
     * 1。当节点没死、只是与zk失联。在系统意义上，它已经死了。所以需要暂时不接活、等状态恢复
     * <p>
     * 通过设置系统的全局变量，在拦截器拦截所有的请求、如果节点lost变量处于true状态，请求就转发到其他节点中
     * <p>
     * 2。不只与zk失联，可能还与集群其他的节点失联(可能其他节点挂了)。这时候转发请求也会失败
     */
    @SneakyThrows
    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        switch (connectionState) {
            case LOST:
                ClusterInfo.setLost();
                break;
            case SUSPENDED:
                //当进行 Leader 选举和 lock 锁等操作时，需要先挂起客户端的连接。注意这里的会话挂起并不等于关闭会话，也不会触发诸如删除临时节点等操作
                break;
            case CONNECTED:
                log.info("i connect to zk,wow");
                ClusterInfo.connected();
                break;
            case READ_ONLY:
                //当zk节点出现问题、会出现只读的情况、不过我们这里不需要处理
                break;
            case RECONNECTED:
                //todo 将临时节点补回来
//                createNode(Constant.ZK_LIVE_NODES + "/" + address, address, CreateMode.EPHEMERAL);
                zookeeperService.createNodeIfNotExist(Constant.ZK_LIVE_NODES + "/" + ClusterInfo.selfAddress(), ClusterInfo.selfAddress(), CreateMode.EPHEMERAL);
                log.info("i reconnect to zk,wow");
                ClusterInfo.connected();
                break;
            default:
                throw new RuntimeException("unknown zk connection state " + connectionState);
        }
    }
}
