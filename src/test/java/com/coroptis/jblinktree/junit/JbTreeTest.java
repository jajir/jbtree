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

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeImpl;
import com.coroptis.jblinktree.JbTreeService;
import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeStore;
import com.google.common.collect.Lists;

/**
 * Junit test form {@link JbTreeImpl}.
 * 
 * @author jajir
 * 
 */
public class JbTreeTest extends TestCase {

    private JbTreeImpl jbTree;

    private NodeStore nodeStore;

    private JbTreeTool jbTreeTool;

    private JbTreeService jbTreeService;

    private Node rootNode;

    @Test
    public void test_constructor() throws Exception {
	/**
	 * All tested functionality is done in setup.
	 */
    }

    @Test
    public void test_insert_null_key() throws Exception {
	EasyMock.replay(nodeStore, rootNode);

	try {
	    jbTree.insert(null, 3);
	    fail();
	} catch (NullPointerException e) {
	    assertTrue(true);
	}

	EasyMock.verify(nodeStore, rootNode);
    }

    @Test
    public void test_insert_null_value() throws Exception {
	EasyMock.replay(nodeStore, rootNode);

	try {
	    jbTree.insert(3, null);
	    fail();
	} catch (NullPointerException e) {
	    assertTrue(true);
	}

	EasyMock.verify(nodeStore, rootNode);
    }

    @Test
    public void test_countValues_just_leaf_node() throws Exception {
	EasyMock.expect(nodeStore.get(0)).andReturn(rootNode);
	EasyMock.expect(rootNode.isLeafNode()).andReturn(true);
	EasyMock.expect(rootNode.getKeysCount()).andReturn(3);
	EasyMock.replay(nodeStore, rootNode);

	int ret = jbTree.countValues();

	assertEquals(3, ret);
	EasyMock.verify(nodeStore, rootNode);
    }

    @Test
    public void test_countValues_one_nonLeaf_node() throws Exception {
	Node n1 = EasyMock.createMock(Node.class);
	EasyMock.expect(nodeStore.get(0)).andReturn(rootNode);
	EasyMock.expect(rootNode.isLeafNode()).andReturn(false);
	EasyMock.expect(rootNode.getNodeIds()).andReturn(Lists.newArrayList(5));

	EasyMock.expect(nodeStore.get(5)).andReturn(n1);
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.expect(n1.getKeysCount()).andReturn(3);
	EasyMock.replay(nodeStore, rootNode, n1);

	int ret = jbTree.countValues();

	assertEquals(3, ret);
	EasyMock.verify(nodeStore, rootNode, n1);
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	nodeStore = EasyMock.createMock(NodeStore.class);
	rootNode = EasyMock.createMock(Node.class);
	jbTreeTool = EasyMock.createMock(JbTreeTool.class);
	jbTreeService = EasyMock.createMock(JbTreeService.class);
	nodeStore.writeNode(new Node(3, 0, true));
	EasyMock.replay(nodeStore);
	jbTree = new JbTreeImpl(3, nodeStore, jbTreeTool, jbTreeService);
	EasyMock.verify(nodeStore);
	EasyMock.reset(nodeStore);

    }

    @Override
    protected void tearDown() throws Exception {
	rootNode = null;
	jbTreeTool = null;
	jbTree = null;
	nodeStore = null;
	jbTreeService = null;
	super.tearDown();
    }

}
