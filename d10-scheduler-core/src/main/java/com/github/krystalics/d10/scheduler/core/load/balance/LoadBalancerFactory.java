package com.github.krystalics.d10.scheduler.core.load.balance;

/**
 * @author linjiabao001
 * @date 2022/2/1
 * @description
 */
public class LoadBalancerFactory {

    public static LoadBalancer instance() throws Exception {
        //todo config this
        String className = "com.github.krystalics.d10.scheduler.core.load.balance.ResourceLoadBalancer";
        return (LoadBalancer) Class.forName(className).newInstance();
    }

}
