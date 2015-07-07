package com.coroptis.jblinktree.integration;

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

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JbTree;
import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeUtil;
import com.coroptis.jblinktree.type.Types;

public class TreeBasicTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(TreeBasicTest.class);

    private JbTree<Integer, Integer> tree;

    private TreeUtil treeUtil;

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

	logger.debug(tree.toString());
	assertEquals(3, tree.countValues());
    }

    @Test
    public void test_insert_4_values_ascending() throws Exception {
	tree.insert(1, -10);
	tree.insert(2, -20);
	tree.insert(3, -30);
	tree.insert(4, -40);
	tree.verify();
	logger.debug(tree.toString());
    }

    @Test
    public void test_insert_4_values_descending() throws Exception {
	tree.insert(5, -50);
	tree.insert(4, -40);
	tree.insert(3, -30);
	tree.insert(2, -20);
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
    public void test_insert_50_values() throws Exception {
	for (int i = 1; i < 51; i++) {
	    logger.debug("inserting " + i);
	    tree.insert(i, -i + 10);
	    logger.debug(tree.toString());
	}

	assertEquals(50, tree.countValues());
    }

    @Test
    public void test_remove_3_values() throws Exception {
	tree.insert(1, -10);
	tree.insert(2, -20);
	tree.insert(3, -30);

	logger.debug(tree.toString());
	treeUtil.toDotFile(new File("pok.dot"));
	tree.remove(1);
	assertEquals(2, tree.countValues());
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	logger.debug(tree.toString());
	tree.remove(3);
	assertEquals(1, tree.countValues());
	logger.debug(tree.toString());
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	tree.remove(2);
	logger.debug(tree.toString());
	assertEquals(0, tree.countValues());
    }

    @Test
    public void test_insert_10_asc_remove_10_asc() throws Exception {
	insert_10_ascending();
	assertEquals(10, tree.countValues());
	verify_contains_10();

	remove_10_ascending();
	assertEquals(0, tree.countValues());
	treeUtil.toDotFile(new File("pok.dot"));
    }

    @Test
    public void test_insert_10_asc_remove_10_desc() throws Exception {
	insert_10_ascending();
	assertEquals(10, tree.countValues());
	logger.debug(tree.toString());

	verify_search_10();
	remove_10_descending();
	assertEquals(0, tree.countValues());
    }

    @Test
    public void test_insert_10_desc_remove_10_asc() throws Exception {
	insert_10_descending();
	assertEquals(10, tree.countValues());

	remove_10_ascending();
	assertEquals(0, tree.countValues());
    }

    @Test
    public void test_insert_10_desc_remove_10_desc() throws Exception {
	insert_10_descending();
	assertEquals(10, tree.countValues());

	remove_10_descending();
	assertEquals(0, tree.countValues());
    }

    private void insert_10_ascending() {
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
    }

    private void remove_10_ascending() {
	tree.remove(1);
	tree.remove(2);
	tree.remove(3);
	tree.remove(4);
	tree.remove(5);
	tree.remove(6);
	tree.remove(7);
	tree.remove(8);
	tree.remove(9);
	tree.remove(10);
    }

    private void remove_10_descending() {
	tree.remove(10);
	logger.debug(tree.toString());
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	tree.remove(9);
	logger.debug(tree.toString());
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	tree.remove(8);
	tree.remove(7);
	logger.debug(tree.toString());
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	tree.remove(6);
	tree.remove(5);
	tree.remove(4);
	tree.remove(3);
	tree.remove(2);
	tree.remove(1);
    }

    private void verify_search_10() {
	assertEquals(Integer.valueOf(-10), tree.search(1));
	assertEquals(Integer.valueOf(-20), tree.search(2));
	assertEquals(Integer.valueOf(-30), tree.search(3));
	assertEquals(Integer.valueOf(-40), tree.search(4));
	assertEquals(Integer.valueOf(-50), tree.search(5));
	assertEquals(Integer.valueOf(-60), tree.search(6));
	assertEquals(Integer.valueOf(-70), tree.search(7));
	assertEquals(Integer.valueOf(-80), tree.search(8));
	assertEquals(Integer.valueOf(-90), tree.search(9));
	assertEquals(Integer.valueOf(-100), tree.search(10));
    }

    private void verify_contains_10() {
	assertTrue(tree.containsKey(1));
	assertTrue(tree.containsKey(2));
	assertTrue(tree.containsKey(3));
	assertTrue(tree.containsKey(4));
	assertTrue(tree.containsKey(5));
	assertTrue(tree.containsKey(6));
	assertTrue(tree.containsKey(7));
	assertTrue(tree.containsKey(8));
	assertTrue(tree.containsKey(9));
	assertTrue(tree.containsKey(10));
    }

    private void insert_10_descending() {
	tree.insert(10, -100);
	tree.insert(9, -90);
	tree.insert(8, -80);
	tree.insert(7, -70);
	tree.insert(6, -60);
	tree.insert(5, -50);
	tree.insert(4, -40);
	tree.insert(3, -30);
	tree.insert(2, -20);
	tree.insert(1, -10);
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	tree = TreeBuilder.builder().setL(2).setKeyType(Types.integer())
		.setValueType(Types.integer()).build();
	treeUtil = new TreeUtil(tree);
    }

    @Override
    protected void tearDown() throws Exception {
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	tree = null;
	treeUtil = null;
	super.tearDown();
    }

}
