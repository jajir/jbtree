package com.coroptis.jblinktree.junit;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.util.JbStack;

/**
 * Junit test form {@link JbTreeImpl}.
 *
 * @author jajir
 *
 */
public class JbTreeTest extends AbstractMockingTest {

    private JbTreeImpl<Integer, Integer> jbTree;

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

    @Test
    public void test_insert_new_key() throws Exception {
        record_find_node();
        expect(nodeService.getValueByKey(n2, 99)).andReturn(null);
        expect(treeHelper.insertToLeafNode(eq(n2), eq(99), eq(-99),
                (JbStack) anyObject())).andReturn(null);

        replay();
        Integer ret = jbTree.insert(99, -99);
        verify();
        assertNull(ret);
    }

    @Test
    public void test_insert_alreadyExisting_key() throws Exception {
        record_find_node();
        expect(nodeService.getValueByKey(n2, 99)).andReturn(-88);
        jbTreeService.storeValueIntoNode(n2, 99, -99);

        replay();
        Integer ret = jbTree.insert(99, -99);
        verify();
        assertEquals(Integer.valueOf(-88), ret);
    }

    private void record_find_node() {
        EasyMock.expect(treeData.getRootNodeId()).andReturn(64);
        EasyMock.expect(
                treeTool.findLeafNodeId(eq(99), (JbStack) anyObject(), eq(64)))
                .andReturn(11);
        expect(nodeStore.getAndLock(11)).andReturn((Node) n1);
        expect(treeTraversingService.moveRightLeafNode(n1, 99))
                .andReturn((Node) n2);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        jbTree = new JbTreeImpl<Integer, Integer>(nodeStore, treeTool,
                treeHelper, treeData, treeTraversingService, jbTreeService,
                nodeService);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        jbTree = null;
        super.tearDown();
    }

}
