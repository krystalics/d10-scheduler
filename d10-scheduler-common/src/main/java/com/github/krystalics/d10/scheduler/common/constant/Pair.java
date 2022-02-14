package com.github.krystalics.d10.scheduler.common.constant;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author linjiabao001
 * @date 2021/10/6
 * @description
 */
@Data
@AllArgsConstructor
public class Pair<L, R> implements Serializable {
    private static final long serialVersionUID = 1L;

    L l;
    R r;

    public static <L, R> Pair<L, R> of(L l, R r) {

        return new Pair<>(l, r);
    }
}
