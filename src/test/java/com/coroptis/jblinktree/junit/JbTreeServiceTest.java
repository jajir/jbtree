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
import java.util.Stack;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeService;
import com.coroptis.jblinktree.JbTreeServiceImpl;
import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.NodeStore;

/**
 * Tests for {@link JbTreeTool}
 * 
 * @author jajir
 * 
 */
public class JbTreeServiceTest extends TestCase {

    private JbTreeService<Integer> treeService;

    private JbTreeTool<Integer, Integer> treeTool;

    private NodeStore<Integer> nodeStore;

    private NodeImpl<Integer, Integer> n1;

    private NodeImpl<Integer, Integer> n2;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_findLeafNodeId() throws Exception {
	final Stack<Integer> stack = new Stack<Integer>();
	EasyMock.expect(nodeStore.get(2)).andReturn((Node) n1);
	EasyMock.expect(n1.getId()).andReturn(2);
	EasyMock.expect(n1.isLeafNode()).andReturn(false);

	EasyMock.expect(n1.getCorrespondingNodeId(12)).andReturn(60);
	EasyMock.expect(n1.getLink()).andReturn(98);

	EasyMock.expect(nodeStore.get(60)).andReturn((Node) n2);
	EasyMock.expect(n2.isLeafNode()).andReturn(true);
	EasyMock.expect(n2.getId()).andReturn(62);

	EasyMock.replay(nodeStore, treeTool, n1, n2);
	Integer ret = treeService.findLeafNodeId(12, stack, 2);

	assertEquals(Integer.valueOf(62), ret);
	EasyMock.verify(nodeStore, treeTool, n1, n2);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
	super.setUp();
	nodeStore = EasyMock.createMock(NodeStore.class);
	treeTool = EasyMock.createMock(JbTreeTool.class);
	treeService = new JbTreeServiceImpl<Integer, Integer>(nodeStore, treeTool);
	n1 = EasyMock.createMock(NodeImpl.class);
	n2 = EasyMock.createMock(NodeImpl.class);
    }

    @Override
    protected void tearDown() throws Exception {
	n1 = null;
	n2 = null;
	treeTool = null;
	treeService = null;
	nodeStore = null;
	super.tearDown();
    }

}
