package com.github.krystalics.d10.scheduler.resource.manager.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author linjiabao001
 * @Date 2022/1/17
 * @Description
 */
@Service
public class ResourceService {



    @Transactional(rollbackFor = Throwable.class)
    public String resourceAndInstanceStateUpdate() {
        //todo 1.check这个instance的队列使用的资源有没有达到min
        //todo 2.有的话尝试更新；没有的话，尝试开启争抢模式，去争抢同优先级队列或者之下的 资源
        //todo 3.获取资源后更新自身的状态

        return "";
    }
}
