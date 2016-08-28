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
import static org.junit.Assert.assertNotNull;

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

        Integer nodeId = service.getCorrespondingNodeId(n,4);

        assertNotNull("node id can't be null", nodeId);
        assertEquals("node id should be different", Integer.valueOf(33),
                nodeId);
    }

    @Test
    public void test_getCorrespondingNodeId_simple() throws Exception {
        Node<Integer, Integer> n = nr.makeNodeFromIntegers(2,
                new Integer[] { 0, 2, 1, 3, 23 });

        logger.debug(n.toString());

        Integer nodeId = service.getCorrespondingNodeId(n,3);

        assertNotNull("node id can't be null", nodeId);
        assertEquals("node id should be different", Integer.valueOf(1), nodeId);
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
