package com.github.krystalics.d10.scheduler.common;

import com.github.krystalics.d10.scheduler.common.constant.VersionInstance;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author linjiabao001
 * @Date 2022/2/9
 * @Description
 */
public class ComparatorTest {
    @Test
    public void comp() {
        VersionInstance instance1 = new VersionInstance();
        instance1.setPriority(12);
        instance1.setBizPriority(1);

        VersionInstance instance2 = new VersionInstance();
        instance2.setPriority(12);
        instance2.setBizPriority(2);


        VersionInstance instance3 = new VersionInstance();
        instance3.setPriority(11);
        instance3.setBizPriority(4);

        List<VersionInstance> list = new ArrayList<>();
        list.add(instance1);
        list.add(instance2);
        list.add(instance3);
        for (VersionInstance versionInstance : list) {
            System.out.println(versionInstance.getPriority() + " " + versionInstance.getBizPriority());
        }
        System.out.println();
        Collections.sort(list);
        for (VersionInstance versionInstance : list) {
            System.out.println(versionInstance.getPriority() + " " + versionInstance.getBizPriority());
        }

    }
}
