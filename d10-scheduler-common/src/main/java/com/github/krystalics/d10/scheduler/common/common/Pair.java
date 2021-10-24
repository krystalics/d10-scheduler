package com.github.krystalics.d10.scheduler.common.common;

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

    public static <L, R> Pair<L, R> of(L l, R r) {

        return new Pair<>(l, r);
    }
}
