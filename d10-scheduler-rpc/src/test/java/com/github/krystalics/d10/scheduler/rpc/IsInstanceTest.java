package com.github.krystalics.d10.scheduler.rpc;

import org.junit.Test;

/**
 * @author linjiabao001
 * @date 2022/1/29
 * @description
 */
public class IsInstanceTest {
    class Base {
    }

    class Derived extends Base {
    }

    @Test
    public void test() {
        //isInstance只会由 父类延伸至子类 的object对象，具体的class进行判断是不行的
        //它的作用是判断，当前的对象是否可以由前面调用它的class去代表
        Base base = new Base();
        Derived derived = new Derived();

        System.out.println(Derived.class.isInstance(base));
        System.out.println(Base.class.isInstance(base));

        System.out.println(Derived.class.isInstance(derived));
        System.out.println(Base.class.isInstance(derived));

        System.out.println(Derived.class.isInstance(Base.class));
        System.out.println(Base.class.isInstance(Base.class));
        System.out.println(Base.class.isInstance(Derived.class));

        System.out.println(Base.class.isAssignableFrom(Derived.class));
        System.out.println(Derived.class.equals(Base.class));
        System.out.println(Base.class.equals(Base.class));
    }

}
