package com.coroptis.jblinktree;

/*
 * #%L
 * jblinktree
 * %%
 * Copyright (C) 2015 coroptis
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.NodeBuilder.NodeImpl;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

public class NodeBuilderTest {

    private Logger logger = LoggerFactory.getLogger(NodeBuilderTest.class);

    @Test
    public void test_build_variable() throws Exception {

        Node<Integer, Integer> n = NodeBuilder.builder().setL(3)
                .setKeyTypeDescriptor(new TypeDescriptorInteger())
                .setValueTypeDescriptor(new TypeDescriptorInteger())
                .setImplementation(NodeImpl.variableLength).setNodeId(12)
                .setLink(85).addKeyValuePair(1, 10).addKeyValuePair(2, 20)
                .addKeyValuePair(3, 30).build();

        logger.debug(n.toString());
        assertEquals(Integer.valueOf(85), n.getLink());
        assertEquals(3, n.getKeyCount());
    }

    @Test
    public void test_build_fixed() throws Exception {

        Node<Integer, Integer> n = NodeBuilder.builder().setL(3)
                .setKeyTypeDescriptor(new TypeDescriptorInteger())
                .setValueTypeDescriptor(new TypeDescriptorInteger())
                .setImplementation(NodeImpl.fixedLength).setNodeId(12)
                .setLink(85).addKeyValuePair(1, 10).addKeyValuePair(2, 20)
                .addKeyValuePair(3, 30).build();

        logger.debug(n.toString());
        assertEquals(Integer.valueOf(85), n.getLink());
        assertEquals(3, n.getKeyCount());
    }

}
