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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeImpl;
import com.coroptis.jblinktree.JbTreeService;
import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.NodeStore;
import com.coroptis.jblinktree.type.Types;

/**
 * Junit test form {@link JbTreeImpl}.
 * 
 * @author jajir
 * 
 */
public class JbTreeTest {

    private JbTreeImpl<Integer, Integer> jbTree;

    private NodeStore<Integer> nodeStore;

    private JbTreeTool<Integer, Integer> jbTreeTool;

    private JbTreeService<Integer> jbTreeService;

    private Node<Integer, Integer> rootNode;

    private Object[] mocks;

    @Test
    public void test_constructor() throws Exception {
	/**
	 * All tested functionality is done in setup.
	 */
    }

    @Test
    public void test_insert_null_key() throws Exception {
	EasyMock.replay(mocks);

	try {
	    jbTree.insert(null, 3);
	    fail();
	} catch (NullPointerException e) {
	    assertTrue(true);
	}

	EasyMock.verify(mocks);
    }

    @Test
    public void test_insert_null_value() throws Exception {
	EasyMock.replay(mocks);

	try {
	    jbTree.insert(3, null);
	    fail();
	} catch (NullPointerException e) {
	    assertTrue(true);
	}

	EasyMock.verify(mocks);
    }

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
	nodeStore = EasyMock.createMock(NodeStore.class);
	rootNode = EasyMock.createMock(NodeImpl.class);
	jbTreeTool = EasyMock.createMock(JbTreeTool.class);
	jbTreeService = EasyMock.createMock(JbTreeService.class);
	mocks = new Object[] { nodeStore, rootNode, jbTreeTool, jbTreeService };

	EasyMock.expect(jbTreeTool.createRootNode()).andReturn(1);
	EasyMock.replay(mocks);
	jbTree = new JbTreeImpl<Integer, Integer>(3, nodeStore, jbTreeTool, jbTreeService,
		Types.integer(), Types.integer());
	EasyMock.verify(mocks);
	EasyMock.reset(mocks);

    }

    @After
    public void tearDown() throws Exception {
	rootNode = null;
	jbTreeTool = null;
	jbTree = null;
	nodeStore = null;
	jbTreeService = null;
	mocks = null;
    }

}
