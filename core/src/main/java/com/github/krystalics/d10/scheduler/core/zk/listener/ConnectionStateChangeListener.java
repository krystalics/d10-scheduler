package com.github.krystalics.d10.scheduler.core.zk.listener;

import com.github.krystalics.d10.scheduler.core.common.ClusterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.springframework.context.annotation.Configuration;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
@Configuration
@Slf4j
public class ConnectionStateChangeListener implements ConnectionStateListener {

    /**
     * 当节点与zk的连接状态发生变化时、在这里处理
     * leaderLatch中 已经封装了ConnectionStateListener、但只有leader角色才会有这待遇。
     * 代码参考:阅读理解
     * 在默认的 CuratorFramework client builder中采用的是StandardConnectionStateErrorPolicy()
     * 它对于错误状态的定义就是 Lost和SUSPENDED
     * public boolean isErrorState(ConnectionState state) {
     *     return state == ConnectionState.SUSPENDED || state == ConnectionState.LOST;
     * }
     *
     * switch(newState) {
     *     case RECONNECTED:
     *       try {
     *         if (this.client.getConnectionStateErrorPolicy().isErrorState(ConnectionState.SUSPENDED) || !this.hasLeadership.get()) {
     *           this.reset();
     *         }
     *       } catch (Exception var3) {
     *         ThreadUtils.checkInterrupted(var3);
     *         this.log.error("Could not reset leader latch", var3);
     *         this.setLeadership(false);
     *       }
     *       break;
     *     case SUSPENDED:
     *       if (this.client.getConnectionStateErrorPolicy().isErrorState(ConnectionState.SUSPENDED)) {
     *         this.setLeadership(false);
     *       }
     *       break;
     *     case LOST:
     *       this.setLeadership(false);
     *     }
     *
     * 1。当节点没死、只是与zk失联。在系统意义上，它已经死了。所以需要暂时不接活、等状态恢复
     *
     * 通过设置系统的全局变量，在拦截器拦截所有的请求、如果节点lost变量处于true状态，请求就转发到其他节点中
     *
     * 2。不只与zk失联，可能还与集群其他的节点失联(可能其他节点挂了)。这时候转发请求也会失败
     *
     * @param curatorFramework
     * @param connectionState
     */
    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        switch (connectionState) {
            case LOST:
                ClusterInfo.setLost();
                break;
            case CONNECTED:
                ClusterInfo.connected();
                //do nothing
                break;
            case READ_ONLY:
                //当zk节点出现问题、会出现只读的情况、不过我们这里不需要处理
                break;
            case SUSPENDED:
                ClusterInfo.setLost();
                break;
            case RECONNECTED:
                ClusterInfo.connected();
                break;
            default:
                throw new RuntimeException("unknown zk connection state " + connectionState);
        }
    }
}
