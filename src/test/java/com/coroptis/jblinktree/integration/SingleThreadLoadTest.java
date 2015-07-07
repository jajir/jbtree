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
import java.util.Random;

import junit.framework.TestCase;

import org.junit.Test;

import com.coroptis.jblinktree.JbTree;
import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeUtil;
import com.coroptis.jblinktree.type.Types;

/**
 * Verify tree structure in case of random numbers in one thread.
 * 
 * @author jajir
 * 
 */
public class SingleThreadLoadTest extends TestCase {

    private JbTree<Integer, Integer> tree;

    private TreeUtil treeUtil;

    @Test
    public void test_insert() throws Exception {
	Random random = new Random();
	for (int i = 0; i < 10000; i++) {
	    Integer integer = random.nextInt(100) + 1;
	    tree.insert(integer, integer);
	}
	treeUtil.toDotFile(new File("pok.dot"));

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
