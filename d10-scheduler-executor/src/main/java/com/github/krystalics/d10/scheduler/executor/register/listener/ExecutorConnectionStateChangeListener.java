package com.github.krystalics.d10.scheduler.executor.register.listener;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.krystalics.d10.scheduler.common.utils.IPUtils;
import com.github.krystalics.d10.scheduler.common.zk.ZookeeperHelper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author linjiabao001
 * @date 2021/10/7
 * @description
 */
@Configuration
@Slf4j
public class ExecutorConnectionStateChangeListener implements ConnectionStateListener {

    @Autowired
    private ZookeeperHelper zookeeperService;

    @Value("${server.port}")
    private int port;

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
     * <p>
     * 当节点没死、只是与zk失联。在系统意义上，它已经死了。所以需要暂时不接活、等状态恢复
     * Curator官方建议把SUSPENDED事件当作完全的连接断开来处理。意思就是把收到SUSPENDED事件的时候就当作自己注册的所有临时节点已经掉了
     * <p>
     * 当zk节点出现网络分区问题、会出现只读的情况、不过我们这里不需要处理
     * <p>
     * session timeout后重新连接的情况、并不会建立新的临时节点，需要手动创建 /live的节点情况
     */
    @SneakyThrows
    @Override
    public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
        switch (connectionState) {
            case LOST:
            case SUSPENDED:
                log.error("node lost the connection with zookeeper!");
//                D10Executor.getInstance().stop();
                break;
            case CONNECTED:
                log.info("already connect to zk");
                break;
            case READ_ONLY:
                log.error("something error happended,zk is in read-only state");
                break;
            case RECONNECTED:
                String address = IPUtils.getHost() + ":" + port;
                log.info("{} reconnect to zk、need to create node by hand", address);
                zookeeperService.createNodeIfNotExist(CommonConstants.ZK_LIVE_NODES, address, CreateMode.EPHEMERAL);
                break;
            default:
                throw new RuntimeException("unknown zk connection state " + connectionState);
        }
    }
}
