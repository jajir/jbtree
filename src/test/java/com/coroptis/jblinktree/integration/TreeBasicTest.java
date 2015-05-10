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

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JbTree;
import com.coroptis.jblinktree.TreeBuilder;

public class TreeBasicTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(TreeBasicTest.class);

    private JbTree tree;

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
    public void test_4_values() throws Exception {
	tree.insert(1, -10);
	tree.insert(2, -20);
	tree.insert(3, -30);
	logger.debug(tree.toString());
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
    public void test_10_values_ascending() throws Exception {
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
    public void test_10_values_descending() throws Exception {
	tree.insert(10, -100);
	tree.insert(9, -90);
	tree.insert(8, -80);
	logger.debug(tree.toString());
	tree.insert(7, -70);
	assertEquals(4, tree.countValues());
	tree.insert(6, -60);
	tree.insert(5, -50);
	assertEquals(6, tree.countValues());
	tree.insert(4, -40);
	tree.insert(3, -30);
	assertEquals(8, tree.countValues());
	tree.insert(2, -20);
	tree.insert(1, -10);
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

    @Test
    public void test_remove_simple() throws Exception {
	tree.insert(1, -10);
	tree.insert(2, -20);
	tree.insert(3, -30);

	logger.debug(tree.toString());
	tree.remove(1);
	tree.verify();
	logger.debug(tree.toString());
	assertEquals(2, tree.countValues());

	tree.remove(3);
	tree.verify();
	logger.debug(tree.toString());
	assertEquals(1, tree.countValues());

	tree.remove(2);
	tree.verify();
	logger.debug(tree.toString());
	assertEquals(0, tree.countValues());
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	tree = TreeBuilder.builder().setL(2).build();
    }

    @Override
    protected void tearDown() throws Exception {
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	tree = null;
	super.tearDown();
    }

}
