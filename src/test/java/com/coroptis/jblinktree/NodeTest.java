package com.coroptis.jblinktree;

import static org.junit.Assert.*;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(NodeTest.class);

    private Node node;

    @Test
    public void test_emptyNode() throws Exception {
	logger.debug(node.toString());
	assertEquals(0, node.getKeysCount());
	assertTrue(node.isLeafNode());
	List<Integer> keys = node.getKeys();
	assertFalse(keys.contains(2));
	assertNull(node.getLink());
	assertEquals(null, node.getMaxKey());
	assertEquals(null, node.getValue(2));
    }

    @Test
    public void test_insert_oneKey() throws Exception {
	node.insert(2, 20);
	logger.debug(node.toString());

	assertEquals(1, node.getKeysCount());
	assertTrue(node.isLeafNode());
	List<Integer> keys = node.getKeys();
	assertTrue(keys.contains(2));
	assertNull(node.getLink());
	assertEquals(Integer.valueOf(2), node.getMaxKey());
	assertEquals("Unable to find inserted key", Integer.valueOf(20), node.getValue(2));
	assertNotNull("lock should be null", node.getLock());
    }

    @Test
    public void test_insert_2nodes() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);

	logger.debug(node.toString());

	assertEquals(2, node.getKeysCount());
	assertTrue(node.isLeafNode());
	List<Integer> keys = node.getKeys();
	assertTrue(keys.contains(1));
	assertTrue(keys.contains(2));
	assertNull(node.getLink());
	assertEquals(Integer.valueOf(2), node.getMaxKey());
	assertEquals(Integer.valueOf(20), node.getValue(2));
	assertEquals(Integer.valueOf(10), node.getValue(1));
    }

    @Test
    public void test_insert_overwriteValue() throws Exception {
	node.insert(2, 20);
	node.insert(1, 80);

	logger.debug(node.toString());

	assertEquals(2, node.getKeysCount());
	assertTrue(node.isLeafNode());
	List<Integer> keys = node.getKeys();
	assertTrue(keys.contains(1));
	assertTrue(keys.contains(2));
	assertNull(node.getLink());
	assertEquals(Integer.valueOf(2), node.getMaxKey());
	assertEquals(Integer.valueOf(20), node.getValue(2));
	assertEquals(Integer.valueOf(80), node.getValue(1));
    }

    @Test
    public void test_insert_nonLeafNode() throws Exception {
	Node n = Node.makeNode(0, new Integer[]{0, 1, 1, 3, null});
	n.insert(4, -40);

	logger.debug(n.toString());

	assertEquals(2, n.getKeysCount());
	assertFalse("it's non LEF NODE",n.isLeafNode());
	List<Integer> keys = n.getKeys();
	assertTrue(keys.contains(1));
	assertTrue(keys.contains(4));
	assertNull(n.getLink());
	assertEquals(Integer.valueOf(4), n.getMaxKey());
	assertEquals(Integer.valueOf(0), n.getCorrespondingNodeId(1));
	assertEquals(Integer.valueOf(1), n.getCorrespondingNodeId(4));
	assertEquals(Integer.valueOf(-40), n.getCorrespondingNodeId(5));
    }
    
    @Test
    public void test_link() throws Exception {
	node.setLink(-10);
	node.insert(2, 20);
	node.insert(1, 10);

	logger.debug(node.toString());
	assertEquals(Integer.valueOf(-10), node.getLink());
    }

    @Test
    public void test_moveTopHalfOfDataTo() throws Exception {
	node.insert(2, 20);
	node.insert(1, 10);
	node.setLink(100);
	logger.debug("node  " + node.toString());

	Node node2 = new Node(1, true);
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
	assertTrue(node2.isLeafNode());
	assertEquals(Integer.valueOf(100), node2.getLink());
	assertEquals("Invalid getMaxKey", Integer.valueOf(2), node2.getMaxKey());
    }

    @Test
    public void test_insert_tooMuchNodes() throws Exception {
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
    public void test_getCorrespondingNodeId_bigger_key() throws Exception {
	Node n = Node.makeNode(2, new Integer[] { 0, 1, 2, 3, null });

	logger.debug(n.toString());

	Integer nodeId = n.getCorrespondingNodeId(4);

	assertNotNull("node id can't be null", nodeId);
	assertEquals("node id should be different", Integer.valueOf(2), nodeId);
    }

    @Test
    public void test_getCorrespondingNodeId_simple() throws Exception {
	Node n = Node.makeNode(2, new Integer[] { 0, 2, 1, 3, null });

	logger.debug(n.toString());

	Integer nodeId = n.getCorrespondingNodeId(3);

	assertNotNull("node id can't be null", nodeId);
	assertEquals("node id should be different", Integer.valueOf(1), nodeId);
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	node = new Node(0, true);
    }

    @Override
    protected void tearDown() throws Exception {
	node = null;
	super.tearDown();
    }
}
