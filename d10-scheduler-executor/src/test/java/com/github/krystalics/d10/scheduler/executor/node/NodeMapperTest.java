package com.github.krystalics.d10.scheduler.executor.node;

import com.github.krystalics.d10.scheduler.dao.entity.Node;
import com.github.krystalics.d10.scheduler.dao.mapper.NodeMapper;
import com.github.krystalics.d10.scheduler.executor.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author linjiabao001
 * @date 2022/2/3
 * @description
 */
public class NodeMapperTest extends BaseTest {

    @Autowired
    private NodeMapper nodeMapper;

    @Test
    public void insert() {
        Node node = new Node();
        node.setNodeAddress("127.0.0.1:10088");
        nodeMapper.insert(node);
    }
}
