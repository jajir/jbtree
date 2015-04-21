package com.coroptis.jblinktree;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeBasicTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(TreeBasicTest.class);

    private NodeStore nodeStore;

    private Tree tree;

    @Test
    public void testJustOneNode() throws Exception {

	tree.insert(3, -30);
	tree.insert(1, -10);

	logger.debug(tree.toString());
	assertEquals(2, tree.countValues());
    }

    @Test
    public void testSimpleSplitting() throws Exception {
	tree.insert(3, -30);
	tree.insert(1, -10);
	tree.insert(5, -50);

	logger.debug("node 2: " + nodeStore.get(2).toString());
	logger.debug("node 1: " + nodeStore.get(1).toString());
	logger.debug("node 0: " + nodeStore.get(0).toString());

	logger.debug(tree.toString());
	assertEquals(3, tree.countValues());
    }

    @Test
    public void test_10_values() throws Exception {
	tree.insert(1, -10);
	tree.insert(2, -20);
	tree.insert(3, -30);
	logger.debug(tree.toString());
	tree.insert(4, -40);
	tree.verify();
	logger.debug(tree.toString());
	tree.insert(5, -50);
	tree.insert(6, -60);

	logger.debug(tree.toString());
	assertEquals(3, tree.countValues());
    }

    @Test
    public void test_1000_values() throws Exception {
	for (int i = 1; i < 1001; i++) {
	    logger.debug("inserting " + i);
	}
	
	logger.debug(tree.toString());
	assertEquals(3, tree.countValues());
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	nodeStore = new NodeStoreImpl();
	tree = new Tree(nodeStore);
    }

    @Override
    protected void tearDown() throws Exception {
	tree = null;
	nodeStore = null;
	super.tearDown();
    }

}
