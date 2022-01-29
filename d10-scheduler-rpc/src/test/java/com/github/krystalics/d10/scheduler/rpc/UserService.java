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

import com.github.krystalics.d10.scheduler.rpc.base.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserService
 */
@RpcService("IUserService")
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    /**
     * sleep 2s 模拟异步调用
     *
     * @return
     */
    @Override
    public String say(String s) throws InterruptedException {
        Thread.sleep(2000);
        logger.info("Kris UserService say-------------------------------asynchronous call msg{}", s);
        return "fine!";
    }

    @Override
    public Integer hi(int num) {

        logger.info("Kris UserService hi-------------------------------sync call msg{}", num);
        return ++num;
    }

    @Override
    public Boolean callBackIsFalse(String s) {
        logger.info("Kris UserService callBackIsFalse-------------------------------async call msg{}", s);
        return true;
    }
}
