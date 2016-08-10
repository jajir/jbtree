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

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeService;
import com.coroptis.jblinktree.JbTreeServiceImpl;
import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.Node;

/**
 * Tests for {@link JbTreeTool}
 *
 * @author jajir
 *
 */
public class JbTreeServiceTest extends AbstractMockingTest {

    private JbTreeService<Integer, Integer> tested;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void prepareMocksUpToIf() {
        EasyMock.expect(nodeStore.getAndLock(34)).andReturn((Node) n2);
        EasyMock.expect(treeTraversingService.moveRightNonLeafNode(n2, 10))
                .andReturn(n3);
        EasyMock.expect(n1.getId()).andReturn(3);
        EasyMock.expect(n1.getMaxKey()).andReturn(39);
    }

    @Test
    public void test_loadParentNode() throws Exception {
        prepareMocksUpToIf();
        EasyMock.expect(n3.updateKeyForValue(3, 39)).andReturn(true);
        nodeStore.writeNode(n3);
        EasyMock.replay(mocks);
        Node<Integer, Integer> ret = tested.loadParentNode(n1, 10, 34);

        assertEquals(n3, ret);
        EasyMock.verify(mocks);
    }

    @Test
    public void test_loadParentNode_noUpdate() throws Exception {
        prepareMocksUpToIf();
        EasyMock.expect(n3.updateKeyForValue(3, 39)).andReturn(false);
        EasyMock.replay(mocks);
        Node<Integer, Integer> ret = tested.loadParentNode(n1, 10, 34);

        assertEquals(n3, ret);
        EasyMock.verify(mocks);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        tested = new JbTreeServiceImpl<Integer, Integer>(nodeStore,
                treeTraversingService);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        tested = null;
        super.tearDown();
    }

}
