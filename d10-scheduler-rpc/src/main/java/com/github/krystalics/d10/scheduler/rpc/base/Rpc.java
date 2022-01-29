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

package com.github.krystalics.d10.scheduler.rpc.base;


import com.github.krystalics.d10.scheduler.rpc.common.AbstractRpcCallBack;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rpc
 *
 * @author DS
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Rpc {

    /**
     * number of retries
     */
    int retries() default 3;

    /**
     * keypoint 异步调用的方法，直接的返回值是空值；真正有用的是 远程执行完成，触发回调后的第二次返回值
     */
    boolean async() default false;

    boolean ack() default false;

    boolean callBack() default false;

    //todo It is better to set the timeout period for synchronous calls

    /**
     * When it is asynchronous transmission, callback must be set
     */
    Class<? extends AbstractRpcCallBack> serviceCallback() default AbstractRpcCallBack.class;

    Class<? extends AbstractRpcCallBack> ackCallback() default AbstractRpcCallBack.class;

}
