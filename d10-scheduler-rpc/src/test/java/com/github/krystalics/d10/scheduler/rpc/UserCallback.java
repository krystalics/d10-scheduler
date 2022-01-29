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

package com.github.krystalics.d10.scheduler.rpc;

import com.github.krystalics.d10.scheduler.rpc.common.AbstractRpcCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserCallback
 */
public class UserCallback extends AbstractRpcCallBack {

    private static final Logger logger = LoggerFactory.getLogger(UserCallback.class);

    /**
     * UserCallback的使用demo
     * @Rpc(async = true, serviceCallback = UserCallback.class)
     * String say(String s) throws InterruptedException;
     *
     * 下面的object具体类型 需要根据调用的方法返回类型设置(这个demo中是String)，尽量一个callback类只被一个地方用到。
     * @param object response
     */
    @Override
    public void run(Object object) {
        //
        String msg = (String) object;
        logger.debug("Kris---------------------------------userCallBack msg is {}", msg);
    }
}
