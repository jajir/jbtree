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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.JbNodeService;
import com.coroptis.jblinktree.JbNodeServiceImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

/**
 * Test verify that correct data are returned. It use real object.
 * 
 * @author jajir
 *
 */
public class JbNodeService2Test {

    private TypeDescriptorInteger tdi;

    private JbNodeDef<Integer, Integer> nodeDef;

    /**
     * key in node are: 2,4,6,8,10,12
     * 
     * @throws Exception
     */
    private Node<Integer, Integer> node;

    private JbNodeService<Integer, Integer> nodeService;

    @Test
    public void test_getCorrespondingNodeId() throws Exception {
        assertEquals(Integer.valueOf(2),
                nodeService.getCorrespondingNodeId(node, 0));
        assertEquals(Integer.valueOf(2),
                nodeService.getCorrespondingNodeId(node, 1));
        assertEquals(Integer.valueOf(2),
                nodeService.getCorrespondingNodeId(node, 2));
        assertEquals(Integer.valueOf(4),
                nodeService.getCorrespondingNodeId(node, 3));
        assertEquals(Integer.valueOf(4),
                nodeService.getCorrespondingNodeId(node, 4));
        assertEquals(Integer.valueOf(6),
                nodeService.getCorrespondingNodeId(node, 5));
        assertEquals(Integer.valueOf(6),
                nodeService.getCorrespondingNodeId(node, 6));
        assertEquals(Integer.valueOf(8),
                nodeService.getCorrespondingNodeId(node, 7));
        assertEquals(Integer.valueOf(8),
                nodeService.getCorrespondingNodeId(node, 8));
        assertEquals(Integer.valueOf(10),
                nodeService.getCorrespondingNodeId(node, 9));
        assertEquals(Integer.valueOf(10),
                nodeService.getCorrespondingNodeId(node, 10));
        assertEquals(Integer.valueOf(12),
                nodeService.getCorrespondingNodeId(node, 11));
        assertEquals(Integer.valueOf(12),
                nodeService.getCorrespondingNodeId(node, 12));
        assertEquals(Integer.valueOf(98),
                nodeService.getCorrespondingNodeId(node, 13));
    }

    @Before
    public void setup() throws Exception {
        tdi = new TypeDescriptorInteger();
        nodeDef = new JbNodeDefImpl<Integer, Integer>(9, tdi, tdi, tdi);
        nodeService = new JbNodeServiceImpl<Integer, Integer>();
        node = new NodeImpl<Integer, Integer>(6, false, nodeDef);
        node.insertAtPosition(2, 2, 0);
        node.insertAtPosition(4, 4, 1);
        node.insertAtPosition(6, 6, 2);
        node.insertAtPosition(8, 8, 3);
        node.insertAtPosition(10, 10, 4);
        node.insertAtPosition(12, 12, 5);
        node.setLink(98);
    }

    @After
    public void tearDown() throws Exception {
        tdi = null;
        nodeDef = null;
        node = null;
        nodeDef = null;
    }

}
