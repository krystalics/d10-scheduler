/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.krystalics.d10.scheduler.rpc.config;

import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import com.github.krystalics.d10.scheduler.rpc.utils.Constants;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ServiceBean find all rpcService
 */
public class ServiceBean {

    private static final Logger logger = LoggerFactory.getLogger(ServiceBean.class);

    private static Map<String, Class> serviceMap = new HashMap<>();

    private static AtomicBoolean initialized = new AtomicBoolean(false);

    private ServiceBean() {
        throw new IllegalStateException("Utility class");
    }

    private static synchronized void init() {
        if (initialized.get()) {
            return;
        }

        //keypoint reflections 0.10.2 版本扫描不到bean、倒是0.9.12版本可以
        Reflections f = new Reflections(Constants.D10_SCHEDULER_RPC_PACKAGE);
        List<Class<?>> list = new ArrayList<>(f.getTypesAnnotatedWith(RpcService.class));
        list.forEach(rpcClass -> {
            RpcService rpcService = rpcClass.getAnnotation(RpcService.class);
            serviceMap.put(rpcService.value(), rpcClass);
            logger.info("load rpc service {}", rpcService.value());
        });
        initialized.set(true);
    }

    public static Class getServiceClass(String className) {
        if (initialized.get()) {
            return serviceMap.get(className);
        } else {
            init();
        }
        return serviceMap.get(className);
    }

}
