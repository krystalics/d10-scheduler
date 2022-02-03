package com.github.krystalics.d10.scheduler.core.load.balance;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.rpc.utils.Host;

/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description 依据机器的资源状况进行分发
 */
public class ResourceLoadBalancer implements LoadBalancer {
    @Override
    public Host suitableHost(VersionInstance instance) {
        Host host = new Host();

        return host;
    }
}
