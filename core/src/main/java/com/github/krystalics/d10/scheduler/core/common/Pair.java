package com.github.krystalics.d10.scheduler.core.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Data
@AllArgsConstructor
public class Pair<L, R> {
    L l;
    R r;
}
