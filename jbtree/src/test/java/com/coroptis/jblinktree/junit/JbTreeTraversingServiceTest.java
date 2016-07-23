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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeTraversingService;
import com.coroptis.jblinktree.JbTreeTraversingServiceImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;

/**
 * 
 * @author jajir
 * 
 */
public class JbTreeTraversingServiceTest extends AbstractMockingTest {

    private JbTreeTraversingService<Integer, Integer> tested;

    @Test
    public void test_moveRightNonLeafNode_isLeafNode() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.replay(mocks);

	try {
	    tested.moveRightNonLeafNode(n1, 10);
	    fail();
	} catch (JblinktreeException e) {
	    assertTrue(e.getMessage().contains("method is for non-leaf"));
	}
	EasyMock.verify(mocks);
    }

    @Test
    public void test_moveRightNonLeafNode_nextNodeId_isNotCurrentLink() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.expect(n1.getCorrespondingNodeId(10)).andReturn(4);
	EasyMock.expect(n1.getLink()).andReturn(12);
	EasyMock.replay(mocks);
	Node<Integer, Integer> ret = tested.moveRightNonLeafNode(n1, 10);

	assertEquals(n1, ret);
	EasyMock.verify(mocks);
    }

    @Test
    public void test_moveRightNonLeafNode_nextNodeId_isEmptyLink() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.expect(n1.getCorrespondingNodeId(10)).andReturn(NodeImpl.EMPTY_INT);
	EasyMock.replay(mocks);
	Node<Integer, Integer> ret = tested.moveRightNonLeafNode(n1, 10);

	assertEquals(n1, ret);
	EasyMock.verify(mocks);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_moveRightNonLeafNode_move() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.expect(n1.getCorrespondingNodeId(10)).andReturn(4);
	EasyMock.expect(n1.getLink()).andReturn(4);
	
	EasyMock.expect(treeTool.moveToNextNode(n1, 4)).andReturn((Node) n2);
	EasyMock.expect(n2.getCorrespondingNodeId(10)).andReturn(12);
	EasyMock.expect(n2.getLink()).andReturn(60);
	EasyMock.replay(mocks);
	Node<Integer, Integer> ret = tested.moveRightNonLeafNode(n1, 10);

	assertEquals(n2, ret);
	EasyMock.verify(mocks);
    }

    @Test
    public void test_moveRightLeafNode_isNonLeafNode() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(false);
	EasyMock.replay(mocks);

	try {
	    tested.moveRightLeafNode(n1, 10);
	    fail();
	} catch (JblinktreeException e) {
	    assertTrue(e.getMessage().contains("method is for leaf nodes"));
	}
	EasyMock.verify(mocks);
    }

    @Test
    public void test_moveRightLeafNode_noMove() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.expect(treeTool.canMoveToNextNode(n1, 10)).andReturn(false);
	EasyMock.replay(mocks);
	Node<Integer, Integer> ret = tested.moveRightLeafNode(n1, 10);

	assertEquals(n1, ret);
	EasyMock.verify(mocks);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_moveRightLeafNode_move() throws Exception {
	EasyMock.expect(n1.isLeafNode()).andReturn(true);
	EasyMock.expect(treeTool.canMoveToNextNode(n1, 10)).andReturn(true);
	EasyMock.expect(n1.getLink()).andReturn(32);

	EasyMock.expect(treeTool.moveToNextNode(n1, 32)).andReturn((Node) n2);
	EasyMock.expect(treeTool.canMoveToNextNode(n2, 10)).andReturn(false);

	EasyMock.replay(mocks);
	Node<Integer, Integer> ret = tested.moveRightLeafNode(n1, 10);

	assertEquals(n2.hashCode(), ret.hashCode());
	EasyMock.verify(mocks);
    }

    @Override
    @Before
    public void setUp() throws Exception {
	super.setUp();
	tested = new JbTreeTraversingServiceImpl<Integer, Integer>(treeTool);
    }

    @Override
    @After
    public void tearDown() throws Exception {
	super.tearDown();
	tested = null;
    }

}
