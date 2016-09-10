package com.coroptis.jblinktree.junit;

import static org.easymock.EasyMock.expect;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JbNodeService;
import com.coroptis.jblinktree.JbNodeServiceImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeRule;
import com.coroptis.jblinktree.type.Wrapper;

public class JbNodeServiceTest extends AbstractMockingTest {

    private Logger logger = LoggerFactory.getLogger(JbNodeServiceTest.class);

    @Rule
    public NodeRule nr = new NodeRule(2);

    private JbNodeService<Integer, Integer> service;

    @Test
    public void test_getCorrespondingNodeId_return_link() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 1, 2, 3, 33 });

        logger.debug(n.toString());

        Integer nodeId = service.getCorrespondingNodeId(n,
                Wrapper.make(4, nr.getTdi()));

        assertNotNull("node id can't be null", nodeId);
        assertEquals("node id should be different", Integer.valueOf(33),
                nodeId);
    }

    @Test
    public void test_getCorrespondingNodeId_simple() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        logger.debug(n.toString());

        Integer nodeId = service.getCorrespondingNodeId(n,
                Wrapper.make(3, nr.getTdi()));

        assertNotNull("node id can't be null", nodeId);
        assertEquals("node id should be different", Integer.valueOf(1), nodeId);
    }

    @Test
    public void test_getValueByKey() throws Exception {
        Wrapper<Integer> w = Wrapper.make(2, nr.getTdi());
        expect(n1.getKeyCount()).andReturn(9);
        expect(n1.compareKey(4, w)).andReturn(1);
        expect(n1.compareKey(1, w)).andReturn(-1);
        expect(n1.compareKey(2, w)).andReturn(0);
        expect(n1.getValue(2)).andReturn(2);

        replay();
        Integer ret = service.getValueByKey(n1, w);
        verify();
        assertEquals(Integer.valueOf(2), ret);
    }

    @Test
    public void test_getValueByKey_notFound() throws Exception {
        Wrapper<Integer> w = Wrapper.make(3, nr.getTdi());
        expect(n1.getKeyCount()).andReturn(9);
        expect(n1.compareKey(4, w)).andReturn(8);
        expect(n1.compareKey(1, w)).andReturn(2);
        expect(n1.compareKey(0, w)).andReturn(4);
        replay();
        Integer ret = service.getValueByKey(n1, w);
        verify();
        assertNull(ret);
    }

    @Test
    public void test_getValueByKey_notFound_emptyNode() throws Exception {
        expect(n1.getKeyCount()).andReturn(0);
        replay();
        Integer ret = service.getValueByKey(n1, Wrapper.make(1, nr.getTdi()));
        verify();
        assertNull(ret);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        service = new JbNodeServiceImpl<Integer, Integer>();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        service = null;
        super.tearDown();
    }
}
