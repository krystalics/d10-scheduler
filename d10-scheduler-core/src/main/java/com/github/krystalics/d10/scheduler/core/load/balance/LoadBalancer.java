package com.github.krystalics.d10.scheduler.core.load.balance;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import com.github.krystalics.d10.scheduler.rpc.utils.Host;

/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description 负载均衡的接口
 */
public interface LoadBalancer {
    /**
     * 获取合适的host作为目标点
     * @param instance 需要进行负载均衡的实例
     * @return 算法选择的host
     */
    Host suitableHost(VersionInstance instance);
}
