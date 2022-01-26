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

package com.github.krystalics.d10.scheduler.rpc.client;


import com.github.krystalics.d10.scheduler.rpc.utils.Host;
import com.github.krystalics.d10.scheduler.rpc.exceptions.RemotingException;

import com.github.krystalics.d10.scheduler.rpc.base.Rpc;
import com.github.krystalics.d10.scheduler.rpc.common.AbstractRpcCallBack;
import com.github.krystalics.d10.scheduler.rpc.common.RpcRequest;
import com.github.krystalics.d10.scheduler.rpc.common.RpcResponse;
import com.github.krystalics.d10.scheduler.rpc.protocol.EventType;
import com.github.krystalics.d10.scheduler.rpc.protocol.MessageHeader;
import com.github.krystalics.d10.scheduler.rpc.protocol.RpcProtocol;
import com.github.krystalics.d10.scheduler.rpc.remote.NettyClient;
import com.github.krystalics.d10.scheduler.rpc.serializer.RpcSerializer;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * ConsumerInterceptor
 */
public class ConsumerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerInterceptor.class);
    private Host host;

    private NettyClient nettyClient = NettyClient.getInstance();

    ConsumerInterceptor(Host host) {
        this.host = host;
    }

    @RuntimeType
    public Object intercept(@AllArguments Object[] args, @Origin Method method) throws RemotingException {
        RpcRequest request = buildReq(args, method);

        String serviceName = method.getDeclaringClass().getSimpleName() + method.getName();
        ConsumerConfig consumerConfig = ConsumerConfigCache.getConfigByServersName(serviceName);
        if (null == consumerConfig) {
            consumerConfig = cacheServiceConfig(method, serviceName);
        }
        boolean async = consumerConfig.getAsync();

        int retries = consumerConfig.getRetries();

        RpcProtocol<RpcRequest> protocol = buildProtocol(request);

        while (retries-- > 0) {
            RpcResponse rsp;
            rsp = nettyClient.sendMsg(host, protocol, async);
            //success
            if (null != rsp && rsp.getStatus() == 0) {
                return rsp.getResult();
            }
        }
        // execute fail
        throw new RemotingException("send msg error");

    }

    private RpcRequest buildReq(Object[] args, Method method) {
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getSimpleName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        return request;
    }

    private ConsumerConfig cacheServiceConfig(Method method, String serviceName) {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setServiceName(serviceName);
        boolean annotationPresent = method.isAnnotationPresent(Rpc.class);
        if (annotationPresent) {
            Rpc rpc = method.getAnnotation(Rpc.class);
            consumerConfig.setAsync(rpc.async());
            consumerConfig.setServiceCallBackClass(rpc.serviceCallback());
            if (!rpc.serviceCallback().isInstance(AbstractRpcCallBack.class)) {
                consumerConfig.setCallBack(true);
            }
            consumerConfig.setAckCallBackClass(rpc.ackCallback());
            consumerConfig.setRetries(rpc.retries());
        }

        ConsumerConfigCache.putConfig(serviceName, consumerConfig);

        return consumerConfig;
    }

    private RpcProtocol<RpcRequest> buildProtocol(RpcRequest req) {
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        MessageHeader header = new MessageHeader();
        header.setRequestId(RpcRequestTable.getRequestId());
        header.setEventType(EventType.REQUEST.getType());
        header.setSerialization(RpcSerializer.PROTOSTUFF.getType());
        protocol.setMsgHeader(header);
        protocol.setBody(req);
        return protocol;
    }

}
