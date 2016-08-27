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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

        verifyNode(node, new Integer[][] {}, true, -1);
        assertEquals(null, node.getMaxKey());
        assertEquals(null, node.getValueByKey(2));
    }

    @Test(expected = NullPointerException.class)
    public void test_getValueByKey_null_key() {
        node.getValueByKey(null);
    }

    @Test
    public void test_insert_leaf_oneKey() throws Exception {
        node.insert(2, 20);
        logger.debug(node.toString());

        verifyNode(node, new Integer[][] { { 2, 20 } }, true, -1);
    }

    @Test
    public void test_insert_leaf_2nodes() throws Exception {
        node.insert(2, 20);
        node.insert(1, 10);

        verifyNode(node, new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -1);
    }

    @Test
    public void test_insert_leaf_overwriteValue() throws Exception {
        node.insert(2, 10);
        node.insert(2, 20);

        logger.debug(node.toString());

        verifyNode(node, new Integer[][] { { 2, 20 } }, true, -1);
    }

    @Test
    public void test_insert_leaf_overwriteValue_fullNode() throws Exception {
        node.insert(2, 20);
        node.insert(1, 80);
        logger.debug(node.toString());

        node.insert(2, 30);

        verifyNode(node, new Integer[][] { { 1, 80 }, { 2, 30 } }, true, -1);
    }

    @Test
    public void test_insert_nonLeaf() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(3, 0,
                new Integer[] { 0, 1, 1, 3, 98 });
        n.insert(4, -40);

        logger.debug(n.toString());

        assertEquals(3, n.getKeyCount());
        assertFalse("it's non leaf node", n.isLeafNode());
        List<Integer> keys = n.getKeys();
        assertTrue(keys.contains(1));
        assertTrue(keys.contains(4));
        assertEquals(Integer.valueOf(98), n.getLink());
        assertEquals("non-leaf nodes should preserver it's max key",
                Integer.valueOf(4), n.getMaxKey());
        assertEquals(Integer.valueOf(0), n.getCorrespondingNodeId(1));
        assertEquals(Integer.valueOf(1), n.getCorrespondingNodeId(3));
        assertEquals(Integer.valueOf(-40), n.getCorrespondingNodeId(4));
        assertEquals(Integer.valueOf(98), n.getCorrespondingNodeId(5));
    }

    @Test
    public void test_insert_nonLeaf_maxKey() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(3, 0,
                new Integer[] { 0, 1, 1, 2, -1 });
        n.insert(4, 3);

        logger.debug(n.toString());

        assertEquals(3, n.getKeyCount());
        assertFalse("it's non leaf node", n.isLeafNode());
        List<Integer> keys = n.getKeys();
        assertTrue(keys.contains(1));
        assertTrue(keys.contains(2));
        assertTrue(keys.contains(4));
        assertEquals(Integer.valueOf(-1), n.getLink());
        assertEquals(Integer.valueOf(0), n.getCorrespondingNodeId(1));
        assertEquals(Integer.valueOf(1), n.getCorrespondingNodeId(2));
        assertEquals(Integer.valueOf(3), n.getCorrespondingNodeId(4));
    }

    @Test
    public void test_insert_nonLeaf_loverKey() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(4,
                new Integer[] { 0, 4, 0 });
        n.insert(3, -30);

        logger.debug(n.toString());

        assertEquals(2, n.getKeyCount());
        assertFalse("it's non leaf node", n.isLeafNode());
        List<Integer> keys = n.getKeys();
        assertTrue(keys.contains(3));
        assertTrue(keys.contains(4));
        assertEquals(Integer.valueOf(0), n.getLink());
        assertEquals(Integer.valueOf(0), n.getCorrespondingNodeId(4));
        assertEquals(Integer.valueOf(-30), n.getCorrespondingNodeId(3));
    }

    @Test(expected = NullPointerException.class)
    public void test_insert_key_null() throws Exception {
        node.insert(null, 2);
    }

    @Test(expected = NullPointerException.class)
    public void test_insert_value_null() throws Exception {
        node.insert(4, null);
    }

    @Test
    public void test_remove_leaf_first() throws Exception {
        node.insert(2, 20);
        node.insert(1, 10);
        logger.debug(node.toString());
        Integer ret = node.remove(1);

        assertEquals(Integer.valueOf(10), ret);
        verifyNode(node, new Integer[][] { { 2, 20 } }, true, -1);
    }

    @Test
    public void test_remove_leaf_last() throws Exception {
        node.insert(2, 20);
        Integer ret = node.remove(2);

        assertEquals(Integer.valueOf(20), ret);
        assertTrue(node.isEmpty());
        assertNull(node.getMaxKey());
        assertTrue(node.isLeafNode());
    }

    @Test
    public void test_remove_nonLeaf_last() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(22,
                new Integer[] { 13, 7, 16, 8, 21, 9, -1 });
        Integer ret = n.remove(9);

        logger.debug(n.toString());
        assertEquals(Integer.valueOf(21), ret);
        assertFalse(n.isEmpty());
        assertEquals(Integer.valueOf(8), n.getMaxKey());
    }

    @Test
    public void test_remove_leaf_second() throws Exception {
        node.insert(2, 20);
        node.insert(1, 10);
        Integer ret = node.remove(2);

        assertEquals(Integer.valueOf(20), ret);
        verifyNode(node, new Integer[][] { { 1, 10 } }, true, -1);
    }

    @Test
    public void test_remove_leaf_notExisting() throws Exception {
        node.insert(2, 20);
        node.insert(1, 10);
        Integer ret = node.remove(12);

        assertNull(ret);
        verifyNode(node, new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -1);
    }

    @Test
    public void test_remove_nonLeaf_P0_one() throws Exception {
        node = nr.makeNodeFromIntegers(2, new Integer[] { 0, 1, 1, 3, 999 });
        logger.debug(node.toString());
        Integer ret = node.remove(1);

        assertEquals(Integer.valueOf(0), ret);
        verifyNode(node, new Integer[][] { { 3, 1 } }, false, 999);
        assertEquals(Integer.valueOf(3), node.getMaxKey());
    }

    @Test
    public void test_remove_nonLeaf_P0_zero() throws Exception {
        node = nr.makeNodeFromIntegers(2, new Integer[] { 1, 2, 888 });
        logger.debug(node.toString());
        Integer ret = node.remove(2);

        assertEquals(Integer.valueOf(1), ret);
        verifyNode(node, new Integer[][] {}, false, 888);
        assertEquals(null, node.getMaxKey());
    }

    @Test(expected = NullPointerException.class)
    public void test_remove_key_null() throws Exception {
        node.remove(null);
    }

    @Test
    public void test_setLink() throws Exception {
        node.setLink(-10);
        node.insert(2, 20);
        node.insert(1, 10);

        verifyNode(node, new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -10);
    }

    @Test(expected = NullPointerException.class)
    public void test_setLink_null() throws Exception {
        node.setLink(null);
    }

    @Test
    public void test_moveTopHalfOfDataTo_leaf() throws Exception {
        node.insert(2, 20);
        node.insert(1, 10);
        node.setLink(100);
        logger.debug("node1  " + node.toString());

        NodeImpl<Integer, Integer> node2 = new NodeImpl<Integer, Integer>(1,
                true, nr.getTreeData().getLeafNodeDescriptor());
        node.moveTopHalfOfDataTo(node2);

        logger.debug("node1  " + node.toString());
        logger.debug("node2 " + node2.toString());
        /**
         * First node
         */
        assertEquals("key count is not correct", 1, node.getKeyCount());
        assertTrue(node.isLeafNode());
        List<Integer> keys = node.getKeys();
        assertTrue(keys.contains(1));
        assertEquals(Integer.valueOf(1), node.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(1), node.getMaxKey());

        /**
         * Second node
         */
        assertEquals(1, node2.getKeyCount());
        keys = node2.getKeys();
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
        List<Integer> keys = n.getKeys();
        assertTrue(keys.contains(1));
        assertEquals("next link node", Integer.valueOf(11), n.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(1), n.getMaxKey());

        /**
         * Second node
         */
        assertEquals(2, node2.getKeyCount());
        keys = node2.getKeys();
        assertTrue(keys.contains(2));
        assertFalse(node2.isLeafNode());
        assertEquals("ln in new node should be null", Integer.valueOf(-1),
                node2.getLink());
        assertEquals("Invalid getMaxKey", Integer.valueOf(9),
                node2.getMaxKey());
    }

    @Test
    public void test_insert_leaf_tooMuchNodes() throws Exception {
        node.insert(2, 20);
        node.insert(1, 10);
        try {
            node.insert(4, 40);
            fail();
        } catch (JblinktreeException e) {
            assertTrue(true);
        }

        assertEquals(2, node.getKeyCount());
        assertFalse(node.isEmpty());
        assertTrue(node.isLeafNode());
        List<Integer> keys = node.getKeys();
        assertTrue(keys.contains(1));
        assertTrue(keys.contains(2));
        assertEquals(Integer.valueOf(-1), node.getLink());
    }

    @Test
    public void test_isEmpty() throws Exception {
        assertTrue(node.isEmpty());
        logger.debug(node.toString());
        node.insert(2, 20);
        logger.debug(node.toString());

        assertFalse(node.isEmpty());
        assertTrue(node.isLeafNode());
    }

    @Test
    public void test_getMaxKey() throws Exception {
        node.insert(2, 20);
        node.insert(1, 10);

        logger.debug(node.toString());
        assertEquals(Integer.valueOf(2), node.getMaxKey());
    }

    @Test
    public void test_getCorrespondingNodeId_return_link() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 1, 2, 3, 33 });

        logger.debug(n.toString());

        Integer nodeId = n.getCorrespondingNodeId(4);

        assertNotNull("node id can't be null", nodeId);
        assertEquals("node id should be different", Integer.valueOf(33),
                nodeId);
    }

    @Test
    public void test_getCorrespondingNodeId_simple() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        logger.debug(n.toString());

        Integer nodeId = n.getCorrespondingNodeId(3);

        assertNotNull("node id can't be null", nodeId);
        assertEquals("node id should be different", Integer.valueOf(1), nodeId);
    }

    @Test
    public void test_updateNodeValue_value_was_updated() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        boolean ret = n.updateKeyForValue(0, 3);

        assertTrue(ret);
    }

    @Test
    public void test_updateNodeValue_value_was_not_updated() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        boolean ret = n.updateKeyForValue(0, 2);

        assertFalse(ret);
    }

    @Test
    public void test_updateNodeValue_missing_node_id() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        boolean ret = n.updateKeyForValue(10, 2);

        assertFalse(ret);
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
            final Integer expectedNodeLink) {
        logger.debug(n.toString());

        assertEquals("Expected number of key is invalid", pairs.length,
                n.getKeyCount());
        assertEquals("isLeafNode value is invalid", isLeafNode, n.isLeafNode());
        List<Integer> keys = n.getKeys();
        for (Integer[] pair : pairs) {
            final Integer key = pair[0];
            final Integer value = pair[1];
            assertTrue("keys should contains key " + pair[0],
                    keys.contains(pair[0]));
            if (isLeafNode) {
                assertEquals(value, n.getValueByKey(key));
            } else {
                assertEquals(value, n.getCorrespondingNodeId(key));
            }
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
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        assertEquals(2, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_1() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 23 });

        assertEquals(1, n.getKeyCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_empty() throws Exception {
        Node<Integer, Integer> n = new NodeImpl<Integer, Integer>(10, false,
                nr.getTreeData().getLeafNodeDescriptor());

        assertEquals(0, n.getKeyCount());
    }

    @Test
    public void test_writeTo() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        StringBuilder buff = new StringBuilder();
        n.writeTo(buff, "    ");
        assertNotNull(buff);
        logger.debug(buff.toString());
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
