package com.coroptis.jblinktree;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TreeBasicTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(TreeBasicTest.class);

    private NodeStoreImpl nodeStore;

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
    public void test_4_values() throws Exception {
	tree.insert(1, -10);
	tree.insert(2, -20);
	tree.insert(3, -30);
	tree.insert(4, -40);
	tree.verify();
	logger.debug(tree.toString());
    }

    @Test
    public void test_overwriting_values() throws Exception {
	assertNull(tree.insert(1, -10));
	assertNull(tree.insert(2, -20));
	assertNull(tree.insert(3, -30));
	assertNull(tree.insert(4, -40));
	assertEquals(Integer.valueOf(-10), tree.insert(1, -100));
	tree.verify();
	logger.debug(tree.toString());
    }

    @Test
    public void test_10_values() throws Exception {
	tree.insert(1, -10);
	tree.insert(2, -20);
	tree.insert(3, -30);
	tree.insert(4, -40);
	tree.insert(5, -50);
	tree.insert(6, -60);
	tree.insert(7, -70);
	tree.insert(8, -80);
	tree.insert(9, -90);
	tree.insert(10, -100);
	tree.verify();

	logger.debug(tree.toString());
	assertEquals(10, tree.countValues());
    }

    @Test
    public void test_100_values() throws Exception {
	for (int i = 1; i < 101; i++) {
	    logger.debug("inserting " + i);
	    tree.insert(i, -i + 10);
	    logger.debug(tree.toString());
	}

	assertEquals(100, tree.countValues());
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	nodeStore = new NodeStoreImpl();
	tree = new Tree(2, nodeStore);
    }

    @Override
    protected void tearDown() throws Exception {
	assertEquals("All locks should be unlocked ",0, nodeStore.countLockedNodes());
	tree = null;
	nodeStore = null;
	super.tearDown();
    }

}
