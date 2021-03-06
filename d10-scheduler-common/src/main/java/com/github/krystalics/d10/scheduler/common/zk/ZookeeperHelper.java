package com.github.krystalics.d10.scheduler.common.zk;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Service
@Slf4j
public class ZookeeperHelper {

    @Autowired
    private CuratorFramework client;

    /**
     * zookeeper 不适合类似于mysql的查询，这个只作为 项目启动时
     * rebalance的凭证，因为这时候 clusterInfo中的信息还不是完整的
     *
     * @return
     * @throws Exception
     */
    public List<String> liveNodes() throws Exception {
        return client.getChildren().forPath(CommonConstants.ZK_LIVE_NODES);
    }

    public void setData(String path, String data) throws Exception {
        client.setData().forPath(path, data.getBytes(StandardCharsets.UTF_8));
    }

    public void createNodeIfNotExist(String path, String data, CreateMode mode) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            return;
        }
        client.create().creatingParentsIfNeeded()
                .withMode(mode)
                .forPath(path, data.getBytes(StandardCharsets.UTF_8));
    }

    public void createNodeWithoutParent(String path, String data, CreateMode mode) throws Exception {
        if (client.checkExists().forPath(path) != null) {
            return;
        }
        client.create()
                .withMode(mode)
                .forPath(path, data.getBytes(StandardCharsets.UTF_8));
    }

    public boolean exists(String path) throws Exception {
        return client.checkExists().forPath(path) != null;
    }

    public void deleteNode(String path) throws Exception {
        client.delete().forPath(path);
    }

    public void deleteIfExists(String path) throws Exception {
        if (exists(path)) {
            deleteChildrenAndParent(path);
        }
    }

    public void deleteChildrenAndReserveParent(String path) throws Exception {
        final List<String> children = getChildren(path);
        if (children != null && children.size() > 0) {
            for (String child : children) {
                deleteNode(path + "/" + child);
            }
        }
    }

    public void deleteChildrenAndParent(String path) throws Exception {
        client.delete().deletingChildrenIfNeeded().forPath(path);
    }

    public List<String> getChildren(String path) throws Exception {
        return client.getChildren().forPath(path);
    }


    public String getData(String path) throws Exception {
        byte[] bytes = client.getData().forPath(path);
        return new String(bytes);
    }
}
