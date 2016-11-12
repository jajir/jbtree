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
import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.JbTreeToolImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeShort;
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
        expect(n1.getLink()).andReturn(4);
        expect(n1.isEmpty()).andReturn(false);
        expect(n1.getMaxKey()).andReturn(w1);
        expect(n1.getMaxKeyIndex()).andReturn(2);
        expect(n1.compareKey(2, w2)).andReturn(-1);
        replay();

        boolean ret = tested.canMoveToNextNode(n1, w2);
        verify();
        assertTrue(ret);
    }

    @Test
    public void test_canMoveToNextNode_empty_link() throws Exception {
        expect(n1.getLink()).andReturn(NodeShort.EMPTY_INT);
        replay();

        boolean ret = tested.canMoveToNextNode(n1, w2);
        verify();
        assertFalse(ret);
    }

    @Test
    public void test_canMoveToNextNodenodeIsEmpty() throws Exception {
        expect(n1.getLink()).andReturn(4);
        expect(n1.isEmpty()).andReturn(true);
        replay();

        boolean ret = tested.canMoveToNextNode(n1, w1);
        verify();
        assertTrue(ret);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void test_findLeafNodeId() throws Exception {
        final JbStack stack = new JbStackArrayList();
        expect(nodeStore.get(2)).andReturn((Node) n1);
        expect(n1.getId()).andReturn(2);
        expect(n1.isLeafNode()).andReturn(false);

        expect(nodeService.getCorrespondingNodeId(n1, w1)).andReturn(60);
        expect(n1.getLink()).andReturn(98);

        expect(nodeStore.get(60)).andReturn((Node) n2);
        expect(n2.isLeafNode()).andReturn(true);
        expect(n2.getId()).andReturn(62);

        replay();
        Integer ret = tested.findLeafNodeId(w1, stack, 2);

        assertEquals(Integer.valueOf(62), ret);
        verify();
    }

    @Test(expected = JblinktreeException.class)
    public void test_moveRightLeafNodeWithoutLocking_nonLeafNode()
            throws Exception {
        expect(n1.isLeafNode()).andReturn(false);

        replay();
        tested.moveRightLeafNodeWithoutLocking(n1, w2);

        verify();
    }

    @Test
    public void test_moveRightLeafNodeWithoutLocking() throws Exception {
        expect(n1.isLeafNode()).andReturn(true);
        expect(n1.getLink()).andReturn(Node.EMPTY_INT);

        replay();
        Node<Integer, Integer> ret = tested.moveRightLeafNodeWithoutLocking(n1,
                w2);

        assertSame(ret, n1);
        verify();
    }

    @Test
    public void test_splitNonLeafNode_insertToHigherNode() throws Exception {
        expect(nodeBuilder.makeEmptyNonLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        expect(n1.getMaxKeyIndex()).andReturn(3);
        expect(n1.compareKey(3, w3)).andReturn(-1);
        expect(nodeService.insert(n2, w3, -100)).andReturn(null);
        replay();
        Node<Integer, Integer> ret = tested.splitNonLeafNode(n1, w3, -100);

        assertSame(ret, n2);
        verify();
    }

    @Test
    public void test_splitNonLeafNode_insertToLowerNode() throws Exception {
        expect(nodeBuilder.makeEmptyNonLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        expect(n1.getMaxKeyIndex()).andReturn(3);
        expect(n1.compareKey(3, w3)).andReturn(1);
        expect(nodeService.insert(n1, w3, -100)).andReturn(null);
        replay();
        Node<Integer, Integer> ret = tested.splitNonLeafNode(n1, w3, -100);

        assertSame(ret, n2);
        verify();
    }

    @Test
    public void test_splitLeafNode_insertToHigherNode() throws Exception {
        expect(nodeBuilder.makeEmptyLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        expect(n1.getMaxKeyIndex()).andReturn(3);
        expect(n1.compareKey(3, w3)).andReturn(-1);
        expect(nodeService.insert(n2, w3, -100)).andReturn(null);
        replay();
        Node<Integer, Integer> ret = tested.splitLeafNode(n1, w3, -100);

        assertSame(ret, n2);
        verify();
    }

    @Test
    public void test_splitLeafNode_insertToLowerNode() throws Exception {
        expect(nodeBuilder.makeEmptyLeafNode(0)).andReturn(n2);
        n1.moveTopHalfOfDataTo(n2);
        expect(n1.getMaxKeyIndex()).andReturn(3);
        expect(n1.compareKey(3, w3)).andReturn(1);
        expect(nodeService.insert(n1, w3, -100)).andReturn(null);
        replay();
        Node<Integer, Integer> ret = tested.splitLeafNode(n1, w3, -100);

        assertSame(ret, n2);
        verify();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        TypeDescriptor<Integer> tdInt = new TypeDescriptorInteger();

        final JbNodeDefImpl.Initializator init = new JbNodeDefImpl.InitializatorShort();
        final JbNodeDef<Integer, Integer> leafNodeDescriptor = new JbNodeDefImpl<Integer, Integer>(
                5, tdInt, tdInt, tdInt, init);
        final JbNodeDef<Integer, Integer> nonLeafNodeDescriptor = new JbNodeDefImpl<Integer, Integer>(
                5, tdInt, tdInt, tdInt, init);

        JbTreeData<Integer, Integer> td = new JbTreeDataImpl<Integer, Integer>(
                0, 3, leafNodeDescriptor, nonLeafNodeDescriptor);
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
