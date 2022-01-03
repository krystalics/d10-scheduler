/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.krystalics.d10.scheduler.start.sharding.impl;


import com.github.krystalics.d10.scheduler.common.constant.Pair;
import com.github.krystalics.d10.scheduler.start.sharding.JobInstance;
import com.github.krystalics.d10.scheduler.start.sharding.ShardingStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author krysta
 * @date 2022/01/03
 * 按照节点地址排序后，进行scope范围的划分
 */

public final class ScopeStrategy implements ShardingStrategy {

    @Override
    public List<JobInstance> sharding(final List<JobInstance> jobInstances, final int shardingTotalCount) {
        if (jobInstances.isEmpty()) {
            return Collections.emptyList();
        }

        Collections.sort(jobInstances);
        final int size = jobInstances.size();
        final int step = shardingTotalCount / size;

        long left = 0;
        long right = 0;
        for (int i = 0; i < size; i++) {
            right = left;
            right = right + step;
            Pair<Long, Long> scope;
            if (i == size - 1) {
                scope = Pair.of(left, (long) shardingTotalCount);
            } else {
                scope = Pair.of(left, right);
            }
            jobInstances.get(i).setTaskIds(scope);
            left = right + 1;
        }
        return jobInstances;
    }


}
