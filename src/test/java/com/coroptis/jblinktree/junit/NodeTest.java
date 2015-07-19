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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

/**
 * Junit test for {@link NodeImpl}.
 * 
 * @author jajir
 * 
 */
public class NodeTest {

    private Logger logger = LoggerFactory.getLogger(NodeTest.class);

    private Node<Integer, Integer> node;

    private TypeDescriptor<Integer> intDescriptor;

    @Test
    public void test_toString() throws Exception {
	logger.debug(node.toString());

	assertEquals("Node{id=0, isLeafNode=true, field=[-1], flag=-77}", node.toString());

	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 0, false, new Integer[] { 0, 1,
		1, 3, -40, 4, 98 });

	logger.debug(n.toString());
	assertEquals("Node{id=0, isLeafNode=false, field=[0, 1, 1, 3, -40, 4, 98], flag=0}",
		n.toString());

    }

    @Test
    public void test_emptyNode() throws Exception {
	logger.debug(node.toString());

	verifyNode(new Integer[][] {}, true, -1);
	assertEquals(null, node.getMaxKey());
	assertEquals(null, node.getValue(2));
    }

    @Test(expected = NullPointerException.class)
    public void test_getValue_null_key() {
	node.getValue(null);
    }

    @Test
    public void test_insert_leaf_oneKey() throws Exception {
	node.insert(2, 20);
	logger.debug(node.toString());

	verifyNode(new Integer[][] { { 2, 20 } }, true, -1);
    }

    @Test
    public void test_insert_leaf_2nodes() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);

	verifyNode(new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -1);
    }

    @Test
    public void test_insert_leaf_overwriteValue() throws Exception {
	node.insert(2, 10);
	node.insert(2, 20);

	logger.debug(node.toString());

	verifyNode(new Integer[][] { { 2, 20 } }, true, -1);
    }

    @Test
    public void test_insert_leaf_overwriteValue_fullNode() throws Exception {
	node.insert(2, 20);
	node.insert(1, 80);
	logger.debug(node.toString());

	node.insert(2, 30);

	verifyNode(new Integer[][] { { 1, 80 }, { 2, 30 } }, true, -1);
    }

    @Test
    public void test_insert_nonLeaf() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(3, 0, false, new Integer[] { 0, 1,
		1, 3, 98 });
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
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(3, 0, false, new Integer[] { 0, 1,
		1, 2, -1 });
	n.insert(4, 3);

	logger.debug(n.toString());

	assertEquals(3, n.getKeysCount());
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
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 4, false, new Integer[] { 0, 4,
		0 });
	n.insert(3, -30);

	logger.debug(n.toString());

	assertEquals(2, n.getKeysCount());
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
	Boolean ret = node.remove(1);

	assertTrue(ret);
	verifyNode(new Integer[][] { { 2, 20 } }, true, -1);
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
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 22, false, new Integer[] { 13,
		7, 16, 8, 21, 9, -1 });
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
	verifyNode(new Integer[][] { { 1, 10 } }, true, -1);
    }

    @Test
    public void test_remove_leaf_notExisting() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);
	Boolean ret = node.remove(12);

	assertFalse(ret);
	verifyNode(new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -1);
    }

    @Test
    public void test_remove_nonLeaf_P0_one() throws Exception {
	node = NodeImpl.makeNodeFromIntegers(3, 2, false, new Integer[] { 0, 1, 1, 3, 999 });
	logger.debug(node.toString());
	Boolean ret = node.remove(1);

	assertTrue(ret);
	verifyNode(new Integer[][] { { 3, 1 } }, false, 999);
	assertEquals(Integer.valueOf(3), node.getMaxValue());
    }

    @Test
    public void test_remove_nonLeaf_P0_zero() throws Exception {
	node = NodeImpl.makeNodeFromIntegers(3, 2, false, new Integer[] { 1, 2, 888 });
	logger.debug(node.toString());
	Boolean ret = node.remove(2);

	assertTrue(ret);
	verifyNode(new Integer[][] {}, false, 888);
	assertEquals(null, node.getMaxValue());
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

	verifyNode(new Integer[][] { { 1, 10 }, { 2, 20 } }, true, -10);
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

	NodeImpl<Integer, Integer> node2 = new NodeImpl<Integer, Integer>(2, 1, true,
		intDescriptor, intDescriptor);
	node.moveTopHalfOfDataTo(node2);

	logger.debug("node1  " + node.toString());
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
	NodeImpl<Integer, Integer> node2 = new NodeImpl<Integer, Integer>(2, 11, true,
		intDescriptor, intDescriptor);
	try {
	    node.moveTopHalfOfDataTo(node2);
	    fail();
	} catch (JblinktreeException e) {
	    assertTrue(true);
	}
    }

    @Test
    public void test_moveTopHalfOfDataTo_node() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 10, false, new Integer[] { 0,
		1, 1, 2, 5, 9, -1 });
	logger.debug("node  " + n.toString());
	assertEquals("key count is not correct", 3, n.getKeysCount());

	NodeImpl<Integer, Integer> node2 = new NodeImpl<Integer, Integer>(2, 11, true,
		intDescriptor, intDescriptor);
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
	assertEquals("ln in new node should be null", Integer.valueOf(-1), node2.getLink());
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
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 1,
		2, 3, 33 });

	logger.debug(n.toString());

	Integer nodeId = n.getCorrespondingNodeId(4);

	assertNotNull("node id can't be null", nodeId);
	assertEquals("node id should be different", Integer.valueOf(33), nodeId);
    }

    @Test
    public void test_getCorrespondingNodeId_simple() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 2,
		1, 3, 23 });

	logger.debug(n.toString());

	Integer nodeId = n.getCorrespondingNodeId(3);

	assertNotNull("node id can't be null", nodeId);
	assertEquals("node id should be different", Integer.valueOf(1), nodeId);
    }

    @Test
    public void test_updateNodeValue_value_was_updated() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 2,
		1, 3, 23 });

	boolean ret = n.updateNodeValue(0, 3);

	assertTrue(ret);
    }

    @Test
    public void test_updateNodeValue_value_was_not_updated() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 2,
		1, 3, 23 });

	boolean ret = n.updateNodeValue(0, 2);

	assertFalse(ret);
    }

    @Test
    public void test_updateNodeValue_missing_node_id() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 2,
		1, 3, 23 });

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
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, true, new Integer[] { -77,
		10, 1, 10, -1 });

	assertEquals(2, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_leaf_empty() throws Exception {
	Node<Integer, Integer> n = new NodeImpl<Integer, Integer>(2, 10, true, intDescriptor,
		intDescriptor);

	assertEquals(0, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 2,
		1, 3, 23 });

	assertEquals(2, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_1() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 2,
		23 });

	assertEquals(1, n.getKeysCount());
    }

    @Test
    public void test_getKeysCount_nonLeaf_empty() throws Exception {
	Node<Integer, Integer> n = new NodeImpl<Integer, Integer>(2, 10, false, intDescriptor,
		intDescriptor);

	assertEquals(0, n.getKeysCount());
    }

    @Test
    public void test_writeTo() throws Exception {
	Node<Integer, Integer> n = NodeImpl.makeNodeFromIntegers(2, 2, false, new Integer[] { 0, 2,
		1, 3, 23 });

	StringBuilder buff = new StringBuilder();
	n.writeTo(buff, "    ");
	assertNotNull(buff);
	logger.debug(buff.toString());
    }

    @Before
    public void setUp() throws Exception {
	intDescriptor = new TypeDescriptorInteger();
	node = new NodeImpl<Integer, Integer>(2, 0, true, intDescriptor, intDescriptor);
    }

    @After
    public void tearDown() throws Exception {
	node = null;
	intDescriptor = null;
    }
}
