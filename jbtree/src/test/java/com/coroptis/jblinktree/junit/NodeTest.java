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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.NodeRule;
import com.coroptis.jblinktree.NodeUtilRule;

/**
 * Junit test for {@link NodeImpl}.
 *
 * @author jajir
 *
 */
public class NodeTest {

    private Logger logger = LoggerFactory.getLogger(NodeTest.class);

    @Rule
    public NodeRule nr = new NodeRule(2);

    @Rule
    public NodeUtilRule nodeUtil = new NodeUtilRule();

    private Node<Integer, Integer> node;

    @Test
    public void test_toString() throws Exception {
        logger.debug(node.toString());

        assertEquals("Node{id=0, isLeafNode=true, field=[], flag=-77, link=-1}",
                node.toString());

        Node<Integer, Integer> n = nr.makeNodeFromIntegers(0,
                new Integer[] { 0, 1, 1, 3, -40, 4, 98 });

        logger.debug(n.toString());
        assertEquals(
                "Node{id=0, isLeafNode=false, field=[<1, 0>, <3, 1>, <4, -40>], flag=0, link=98}",
                n.toString());

    }

    @Test
    public void test_emptyNode() throws Exception {
        logger.debug(node.toString());

        verifyNode(node, new Integer[][] {}, true, -1, 0);
        assertEquals(null, node.getMaxKey());
    }

    /**
     * Test verify that storing more pairs into node is accepted.
     *
     * @throws Exception
     *             default exception
     */
    @Test
    public void test_insertAtPosition_nodeIsFull() throws Exception {
        Node<Integer, Integer> n =
                nr.makeNodeFromIntegers(2, 0, new Integer[] { 0, 1, 1, 3, 98 });
        logger.debug(n.toString());
        n.insertAtPosition(4, -40, 2);

        logger.debug(n.toString());

        assertEquals(3, n.getKeyCount());
    }

    @Test
    public void test_insertAtPosition_highest() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(3, 45,
                new Integer[] { 0, 1, 1, 3, 98 });
        logger.debug(n.toString());
        n.insertAtPosition(4, -40, 2);

        verifyNode(n, new Integer[][] { { 1, 0 }, { 3, 1 }, { 4, -40 } }, false,
                98, 45);
    }

    @Test
    public void test_insertAtPosition_lowest() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(3, 12,
                new Integer[] { 0, 1, 1, 3, 98 });
        logger.debug(n.toString());
        n.insertAtPosition(0, -10, 0);

        verifyNode(n, new Integer[][] { { 0, -10 }, { 1, 0 }, { 3, 1 } }, false,
                98, 12);
    }

    @Test(expected = NullPointerException.class)
    public void test_insertAtPosition_key_null() throws Exception {
        node.insertAtPosition(null, 2, 0);
    }

    @Test(expected = NullPointerException.class)
    public void test_insertAtPosition_value_null() throws Exception {
        node.insertAtPosition(4, null, 0);
    }

    @Test
    public void test_removeAtPosition() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(3, 12,
                new Integer[] { 0, 1, 1, 3, 2, 4, 98 });
        logger.debug(n.toString());

        n.removeAtPosition(1);
        verifyNode(n, new Integer[][] { { 1, 0 }, { 4, 2 } }, false, 98, 12);
    }

    @Test
    public void test_removeAtPosition_highest() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(3, 12,
                new Integer[] { 0, 1, 1, 3, 2, 4, 98 });
        logger.debug(n.toString());

        n.removeAtPosition(2);
        verifyNode(n, new Integer[][] { { 1, 0 }, { 3, 1 } }, false, 98, 12);
    }

    @Test
    public void test_removeAtPosition_lowest() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(3, 12,
                new Integer[] { 0, 1, 1, 3, 2, 4, 98 });
        logger.debug(n.toString());

        n.removeAtPosition(0);
        verifyNode(n, new Integer[][] { { 3, 1 }, { 4, 2 } }, false, 98, 12);
    }

    @Test
    public void test_setLink() throws Exception {
        node.setLink(-10);
        node.insertAtPosition(1, 10, 0);
        node.insertAtPosition(2, 20, 1);

        verifyNode(node, new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -10,
                0);
    }

    @Test(expected = NullPointerException.class)
    public void test_setLink_null() throws Exception {
        node.setLink(null);
    }

    @Test
    public void test_moveTopHalfOfDataTo_leaf() throws Exception {
        Node<Integer, Integer> node1 = nr.makeNodeFromIntegers(3, 0,
                new Integer[] { 10, 1, 20, 2, 100 });
        node1.setFlag(Node.FLAG_LEAF_NODE);
        logger.debug("node1: " + node1.toString());

        NodeImpl<Integer, Integer> node2 = new NodeImpl<Integer, Integer>(1,
                true, nr.getTreeData().getLeafNodeDescriptor());
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
                node1.getMaxKey());

        /**
         * Second node
         */
        assertEquals(1, node2.getKeyCount());
        keys = nodeUtil.getKeys(node2);
        assertTrue(keys.contains(2));
        assertTrue("target node should be leaf", node2.isLeafNode());
        assertEquals(Integer.valueOf(100), node2.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(2),
                node2.getMaxKey());
    }

    @Test
    public void test_moveTopHalfOfDataTo_nothingToMove() throws Exception {
        NodeImpl<Integer, Integer> node2 = new NodeImpl<Integer, Integer>(11,
                true, nr.getTreeData().getLeafNodeDescriptor());
        try {
            node.moveTopHalfOfDataTo(node2);
            fail();
        } catch (JblinktreeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void test_moveTopHalfOfDataTo_node() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(10,
                new Integer[] { 0, 1, 1, 2, 5, 9, -1 });
        logger.debug("node  " + n.toString());
        assertEquals("key count is not correct", 3, n.getKeyCount());

        NodeImpl<Integer, Integer> node2 = new NodeImpl<Integer, Integer>(11,
                true, nr.getTreeData().getLeafNodeDescriptor());
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
        assertEquals("Invalid getMaxKey", Integer.valueOf(1), n.getMaxKey());

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
                node2.getMaxKey());
    }

    @Test
    public void test_isEmpty() throws Exception {
        assertTrue(node.isEmpty());
        logger.debug(node.toString());
        node.insertAtPosition(2, 20, 0);
        logger.debug(node.toString());

        assertFalse(node.isEmpty());
        assertTrue(node.isLeafNode());
    }

    @Test
    public void test_getMaxKey() throws Exception {
        node.insertAtPosition(1, 10, 0);
        node.insertAtPosition(2, 20, 1);

        logger.debug(node.toString());
        assertEquals(Integer.valueOf(2), node.getMaxKey());
    }

    /**
     * Verify that node have following basic attributes:
     * <ul>
     * <li>number of keys is correct</li>
     * <li>values of keys are correct</li>
     * <li>values stored in node are correct</li>
     * <li>next key is correct</li>
     * <li>distinguish between leaf and non-leaf node is correct</li>
     * </ul>
     *
     * @param n
     *            required verified node
     * @param pairs
     *            required key value pairs stored in node
     * @param isLeafNode
     *            required info if it's leaf node
     * @param expectedNodeLink
     *            required value of expectect link
     */
    private void verifyNode(final Node<Integer, Integer> n,
            final Integer[][] pairs, final boolean isLeafNode,
            final Integer expectedNodeLink, final Integer expectedNodeId) {
        logger.debug(n.toString());

        assertEquals("Expected number of key is invalid", pairs.length,
                n.getKeyCount());
        assertEquals("isLeafNode value is invalid", isLeafNode, n.isLeafNode());
        assertEquals("nodeId is invalid", expectedNodeId, n.getId());
        List<Integer> keys = nodeUtil.getKeys(n);
        for (Integer[] pair : pairs) {
            final Integer key = pair[0];
            final Integer value = pair[1];
            assertTrue("keys should contains key " + pair[0],
                    keys.contains(pair[0]));
            assertEquals(value, nodeUtil.getValueByKey(n, key));
        }
        assertEquals("Node link is invalid", expectedNodeLink, n.getLink());
        if (pairs.length > 0) {
            final Integer expectedMaxKey = pairs[pairs.length - 1][0];
            assertEquals("Max key value is invalid", expectedMaxKey,
                    n.getMaxKey());
        }
    }

    @Test
    public void test_getKeysCount_leaf() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { -77, 10, 1, 10, -1 });

        assertEquals(2, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_leaf_empty() throws Exception {
        Node<Integer, Integer> n = new NodeImpl<Integer, Integer>(10, true,
                nr.getTreeData().getLeafNodeDescriptor());

        assertEquals(0, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf() throws Exception {
        Node<Integer, Integer> n =
                nr.makeNodeFromIntegers(2, new Integer[] { 0, 2, 1, 3, 23 });

        assertEquals(2, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_1() throws Exception {
        Node<Integer, Integer> n =
                nr.makeNodeFromIntegers(2, new Integer[] { 0, 2, 23 });

        assertEquals(1, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_empty() throws Exception {
        Node<Integer, Integer> n = new NodeImpl<Integer, Integer>(10, false,
                nr.getTreeData().getLeafNodeDescriptor());

        assertEquals(0, n.getKeyCount());
    }

    @Test
    public void test_equals_same() throws Exception {
        assertTrue(node.equals(node));
    }

    @Before
    public void setUp() throws Exception {
        node = new NodeImpl<Integer, Integer>(0, true,
                nr.getTreeData().getLeafNodeDescriptor());
    }

    @After
    public void tearDown() throws Exception {
        node = null;
    }
}
