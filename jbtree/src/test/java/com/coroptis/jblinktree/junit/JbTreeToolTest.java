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
import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.JbTreeToolImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayList;

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
        replay();

        boolean ret = tested.canMoveToNextNode(n1, 12);
        verify();
        assertTrue(ret);
    }

    @Test
    public void test_canMoveToNextNode_empty_link() throws Exception {
        EasyMock.expect(n1.getLink()).andReturn(NodeImpl.EMPTY_INT);
        replay();

        boolean ret = tested.canMoveToNextNode(n1, 12);
        verify();
        assertFalse(ret);
    }

    @Test
    public void test_canMoveToNextNodenodeIsEmpty() throws Exception {
        EasyMock.expect(n1.getLink()).andReturn(4);
        EasyMock.expect(n1.isEmpty()).andReturn(true);
        replay();

        boolean ret = tested.canMoveToNextNode(n1, 12);
        verify();
        assertTrue(ret);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_findLeafNodeId() throws Exception {
        final JbStack stack = new JbStackArrayList();
        EasyMock.expect(nodeStore.get(2)).andReturn((Node) n1);
        EasyMock.expect(n1.getId()).andReturn(2);
        EasyMock.expect(n1.isLeafNode()).andReturn(false);

        EasyMock.expect(nodeService.getCorrespondingNodeId(n1, 12))
                .andReturn(60);
        EasyMock.expect(n1.getLink()).andReturn(98);

        EasyMock.expect(nodeStore.get(60)).andReturn((Node) n2);
        EasyMock.expect(n2.isLeafNode()).andReturn(true);
        EasyMock.expect(n2.getId()).andReturn(62);

        replay();
        Integer ret = tested.findLeafNodeId(12, stack, 2);

        assertEquals(Integer.valueOf(62), ret);
        verify();
    }

    @Test(expected = JblinktreeException.class)
    public void test_moveRightLeafNodeWithoutLocking_nonLeafNode()
            throws Exception {
        EasyMock.expect(n1.isLeafNode()).andReturn(false);

        replay();
        tested.moveRightLeafNodeWithoutLocking(n1, 13);

        verify();
    }

    @Test
    public void test_moveRightLeafNodeWithoutLocking() throws Exception {
        EasyMock.expect(n1.isLeafNode()).andReturn(true);
        EasyMock.expect(n1.getLink()).andReturn(Node.EMPTY_INT);

        replay();
        Node<Integer, Integer> ret =
                tested.moveRightLeafNodeWithoutLocking(n1, 13);

        assertSame(ret, n1);
        verify();
    }

    @Test
    public void test_splitNonLeafNode_insertToHigherNode() throws Exception {
        EasyMock.expect(nodeBuilder.makeEmptyNonLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        EasyMock.expect(n1.getMaxKey()).andReturn(51);
        nodeService.insert(n2, 55, -100);
        replay();
        Node<Integer, Integer> ret = tested.splitNonLeafNode(n1, 55, -100);

        assertSame(ret, n2);
        verify();
    }

    @Test
    public void test_splitNonLeafNode_insertToLowerNode() throws Exception {
        EasyMock.expect(nodeBuilder.makeEmptyNonLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        EasyMock.expect(n1.getMaxKey()).andReturn(59);
        nodeService.insert(n1, 55, -100);
        replay();
        Node<Integer, Integer> ret = tested.splitNonLeafNode(n1, 55, -100);

        assertSame(ret, n2);
        verify();
    }
    
    @Test
    public void test_splitLeafNode_insertToHigherNode() throws Exception {
        EasyMock.expect(nodeBuilder.makeEmptyLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        EasyMock.expect(n1.getMaxKey()).andReturn(51);
        nodeService.insert(n2, 55, -100);
        replay();
        Node<Integer, Integer> ret = tested.splitLeafNode(n1, 55, -100);

        assertSame(ret, n2);
        verify();
    }
    
    @Test
    public void test_splitLeafNode_insertToLowerNode() throws Exception {
        EasyMock.expect(nodeBuilder.makeEmptyLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        EasyMock.expect(n1.getMaxKey()).andReturn(59);
        nodeService.insert(n1, 55, -100);
        replay();
        Node<Integer, Integer> ret = tested.splitLeafNode(n1, 55, -100);

        assertSame(ret, n2);
        verify();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        TypeDescriptor<Integer> tdInt = new TypeDescriptorInteger();
        JbTreeData<Integer, Integer> td =
                new JbTreeDataImpl<Integer, Integer>(0, 3, tdInt, tdInt, tdInt);
        tested = new JbTreeToolImpl<Integer, Integer>(nodeStore, td,
                nodeBuilder, nodeService);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        tested = null;
        super.tearDown();
    }

}
