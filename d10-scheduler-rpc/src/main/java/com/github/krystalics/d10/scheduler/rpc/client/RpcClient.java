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
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * RpcClient
 */
public class RpcClient implements IRpcClient {

    /**
     * 使用bytebuddy进行动态代理，将调用都通过interceptor转发到远端
     * 同类型到产品还有 CGLIB、 Javassist、 JDK动态代理等
     * Byte Buddy是致力于解决字节码操作和 简化操作复杂性的开源框架。Byte Buddy 目标是将显式的字节码操作隐藏在一个类型安全的领域特定语言背后。
     * 它属于后起之秀，在很多优秀的项目中，像 Spring、Jackson 都用到了 Byte Buddy 来完成底层代理。相比 Javassist，Byte Buddy 提供了更容易操作的 API，编写的代码可读性更高。
     *
     * @param clazz 需要创建的目标类、需要这个类具有空的构造函数，否则会初始化失败
     * @param host  需要接收请求的host
     * @param <T>   类型
     * @return 创建出带有代理的对象
     * @throws Exception 抛出异常
     */
    @Override
    public <T> T create(Class<T> clazz, Host host) throws Exception {
        return new ByteBuddy()
                //使用byte-buddy来拓展clazz类型,进行增强
                .subclass(clazz)
                //对该类型对所有声明对方法进行增强，也可以只对某一个方法例如采用 ElementMatchers.isToString() 只对toString方法进行增强
                .method(ElementMatchers.isDeclaredBy(clazz))
                //subclass.method里的调用都会走这个intercept
                .intercept(MethodDelegation.to(new ConsumerInterceptor(host)))
                //构造对应的字节码文件
                .make()
                //使用调用者的classLoader
                .load(this.getClass().getClassLoader())
                //将对象加载进jvm
                .getLoaded()
                //进行对象构造
                .getDeclaredConstructor().newInstance();
    }
}
