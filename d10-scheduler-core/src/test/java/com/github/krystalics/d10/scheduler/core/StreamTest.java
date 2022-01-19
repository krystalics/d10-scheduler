package com.github.krystalics.d10.scheduler.core;

import org.junit.jupiter.api.Test;

import java.util.Date;
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

    private volatile boolean stop = false;

    /**
     *  测试证明 parallel stream的
     *  中断是起作用的
     *
     * @throws InterruptedException
     */
    @Test
    public void parallelInterrupted() throws InterruptedException {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stop = true;

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            stop = false;
        }).start();

        int i = 0;
        while (true) {

            try {
                System.out.println("dd");
                Thread.sleep(1000);
                try {
                    long count = Stream.of("I am a good boy I am a good boy. I am a good boy I am a good boy I am a good boy".split(" "))
                            .parallel()
                            .map(this::interruptSomething)
                            .filter(this::filterG)
                            .filter(this::filterG)
                            .count();
                } catch (RuntimeException ex) {
                    if (ex.getMessage().equals("interrupted")) {
                        throw new RuntimeException("i am broken");
                    }
                }
                System.out.println(i++ + "the end");
            } catch (Exception e) {
                System.out.println(e.toString());
            }

        }


    }


    public String interruptSomething(String item) {

        if (stop) {
            System.out.println(Thread.currentThread().getName());
            throw new RuntimeException("interrupted");
        }

        System.out.println(item);

        return item;
    }

    public boolean filterG(String s) {

        return true;
    }
}
