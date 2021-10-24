package com.github.krystalics.d10.scheduler.core.time.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 二维元数据
 * @param <A>
 * @param <B>
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Tuple<A, B> implements Serializable {

    private A a;

    private B b;

    public static <A, B> Tuple<A, B> of(A a, B b){
        return new Tuple<>(a, b);
    }

}
