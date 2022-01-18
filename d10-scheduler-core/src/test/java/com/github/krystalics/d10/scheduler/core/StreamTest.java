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
        long count = Stream.of("Will I eat the apple?".split(" "))
                .map(w -> {
                    try {
                        if (w.equals("Will")) {
                            throw new RuntimeException("Will error");
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }

                    return w + " ";
                })
                .peek(System.out::print)
                .map(String::toUpperCase)
                .peek(System.out::print)
                .map(String::toLowerCase)
                .count();

        System.out.println(count);

    }
}
