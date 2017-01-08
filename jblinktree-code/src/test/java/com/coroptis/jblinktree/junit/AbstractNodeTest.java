package com.coroptis.jblinktree.junit;

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

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.coroptis.jblinktree.NodeUtilRule;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.Wrapper;
import com.coroptis.jblinktree.util.JblinktreeException;

/**
 * Junit test for {@link Node}.
 *
 * @author jajir
 *
 */
public abstract class AbstractNodeTest {

    private Logger logger = LoggerFactory.getLogger(AbstractNodeTest.class);

    private TypeDescriptorInteger tdi;

    @Rule
    public NodeUtilRule nodeUtil = new NodeUtilRule();

    abstract protected NodeBuilder getNb();

    @Test
    public void test_makeNode() throws Exception {
        Node<Integer, Integer> n =
                getNb().setNodeId(45).setLink(98).addKeyValuePair(1, 0)
                        .addKeyValuePair(3, 1).setLeafNode(false).build();

        nodeUtil.verifyNode(n, new Integer[][] { { 1, 0 }, { 3, 1 } }, false,
                98, 45);

    }

    @Test
    public void test_compareKey() throws Exception {
        Node<Integer, Integer> n =
                getNb().setNodeId(45).setLink(98).setLeafNode(false)
                        .addKeyValuePair(1, 0).addKeyValuePair(3, 1).build();

        assertEquals(0, n.compareKey(1, Wrapper.make(3, tdi)));
    }

    @Test
    public void test_toString_empty_node() throws Exception {
        Node<Integer, Integer> n1 = getNb().build();

        logger.debug(n1.toString());

        assertEquals("Node{id=0, isLeafNode=true, field=[], flag=-77, link=-1}",
                n1.toString());
    }

    @Test
    public void test_toString_filed_node() throws Exception {
        Node<Integer, Integer> n2 =
                getNb().setLink(98).setLeafNode(false).addKeyValuePair(1, 0)
                        .addKeyValuePair(3, 1).addKeyValuePair(4, -40).build();

        logger.debug(n2.toString());
        assertEquals(
                "Node{id=0, isLeafNode=false, field=[<1, 0>, <3, 1>, <4, -40>], flag=-3, link=98}",
                n2.toString());
    }

    @Test
    public void test_emptyNode() throws Exception {
        Node<Integer, Integer> n = getNb().build();
        logger.debug(n.toString());

        nodeUtil.verifyNode(n, new Integer[][] {}, true, -1, 0);
        assertEquals(null, n.getMaxKey());
    }

    /**
     * Test verify that storing more pairs into node is accepted.
     *
     * @throws Exception
     *             default exception
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_insertAtPosition_nodeIsFull() throws Exception {
        Node<Integer, Integer> n =
                getNb().setL(2).setNodeId(0).setLeafNode(false).setLink(98)
                        .addKeyValuePair(1, 0).addKeyValuePair(3, 1).build();

        logger.debug(n.toString());
        n.insertAtPosition(Wrapper.make(4, tdi), -40, 2);
    }

    @Test
    public void test_insertAtPosition_highest() throws Exception {
        Node<Integer, Integer> n =
                getNb().setNodeId(45).setLeafNode(false).setLink(98)
                        .addKeyValuePair(1, 0).addKeyValuePair(3, 1).build();

        logger.debug(n.toString());
        n.insertAtPosition(Wrapper.make(4, tdi), -40, 2);

        nodeUtil.verifyNode(n,
                new Integer[][] { { 1, 0 }, { 3, 1 }, { 4, -40 } }, false, 98,
                45);
    }

    @Test
    public void test_insertAtPosition_lowest() throws Exception {
        Node<Integer, Integer> n =
                getNb().setNodeId(12).setLeafNode(false).setLink(98)
                        .addKeyValuePair(1, 0).addKeyValuePair(3, 1).build();

        logger.debug(n.toString());
        n.insertAtPosition(Wrapper.make(0, tdi), -10, 0);

        nodeUtil.verifyNode(n,
                new Integer[][] { { 0, -10 }, { 1, 0 }, { 3, 1 } }, false, 98,
                12);
    }

    @Test(expected = NullPointerException.class)
    public void test_insertAtPosition_key_null() throws Exception {
        Node<Integer, Integer> n = getNb().build();

        n.insertAtPosition(null, 2, 0);
    }

    @Test(expected = NullPointerException.class)
    public void test_insertAtPosition_value_null() throws Exception {
        Node<Integer, Integer> n = getNb().build();

        n.insertAtPosition(Wrapper.make(4, tdi), null, 0);
    }

    @Test
    public void test_removeAtPosition() throws Exception {
        Node<Integer, Integer> n = getNb().setNodeId(12).setLeafNode(false)
                .setLink(98).addKeyValuePair(1, 0).addKeyValuePair(3, 1)
                .addKeyValuePair(4, 2).build();
        logger.debug(n.toString());

        n.removeAtPosition(1);
        nodeUtil.verifyNode(n, new Integer[][] { { 1, 0 }, { 4, 2 } }, false,
                98, 12);
    }

    @Test
    public void test_removeAtPosition_highest() throws Exception {
        Node<Integer, Integer> n = getNb().setNodeId(12).setLeafNode(false)
                .setLink(98).addKeyValuePair(1, 0).addKeyValuePair(3, 1)
                .addKeyValuePair(4, 2).build();
        logger.debug(n.toString());

        n.removeAtPosition(2);
        nodeUtil.verifyNode(n, new Integer[][] { { 1, 0 }, { 3, 1 } }, false,
                98, 12);
    }

    @Test
    public void test_removeAtPosition_lowest() throws Exception {
        Node<Integer, Integer> n = getNb().setNodeId(12).setLeafNode(false)
                .setLink(98).addKeyValuePair(1, 0).addKeyValuePair(3, 1)
                .addKeyValuePair(4, 2).build();
        logger.debug(n.toString());

        n.removeAtPosition(0);
        nodeUtil.verifyNode(n, new Integer[][] { { 3, 1 }, { 4, 2 } }, false,
                98, 12);
    }

    @Test
    public void test_setLink_simple() throws Exception {
        Node<Integer, Integer> n = getNb().build();

        n.setLink(-10);

        nodeUtil.verifyNode(n, new Integer[][] {}, true, -10, 0);
    }

    @Test
    public void test_setLink() throws Exception {
        Node<Integer, Integer> n = getNb().build();

        n.setLink(-10);
        n.insertAtPosition(Wrapper.make(1, tdi), 10, 0);
        n.insertAtPosition(Wrapper.make(2, tdi), 20, 1);

        nodeUtil.verifyNode(n, new Integer[][] { { 1, 10 }, { 2, 20 } }, true,
                -10, 0);
    }

    @Test(expected = NullPointerException.class)
    public void test_setLink_null() throws Exception {
        Node<Integer, Integer> n = getNb().build();

        n.setLink(null);
    }

    @Test
    public void test_moveTopHalfOfDataTo_leaf() throws Exception {
        Node<Integer, Integer> node1 =
                getNb().setNodeId(0).setLeafNode(false).setLink(100)
                        .addKeyValuePair(1, 10).addKeyValuePair(2, 20).build();

        node1.setFlag(Node.FLAG_LEAF_NODE);
        logger.debug("node1: " + node1.toString());

        Node<Integer, Integer> node2 =
                getNb().setNodeId(1).setLeafNode(true).build();
        node1.moveTopHalfOfDataTo(node2);

        logger.debug("node1: " + node1.toString());
        logger.debug("node2: " + node2.toString());
        /**
         * First node
         */
        assertEquals("key count is not correct", 1, node1.getKeyCount());
        assertTrue(node1.isLeafNode());
        List<Integer> keys = nodeUtil.getKeys(node1);
        assertTrue(keys.contains(1));
        assertEquals(Integer.valueOf(1), node1.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(1),
                node1.getMaxKey().getValue());

        /**
         * Second node
         */
        assertEquals(1, node2.getKeyCount());
        keys = nodeUtil.getKeys(node2);
        assertTrue(keys.contains(2));
        assertTrue("target node should be leaf", node2.isLeafNode());
        assertEquals(Integer.valueOf(100), node2.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(2),
                node2.getMaxKey().getValue());
    }

    @Test(expected = JblinktreeException.class)
    public void test_moveTopHalfOfDataTo_nothingToMove() throws Exception {
        Node<Integer, Integer> n1 = getNb().build();
        Node<Integer, Integer> n2 =
                getNb().setNodeId(11).setLeafNode(true).build();

        n1.moveTopHalfOfDataTo(n2);
    }

    @Test
    public void test_moveTopHalfOfDataTo_node() throws Exception {
        Node<Integer, Integer> n = getNb().setL(10).setNodeId(3)
                .setLeafNode(false).setLink(-1).addKeyValuePair(1, 0)
                .addKeyValuePair(2, 1).addKeyValuePair(9, 5).build();

        logger.debug("node  " + n.toString());
        assertEquals("key count is not correct", 3, n.getKeyCount());

        Node<Integer, Integer> node2 =
                getNb().setNodeId(11).setLeafNode(true).build();
        n.moveTopHalfOfDataTo(node2);

        logger.debug("node  " + n.toString());
        logger.debug("node2 " + node2.toString());
        /**
         * First node
         */
        assertEquals("key count is not correct", 1, n.getKeyCount());
        assertFalse(n.isLeafNode());
        List<Integer> keys = nodeUtil.getKeys(n);
        assertTrue(keys.contains(1));
        assertEquals("next link node", Integer.valueOf(11), n.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(1),
                n.getMaxKey().getValue());

        /**
         * Second node
         */
        assertEquals(2, node2.getKeyCount());
        keys = nodeUtil.getKeys(node2);
        assertTrue(keys.contains(2));
        assertFalse(node2.isLeafNode());
        assertEquals("ln in new node should be null", Integer.valueOf(-1),
                node2.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(9),
                node2.getMaxKey().getValue());
    }

    @Test
    public void test_isEmpty() throws Exception {
        Node<Integer, Integer> n = getNb().build();

        assertTrue(n.isEmpty());
        logger.debug(n.toString());
        n.insertAtPosition(Wrapper.make(2, tdi), 20, 0);
        logger.debug(n.toString());

        assertFalse(n.isEmpty());
        assertTrue(n.isLeafNode());
    }

    @Test
    public void test_getMaxKey() throws Exception {
        Node<Integer, Integer> n =
                getNb().addKeyValuePair(1, 10).addKeyValuePair(2, 20).build();

        logger.debug(n.toString());
        assertEquals(Integer.valueOf(2), n.getMaxKey().getValue());
    }

    @Test
    public void test_getKeysCount_leaf() throws Exception {
        Node<Integer, Integer> n = getNb().setNodeId(2).setLeafNode(true)
                .setLink(-1).addKeyValuePair(10, -77).addKeyValuePair(8, -8)
                .build();

        assertEquals(2, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_leaf_empty() throws Exception {
        Node<Integer, Integer> n =
                getNb().setNodeId(10).setLeafNode(true).build();

        assertEquals(0, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf() throws Exception {
        Node<Integer, Integer> n = getNb().setNodeId(2).addKeyValuePair(2, 0)
                .addKeyValuePair(3, 1).setLink(23).build();

        assertEquals(2, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_1() throws Exception {
        Node<Integer, Integer> n = getNb().setNodeId(2).setLeafNode(false)
                .setLink(23).addKeyValuePair(2, 0).build();

        assertEquals(1, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_empty() throws Exception {
        Node<Integer, Integer> n =
                getNb().setNodeId(10).setLeafNode(false).build();

        assertEquals(0, n.getKeyCount());
    }

    @Test
    public void test_equals_same() throws Exception {
        Node<Integer, Integer> n = getNb().build();

        assertTrue(n.equals(n));
    }

    @Before
    public void setUp() throws Exception {
        tdi = new TypeDescriptorInteger();
    }

    @After
    public void tearDown() throws Exception {
        tdi = null;
    }
}
