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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.JbTreeToolImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayDeque;

/**
 * Tests for {@link JbTreeTool}
 * 
 * @author jajir
 * 
 */
public class JbTreeToolTest extends AbstractMockingTest {

    private JbTreeTool<Integer, Integer> tested;

    @Test
    public void test_canMoveToNextNode_pass() throws Exception {
	EasyMock.expect(n1.getLink()).andReturn(4);
	EasyMock.expect(n1.isEmpty()).andReturn(false);
	EasyMock.expect(n1.getMaxKey()).andReturn(7).times(2);
	EasyMock.replay(mocks);

	boolean ret = tested.canMoveToNextNode(n1, 12);
	EasyMock.verify(mocks);
	assertTrue(ret);
    }

    @Test
    public void test_canMoveToNextNode_empty_link() throws Exception {
	EasyMock.expect(n1.getLink()).andReturn(NodeImpl.EMPTY_INT);
	EasyMock.replay(mocks);

	boolean ret = tested.canMoveToNextNode(n1, 12);
	EasyMock.verify(mocks);
	assertFalse(ret);
    }

    @Test
    public void test_canMoveToNextNodenodeIsEmpty() throws Exception {
	EasyMock.expect(n1.getLink()).andReturn(4);
	EasyMock.expect(n1.isEmpty()).andReturn(true);
	EasyMock.replay(mocks);

	boolean ret = tested.canMoveToNextNode(n1, 12);
	EasyMock.verify(mocks);
	assertTrue(ret);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_findLeafNodeId() throws Exception {
	final JbStack stack = new JbStackArrayDeque();
	EasyMock.expect(nodeStore.get(2)).andReturn((Node) n1);
	EasyMock.expect(n1.getId()).andReturn(2);
	EasyMock.expect(n1.isLeafNode()).andReturn(false);

	EasyMock.expect(n1.getCorrespondingNodeId(12)).andReturn(60);
	EasyMock.expect(n1.getLink()).andReturn(98);

	EasyMock.expect(nodeStore.get(60)).andReturn((Node) n2);
	EasyMock.expect(n2.isLeafNode()).andReturn(true);
	EasyMock.expect(n2.getId()).andReturn(62);

	EasyMock.replay(nodeStore, treeTool, n1, n2);
	Integer ret = tested.findLeafNodeId(12, stack, 2);

	assertEquals(Integer.valueOf(62), ret);
	EasyMock.verify(nodeStore, treeTool, n1, n2);
    }

    @Before
    public void setUp() throws Exception {
	super.setUp();
	tested = new JbTreeToolImpl<Integer, Integer>(nodeStore, new TypeDescriptorInteger(),
		builder);
    }

    @After
    public void tearDown() throws Exception {
	tested = null;
	super.tearDown();
    }

}
