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

package com.github.krystalics.d10.scheduler.common.utils;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.github.krystalics.d10.scheduler.common.constant.CommonConstants;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

/**
 * from dolphin-scheduler
 * 使用guava-retrying拓展包、方便干净的重试机制
 */
public class RetryerUtils {
    private static Retryer<Boolean> defaultRetryerResultCheck;
    private static Retryer<Boolean> defaultRetryerResultNoCheck;
    private static Retryer<Boolean> longRetryerResultCheck;
    private static Retryer<Boolean> longRetryerResultNoCheck;

    private RetryerUtils() {
        throw new UnsupportedOperationException("Construct RetryerUtils");
    }

    private static Retryer<Boolean> getLongDefaultRetryerResultNoCheck() {
        if (longRetryerResultNoCheck == null) {
            longRetryerResultNoCheck = RetryerBuilder
                    .<Boolean>newBuilder()
                    .retryIfException()
                    .withWaitStrategy(WaitStrategies.fixedWait(CommonConstants.COMMON_RETRY_STOP_TIME, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(CommonConstants.COMMON_RETRY_TIMES * 10))
                    .build();
        }
        return longRetryerResultNoCheck;
    }

    private static Retryer<Boolean> getLongDefaultRetryerResultCheck() {
        if (longRetryerResultCheck == null) {
            longRetryerResultCheck = RetryerBuilder
                    .<Boolean>newBuilder()
                    .retryIfResult(Boolean.FALSE::equals)
                    .retryIfException()
                    .withWaitStrategy(WaitStrategies.fixedWait(CommonConstants.COMMON_RETRY_STOP_TIME, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(CommonConstants.COMMON_RETRY_TIMES * 10))
                    .build();
        }
        return longRetryerResultCheck;
    }


    private static Retryer<Boolean> getDefaultRetryerResultNoCheck() {
        if (defaultRetryerResultNoCheck == null) {
            defaultRetryerResultNoCheck = RetryerBuilder
                    .<Boolean>newBuilder()
                    .retryIfException()
                    .withWaitStrategy(WaitStrategies.fixedWait(CommonConstants.COMMON_RETRY_STOP_TIME, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(CommonConstants.COMMON_RETRY_TIMES))
                    .build();
        }
        return defaultRetryerResultNoCheck;
    }

    /**
     * Gets default retryer.
     * the retryer will retry 3 times if exceptions throw
     * and wait 1 second between each retry
     *
     * @param checkResult true means the callable must return true before retrying
     *                    false means that retry callable only throw exceptions
     * @return the default retryer
     */
    public static Retryer<Boolean> getDefaultRetryer(boolean checkResult) {
        return checkResult ? getDefaultRetryer() : getDefaultRetryerResultNoCheck();
    }

    public static Retryer<Boolean> getDefaultRetryerLong(boolean checkResult) {
        return checkResult ? getLongDefaultRetryerResultCheck() : getLongDefaultRetryerResultNoCheck();
    }

    /**
     * Gets default retryer.
     * the retryer will retry 3 times if exceptions throw
     * and wait 3 second between each retry
     *
     * @return the default retryer
     */
    public static Retryer<Boolean> getDefaultRetryer() {
        if (defaultRetryerResultCheck == null) {
            defaultRetryerResultCheck = RetryerBuilder
                    .<Boolean>newBuilder()
                    .retryIfResult(Boolean.FALSE::equals)
                    .retryIfException()
                    .withWaitStrategy(WaitStrategies.fixedWait(CommonConstants.COMMON_RETRY_STOP_TIME, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterAttempt(CommonConstants.COMMON_RETRY_TIMES))
                    .build();
        }
        return defaultRetryerResultCheck;
    }

    /**
     * Use RETRYER to invoke the Callable
     *
     * @param callable    the callable
     * @param checkResult true means that retry callable before returning true
     *                    false means that retry callable only throw exceptions
     * @return the final result of callable
     * @throws ExecutionException the execution exception
     * @throws RetryException     the retry exception
     */
    public static Boolean retryCall(final Callable<Boolean> callable, boolean checkResult) throws ExecutionException, RetryException {
        return getDefaultRetryer(checkResult).call(callable);
    }

    public static Boolean retryCallLong(final Callable<Boolean> callable, boolean checkResult) throws ExecutionException, RetryException {
        return getDefaultRetryerLong(checkResult).call(callable);
    }

    /**
     * Use RETRYER to invoke the Callable before returning true
     *
     * @param callable the callable
     * @return the boolean
     * @throws ExecutionException the execution exception
     * @throws RetryException     the retry exception
     */
    public static Boolean retryCall(final Callable<Boolean> callable) throws ExecutionException, RetryException {
        return retryCall(callable, true);
    }

    /**
     * Use RETRYER to invoke the Callable before returning true
     *
     * @param callable the callable
     * @return the boolean
     * @throws ExecutionException the execution exception
     * @throws RetryException     the retry exception
     */
    public static Boolean retryCallLong(final Callable<Boolean> callable) throws ExecutionException, RetryException {
        return retryCallLong(callable, true);
    }
}
