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

import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.JbTreeToolImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.NodeStore;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

/**
 * Tests for {@link JbTreeTool}
 * 
 * @author jajir
 * 
 */
public class JbTreeToolTest extends TestCase {

    private JbTreeTool<Integer, Integer> jbTreeTool;

    private NodeStore<Integer, Integer> nodeStore;

    private NodeImpl<Integer, Integer> n1;

    private NodeImpl<Integer, Integer> n2;

    @Test
    public void test_moveRightNonLeafNode_isLeafNode() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.replay(nodeStore, n1, n2);

	try {
	    jbTreeTool.moveRightNonLeafNode(n1, 10);
	    fail();
	} catch (JblinktreeException e) {
	    assertTrue(e.getMessage().contains("method is for non-leaf"));
	}
	EasyMock.verify(nodeStore, n1, n2);
    }

    @Test
    public void test_moveRightNonLeafNode_noMove() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.expect(n1.getCorrespondingNodeId(10)).andReturn(4);
	EasyMock.expect(n1.getLink()).andReturn(12);
	EasyMock.replay(nodeStore, n1, n2);
	Node<Integer, Integer> ret = jbTreeTool.moveRightNonLeafNode(n1, 10);

	assertEquals(n1, ret);
	EasyMock.verify(nodeStore, n1, n2);
    }

    @Test
    public void test_moveRightNonLeafNode_nextNodeId_isNull() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.expect(n1.getCorrespondingNodeId(10)).andReturn(null);
	EasyMock.expect(n1.getLink()).andReturn(NodeImpl.EMPTY_INT);
	EasyMock.replay(nodeStore, n1, n2);
	Node<Integer, Integer> ret = jbTreeTool.moveRightNonLeafNode(n1, 10);

	assertEquals(n1, ret);
	EasyMock.verify(nodeStore, n1, n2);
    }

    @Test
    public void test_moveRightNonLeafNode_move() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.expect(n1.getCorrespondingNodeId(10)).andReturn(4);
	EasyMock.expect(n1.getLink()).andReturn(4);
	EasyMock.expect(nodeStore.getAndLock(4)).andReturn((Node) n2);
	EasyMock.expect(n1.getId()).andReturn(5);
	nodeStore.unlockNode(5);
	EasyMock.expect(n2.getCorrespondingNodeId(10)).andReturn(6);
	EasyMock.expect(n2.getLink()).andReturn(60);
	EasyMock.replay(nodeStore, n1, n2);
	Node<Integer, Integer> ret = jbTreeTool.moveRightNonLeafNode(n1, 10);

	assertEquals(n2, ret);
	EasyMock.verify(nodeStore, n1, n2);
    }

    @Test
    public void test_moveRightLeafNode_isNonLeafNode() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.replay(nodeStore, n1, n2);

	try {
	    jbTreeTool.moveRightLeafNode(n1, 10);
	    fail();
	} catch (JblinktreeException e) {
	    assertTrue(e.getMessage().contains("method is for leaf nodes"));
	}
	EasyMock.verify(nodeStore, n1, n2);
    }

    @Test
    public void test_moveRightLeafNode_noMove() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.expect(n1.getLink()).andReturn(32);
	EasyMock.expect(n1.getMaxKey()).andReturn(18).times(2);
	EasyMock.expect(n1.isEmpty()).andReturn(false);
	EasyMock.replay(nodeStore, n1, n2);
	Node<Integer, Integer> ret = jbTreeTool.moveRightLeafNode(n1, 10);

	assertEquals(n1, ret);
	EasyMock.verify(nodeStore, n1, n2);
    }

    @Test
    public void test_moveRightLeafNode_linkIsNull() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.expect(n1.getLink()).andReturn(null).times(2);
	EasyMock.expect(n1.isEmpty()).andReturn(true);
	EasyMock.replay(nodeStore, n1, n2);
	Node<Integer, Integer> ret = jbTreeTool.moveRightLeafNode(n1, 10);

	assertEquals(n1, ret);
	EasyMock.verify(nodeStore, n1, n2);
    }

    @Test
    public void test_moveRightLeafNode_move() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.expect(n1.getLink()).andReturn(32);
	EasyMock.expect(n1.getMaxKey()).andReturn(5).times(2);
	EasyMock.expect(n1.isEmpty()).andReturn(false);

	EasyMock.expect(n1.getLink()).andReturn(32);
	EasyMock.expect(nodeStore.getAndLock(32)).andReturn((Node) n2);
	EasyMock.expect(n1.getId()).andReturn(5);

	EasyMock.expect(n2.getLink()).andReturn(45);
	EasyMock.expect(n2.isEmpty()).andReturn(false);
	EasyMock.expect(n2.getMaxKey()).andReturn(15).times(2);
	nodeStore.unlockNode(5);

	EasyMock.replay(nodeStore, n1, n2);
	Node<Integer, Integer> ret = jbTreeTool.moveRightLeafNode(n1, 10);

	assertEquals(n2, ret);
	EasyMock.verify(nodeStore, n1, n2);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void setUp() throws Exception {
	super.setUp();
	nodeStore = EasyMock.createMock(NodeStore.class);
	jbTreeTool = new JbTreeToolImpl<Integer, Integer>(nodeStore,
		new TypeDescriptorInteger());
	n1 = EasyMock.createMock(NodeImpl.class);
	n2 = EasyMock.createMock(NodeImpl.class);
    }

    @Override
    protected void tearDown() throws Exception {
	n1 = null;
	n2 = null;
	jbTreeTool = null;
	nodeStore = null;
	super.tearDown();
    }

}
