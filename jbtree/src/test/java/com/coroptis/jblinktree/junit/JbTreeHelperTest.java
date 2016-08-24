package com.coroptis.jblinktree.junit;

import org.easymock.EasyMock;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbDataVisitor;
import com.coroptis.jblinktree.JbTreeHelper;
import com.coroptis.jblinktree.JbTreeHelperImpl;

/**
 * Test for {@link JbTreeHelper}.
 * 
 * @author jajir
 *
 */
public class JbTreeHelperTest extends AbstractMockingTest {

    private JbTreeHelper<Integer, Integer> treeHelper;

    @SuppressWarnings("unchecked")
    @Test
    public void test_visit() throws Exception {
        JbDataVisitor<Integer, Integer> visitor =
                EasyMock.createMock(JbDataVisitor.class);
        EasyMock.expect(treeData.getRootNodeId()).andReturn(55);
        EasyMock.expect(jbTreeService.findSmallerNode(55)).andReturn(n1);
        EasyMock.expect(n1.getField()).andReturn(f1);
        EasyMock.expect(f1.getKeyCount()).andReturn(1);
        EasyMock.expect(f1.getKey(0)).andReturn(33);
        EasyMock.expect(f1.getValue(0)).andReturn(44);
        EasyMock.expect(visitor.visited(33, 44)).andReturn(false);

        EasyMock.replay(mocks);
        EasyMock.replay(visitor);

        treeHelper.visit(visitor);

        EasyMock.verify(mocks);
        EasyMock.verify(visitor);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        treeHelper = new JbTreeHelperImpl<Integer, Integer>(nodeStore, treeTool,
                jbTreeService, treeData);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        treeHelper = null;
        super.tearDown();
    }
}
