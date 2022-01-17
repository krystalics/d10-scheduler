package com.github.krystalics.d10.scheduler.core;

import java.util.stream.Stream;

/**
 * @Author linjiabao001
 * @Date 2022/1/17
 * @Description 流式计算、流水线的方式提高产出效率
 * 先处理Will -> WILL -> will
 * 然后是 I -> I -> i
 * 并不是等所有元素第一个map处理完再去处理下一个
 */
public class StreamTest {
    public static void main(String[] args) {
        Stream.of("Will I eat the apple?".split(" "))
                .map(w -> {
                    if(w.equals("Will")){
                        throw new RuntimeException("");
                    }
                    return w + " ";
                })
                .peek(System.out::print)
                .map(String::toUpperCase)
                .peek(System.out::print)
                .map(String::toLowerCase)
                .forEach(System.out::print);

    }
}
