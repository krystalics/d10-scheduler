package com.github.krystalics.d10.scheduler.rpc;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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

        Assertions.assertFalse(Derived.class.isInstance(base));
        Assertions.assertTrue(Derived.class.isInstance(derived));
        Assertions.assertTrue(Base.class.isInstance(base));
        Assertions.assertTrue(Base.class.isInstance(derived));

        Assertions.assertFalse(Derived.class.isInstance(Base.class));
        Assertions.assertFalse(Derived.class.isInstance(Derived.class));
        Assertions.assertFalse(Base.class.isInstance(Base.class));
        Assertions.assertFalse(Base.class.isInstance(Derived.class));

        Assertions.assertTrue(Base.class.isAssignableFrom(Derived.class));
        Assertions.assertFalse(Derived.class.equals(Base.class));

    }

}
