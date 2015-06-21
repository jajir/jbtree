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

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;

/**
 * Junit test for {@link Node}.
 * 
 * @author jajir
 * 
 */
public class NodeTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(NodeTest.class);

    private Node node;

    @Test
    public void test_toString() throws Exception {
	logger.debug(node.toString());

	assertEquals("Node{id=0, isLeafNode=true, field=[-1, null, null]}", node.toString());

	Node n = Node.makeNode(2, 0, new Integer[] { 0, 1, 1, 3, -40, 4, 98 });

	logger.debug(n.toString());
	assertEquals("Node{id=0, isLeafNode=false, field=[0, 1, 1, 3, -40, 4, 98]}", n.toString());

    }

    @Test
    public void test_emptyNode() throws Exception {
	logger.debug(node.toString());

	verifyNode(new Integer[][] {}, true, null);
    	assertEquals(null, node.getMaxKey());
    	assertEquals(null, node.getValue(2));
    }

    @Test
    public void test_insert_leaf_oneKey() throws Exception {
	node.insert(2, 20);
	logger.debug(node.toString());

	verifyNode(new Integer[][] { { 2, 20 } }, true, null);
    }

    @Test
    public void test_insert_leaf_2nodes() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);

	verifyNode(new Integer[][] { { 1, 10 }, { 2, 20 } }, true, null);
    }

    @Test
    public void test_insert_leaf_overwriteValue() throws Exception {
	node.insert(2, 10);
	node.insert(2, 20);

	logger.debug(node.toString());

	verifyNode(new Integer[][] { { 2, 20 } }, true, null);
    }

    @Test
    public void test_insert_leaf_overwriteValue_fullNode() throws Exception {
	node.insert(2, 20);
	node.insert(1, 80);

	node.insert(2, 30);
	logger.debug(node.toString());

	verifyNode(new Integer[][] { { 1, 80 }, { 2, 30 } }, true, null);
    }

    @Test
    public void test_insert_nonLeaf() throws Exception {
	Node n = Node.makeNode(2, 0, new Integer[] { 0, 1, 1, 3, 98 });
	n.insert(4, -40);

	logger.debug(n.toString());

	assertEquals(3, n.getKeysCount());
	assertFalse("it's non leaf node", n.isLeafNode());
	List<Integer> keys = n.getKeys();
	assertTrue(keys.contains(1));
	assertTrue(keys.contains(4));
	assertEquals(Integer.valueOf(98), n.getLink());
	assertEquals("non-leaf nodes should preserver it's max key", Integer.valueOf(4),
		n.getMaxKey());
	assertEquals(Integer.valueOf(0), n.getCorrespondingNodeId(1));
	assertEquals(Integer.valueOf(1), n.getCorrespondingNodeId(3));
	assertEquals(Integer.valueOf(-40), n.getCorrespondingNodeId(4));
	assertEquals(Integer.valueOf(98), n.getCorrespondingNodeId(5));
    }

    @Test
    public void test_insert_nonLeaf_maxKey() throws Exception {
	Node n = Node.makeNode(2, 0, new Integer[] { 0, 1, 1, 2, null });
	n.insert(4, 3);

	logger.debug(n.toString());

	assertEquals(3, n.getKeysCount());
	assertFalse("it's non leaf node", n.isLeafNode());
	List<Integer> keys = n.getKeys();
	assertTrue(keys.contains(1));
	assertTrue(keys.contains(2));
	assertTrue(keys.contains(4));
	assertNull(n.getLink());
	assertEquals(Integer.valueOf(0), n.getCorrespondingNodeId(1));
	assertEquals(Integer.valueOf(1), n.getCorrespondingNodeId(2));
	assertEquals(Integer.valueOf(3), n.getCorrespondingNodeId(4));
    }

    @Test
    public void test_insert_nonLeaf_loverKey() throws Exception {
	Node n = Node.makeNode(2, 4, new Integer[] { 0, 4, null });
	n.insert(3, -30);

	logger.debug(n.toString());

	assertEquals(2, n.getKeysCount());
	assertFalse("it's non leaf node", n.isLeafNode());
	List<Integer> keys = n.getKeys();
	assertTrue(keys.contains(3));
	assertTrue(keys.contains(4));
	assertNull(n.getLink());
	assertEquals(Integer.valueOf(0), n.getCorrespondingNodeId(4));
	assertEquals(Integer.valueOf(-30), n.getCorrespondingNodeId(3));
    }

    @Test
    public void test_remove_leaf_first() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);
	Boolean ret = node.remove(1);

	assertTrue(ret);
	verifyNode(new Integer[][] { { 2, 20 } }, true, null);
    }

    @Test
    public void test_remove_leaf_last() throws Exception {
	node.insert(2, 20);
	Boolean ret = node.remove(2);

	assertTrue(ret);
	assertTrue(node.isEmpty());
	assertNull(node.getMaxValue());
	assertTrue(node.isLeafNode());
    }

    @Test
    public void test_remove_nonLeaf_last() throws Exception {
	Node n = Node.makeNode(2, 22, new Integer[] { 13, 7, 16, 8, 21, 9, null });
	Boolean ret = n.remove(9);

	logger.debug(n.toString());
	assertTrue(ret);
	assertFalse(n.isEmpty());
	assertEquals(Integer.valueOf(8), n.getMaxValue());
    }

    @Test
    public void test_remove_leaf_second() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);
	Boolean ret = node.remove(2);

	assertTrue(ret);
	verifyNode(new Integer[][] { { 1, 10 } }, true, null);
    }

    @Test
    public void test_remove_leaf_notExisting() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);
	Boolean ret = node.remove(12);

	assertFalse(ret);
	verifyNode(new Integer[][] { { 1, 10 }, { 2, 20 } }, true, null);
    }

    @Test
    public void test_remove_nonLeaf_P0_one() throws Exception {
	node = Node.makeNode(3, 2, new Integer[] { 0, 1, 1, 3, 999 });
	logger.debug(node.toString());
	Boolean ret = node.remove(1);

	assertTrue(ret);
	verifyNode(new Integer[][] { { 3, 1 } }, false, 999);
	assertEquals(Integer.valueOf(1), node.getP0());
	assertEquals(Integer.valueOf(3), node.getMaxValue());
    }

    @Test
    public void test_remove_nonLeaf_P0_zero() throws Exception {
	node = Node.makeNode(3, 2, new Integer[] { 1, 2, 888 });
	logger.debug(node.toString());
	Boolean ret = node.remove(2);

	assertTrue(ret);
	verifyNode(new Integer[][] {}, false, 888);
	assertEquals(null, node.getMaxValue());
    }

    @Test
    public void test_link() throws Exception {
	node.setLink(-10);
	node.insert(2, 20);
	node.insert(1, 10);

	verifyNode(new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -10);
    }

    @Test
    public void test_moveTopHalfOfDataTo_leaf() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);
	node.setLink(100);
	logger.debug("node  " + node.toString());

	Node node2 = new Node(2, 1, true);
	node.moveTopHalfOfDataTo(node2);

	logger.debug("node  " + node.toString());
	logger.debug("node2 " + node2.toString());
	/**
	 * First node
	 */
	assertEquals("key count is not correct", 1, node.getKeysCount());
	assertTrue(node.isLeafNode());
	List<Integer> keys = node.getKeys();
	assertTrue(keys.contains(1));
	assertEquals(Integer.valueOf(1), node.getLink());
	assertEquals("Invalid getMaxKey", Integer.valueOf(1), node.getMaxKey());

	/**
	 * Second node
	 */
	assertEquals(1, node2.getKeysCount());
	keys = node2.getKeys();
	assertTrue(keys.contains(2));
	assertTrue("target node should be leaf", node2.isLeafNode());
	assertEquals(Integer.valueOf(100), node2.getLink());
	assertEquals("Invalid getMaxKey", Integer.valueOf(2), node2.getMaxKey());
    }

    @Test
    public void test_moveTopHalfOfDataTo_nothingToMove() throws Exception {
	Node node2 = new Node(2, 11, true);
	try {
	    node.moveTopHalfOfDataTo(node2);
	    fail();
	} catch (JblinktreeException e) {
	    assertTrue(true);
	}
    }

    @Test
    public void test_moveTopHalfOfDataTo_node() throws Exception {
	Node n = Node.makeNode(2, 10, new Integer[] { 0, 1, 1, 2, 5, 9, null });
	logger.debug("node  " + n.toString());
	assertEquals("key count is not correct", 3, n.getKeysCount());

	Node node2 = new Node(2, 11, true);
	n.moveTopHalfOfDataTo(node2);

	logger.debug("node  " + n.toString());
	logger.debug("node2 " + node2.toString());
	/**
	 * First node
	 */
	assertEquals("key count is not correct", 1, n.getKeysCount());
	assertFalse(n.isLeafNode());
	List<Integer> keys = n.getKeys();
	assertTrue(keys.contains(1));
	assertEquals("next link node", Integer.valueOf(11), n.getLink());
	assertEquals("Invalid getMaxKey", Integer.valueOf(1), n.getMaxKey());

	/**
	 * Second node
	 */
	assertEquals(2, node2.getKeysCount());
	keys = node2.getKeys();
	assertTrue(keys.contains(2));
	assertFalse(node2.isLeafNode());
	assertNull("ln in new node should be null", node2.getLink());
	assertEquals("Invalid getMaxKey", Integer.valueOf(9), node2.getMaxKey());
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

	assertEquals(2, node.getKeysCount());
	assertFalse(node.isEmpty());
	assertTrue(node.isLeafNode());
	List<Integer> keys = node.getKeys();
	assertTrue(keys.contains(1));
	assertTrue(keys.contains(2));
	assertNull(node.getLink());
    }

    @Test
    public void test_isEmpty() throws Exception {
	assertTrue(node.isEmpty());
	node.insert(2, 20);

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
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 1, 2, 3, 33 });

	logger.debug(n.toString());

	Integer nodeId = n.getCorrespondingNodeId(4);

	assertNotNull("node id can't be null", nodeId);
	assertEquals("node id should be different", Integer.valueOf(33), nodeId);
    }

    @Test
    public void test_getCorrespondingNodeId_simple() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 2, 1, 3, 23 });

	logger.debug(n.toString());

	Integer nodeId = n.getCorrespondingNodeId(3);

	assertNotNull("node id can't be null", nodeId);
	assertEquals("node id should be different", Integer.valueOf(1), nodeId);
    }

    @Test
    public void test_updateNodeValue_value_was_updated() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 2, 1, 3, 23 });

	boolean ret = n.updateNodeValue(0, 3);

	assertTrue(ret);
    }

    @Test
    public void test_updateNodeValue_value_was_not_updated() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 2, 1, 3, 23 });

	boolean ret = n.updateNodeValue(0, 2);

	assertFalse(ret);
    }

    @Test
    public void test_updateNodeValue_missing_node_id() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 2, 1, 3, 23 });

	boolean ret = n.updateNodeValue(10, 2);

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
     * @param pairs
     *            required key value pairs stored in node
     * @param isLeafNode
     *            required info if it's leaf node
     * @param expectedNodeLink
     *            required value of expectect link
     */
    private void verifyNode(final Integer[][] pairs, final boolean isLeafNode,
	    final Integer expectedNodeLink) {
	logger.debug(node.toString());

	assertEquals("Expected number of key is invalid", pairs.length, node.getKeysCount());
	assertEquals("isLeafNode value is invalid", isLeafNode, node.isLeafNode());
	List<Integer> keys = node.getKeys();
	for (Integer[] pair : pairs) {
	    final Integer key = pair[0];
	    final Integer value = pair[1];
	    assertTrue("keys should contains key " + pair[0], keys.contains(pair[0]));
	    if (isLeafNode) {
		assertEquals(value, node.getValue(key));
	    } else {
		assertEquals(value, node.getCorrespondingNodeId(key));
	    }
	}
	assertEquals("Node link is invalid", expectedNodeLink, node.getLink());
	if (pairs.length > 0) {
	    final Integer expectedMaxKey = pairs[pairs.length - 1][0];
	    assertEquals("Max key value is invalid", expectedMaxKey, node.getMaxKey());
	}
    }

    @Test
    public void test_getKeysCount_leaf() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { -1, 10, 1, 10, null });

	assertEquals(1, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_leaf_empty() throws Exception {
	Node n = new Node(2, 10, true);

	assertEquals(0, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 2, 1, 3, 23 });

	assertEquals(2, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_1() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 2, 23 });

	assertEquals(1, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_empty() throws Exception {
	Node n = new Node(2, 10, false);

	assertEquals(0, n.getKeysCount());
    }

    @Test
    public void test_writeTo() throws Exception {
	Node n = Node.makeNode(2, 2, new Integer[] { 0, 2, 1, 3, 23 });

	StringBuilder buff = new StringBuilder();
	n.writeTo(buff, "    ");
	assertNotNull(buff);
	logger.debug(buff.toString());
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	node = new Node(2, 0, true);
    }

    @Override
    protected void tearDown() throws Exception {
	node = null;
	super.tearDown();
    }
}
