package com.github.krystalics.d10.scheduler.registry.zk;

import com.github.krystalics.d10.scheduler.registry.zk.listener.ConnectionStateChangeListener;
import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author linjiabao001
 * @date 2021/10/1
 * @description
 */
@Configuration
public class CuratorConfig {
    @Value("${zk.scheduler.address}")
    private String zkAddress;

    @Value("${zk.scheduler.username}")
    private String username;

    @Value("${zk.scheduler.password}")
    private String password;

    /**
     * 1分钟的会话超时
     * 10s的连接超时
     */
    private int sessionTimeout = 60000;

    private int connectTimeout = 10000;

    /**
     * 设置digest权限：基于账号密码的授权模式，这里的密码只能使用加密之后的密码
     */
    private static final String SCHEME = "digest";

    @Autowired
    private ConnectionStateChangeListener connectionStateChangeListener;

    @Bean
    public CuratorFramework curator() {
        final RetryNTimes retryPolicy = new RetryNTimes(3, 500);

        String auth = getAuth();
        final ACLProvider aclProvider = new ACLProvider() {
            @Override
            public List<ACL> getDefaultAcl() {
                return aclList();
            }

            @Override
            public List<ACL> getAclForPath(String s) {
                return aclList();
            }
        };

        final CuratorFramework client = CuratorFrameworkFactory.builder()
                .aclProvider(aclProvider)
                .authorization(SCHEME, auth.getBytes(StandardCharsets.UTF_8))
                .connectString(zkAddress)
                .sessionTimeoutMs(sessionTimeout)
                .connectionTimeoutMs(connectTimeout)
                .retryPolicy(retryPolicy)
                .build();

        client.getConnectionStateListenable().addListener(connectionStateChangeListener);

        client.start();

        return client;
    }

    private String getAuth() {
        return username + ":" + password;
    }

    public List<ACL> aclList() {
        List<ACL> acls = Lists.newArrayList();
        String auth = getAuth();
        try {
            Id aclId = new Id(SCHEME, DigestAuthenticationProvider.generateDigest(auth));
            ACL acl = new ACL(ZooDefs.Perms.ALL, aclId);
            acls.add(acl);
        } catch (Exception e) {
            throw new IllegalStateException("zk acls error");
        }
        return acls;
    }
}
