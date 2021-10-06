package com.github.krystalics.d10.scheduler.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
public class IPUtils {
    public static String getHost() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("failed to fetch Ip!", e);
        }
        return ip;
    }

}
