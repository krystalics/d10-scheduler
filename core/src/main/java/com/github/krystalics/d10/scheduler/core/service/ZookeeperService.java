package com.github.krystalics.d10.scheduler.core.service;

import java.util.List;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
public interface ZookeeperService {
    List<String> liveNodes() throws Exception;
}
