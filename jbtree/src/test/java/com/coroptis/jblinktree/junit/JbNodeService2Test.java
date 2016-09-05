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
import org.junit.Rule;
import org.junit.Test;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.JbNodeService;
import com.coroptis.jblinktree.JbNodeServiceImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.NodeUtilRule;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.Wrapper;

/**
 * Test verify that correct data are returned. It use real object.
 * 
 * @author jajir
 *
 */
public class JbNodeService2Test {

    @Rule
    public NodeUtilRule nodeUtil = new NodeUtilRule();

    private TypeDescriptorInteger tdi;

    private JbNodeDef<Integer, Integer> nodeDef;

    /**
     * key in node are: 2,4,6,8,10,12
     * 
     * @throws Exception
     */
    private Node<Integer, Integer> node;

    private JbNodeService<Integer, Integer> nodeService;

    private final static int LINK = 43;

    private final static int ID = 9;

    @Test
    public void test_getCorrespondingNodeId() throws Exception {
        assertEquals(Integer.valueOf(2),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(0, tdi)));
        assertEquals(Integer.valueOf(2),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(1, tdi)));
        assertEquals(Integer.valueOf(2),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(2, tdi)));
        assertEquals(Integer.valueOf(4),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(3, tdi)));
        assertEquals(Integer.valueOf(4),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(4, tdi)));
        assertEquals(Integer.valueOf(6),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(5, tdi)));
        assertEquals(Integer.valueOf(6),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(6, tdi)));
        assertEquals(Integer.valueOf(8),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(7, tdi)));
        assertEquals(Integer.valueOf(8),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(8, tdi)));
        assertEquals(Integer.valueOf(10),
                nodeService.getCorrespondingNodeId(node, Wrapper.make(9, tdi)));
        assertEquals(Integer.valueOf(10), nodeService
                .getCorrespondingNodeId(node, Wrapper.make(10, tdi)));
        assertEquals(Integer.valueOf(12), nodeService
                .getCorrespondingNodeId(node, Wrapper.make(11, tdi)));
        assertEquals(Integer.valueOf(12), nodeService
                .getCorrespondingNodeId(node, Wrapper.make(12, tdi)));
        assertEquals(Integer.valueOf(LINK), nodeService
                .getCorrespondingNodeId(node, Wrapper.make(13, tdi)));
    }

    @Test
    public void test_insert_0() throws Exception {
        assertEquals(null, nodeService.insert(node, Wrapper.make(0, tdi), 0));
        nodeUtil.verifyNode(node, new Integer[][] { { 0, 0 }, { 2, 2 },
                { 4, 4 }, { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false,
                LINK, ID);
    }

    @Test
    public void test_insert_1() throws Exception {
        assertEquals(null, nodeService.insert(node, Wrapper.make(1, tdi), -1));
        nodeUtil.verifyNode(node, new Integer[][] { { 1, -1 }, { 2, 2 },
                { 4, 4 }, { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false,
                LINK, ID);
    }

    @Test
    public void test_insert_2() throws Exception {
        assertEquals(Integer.valueOf(2),
                nodeService.insert(node, Wrapper.make(2, tdi), -2));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, -2 }, { 4, 4 },
                { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false, LINK, ID);
    }

    @Test
    public void test_insert_3() throws Exception {
        assertEquals(null, nodeService.insert(node, Wrapper.make(3, tdi), -3));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 3, -3 },
                { 4, 4 }, { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false,
                LINK, ID);
    }

    @Test
    public void test_insert_4() throws Exception {
        assertEquals(Integer.valueOf(4),
                nodeService.insert(node, Wrapper.make(4, tdi), -4));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, -4 },
                { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false, LINK, ID);
    }

    @Test
    public void test_insert_5() throws Exception {
        assertEquals(null, nodeService.insert(node, Wrapper.make(5, tdi), -5));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 5, -5 }, { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false,
                LINK, ID);
    }

    @Test
    public void test_insert_6() throws Exception {
        assertEquals(Integer.valueOf(6),
                nodeService.insert(node, Wrapper.make(6, tdi), -6));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, -6 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false, LINK, ID);
    }

    @Test
    public void test_insert_7() throws Exception {
        assertEquals(null, nodeService.insert(node, Wrapper.make(7, tdi), -7));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, 6 }, { 7, -7 }, { 8, 8 }, { 10, 10 }, { 12, 12 } }, false,
                LINK, ID);
    }

    @Test
    public void test_insert_8() throws Exception {
        assertEquals(Integer.valueOf(8),
                nodeService.insert(node, Wrapper.make(8, tdi), -8));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, 6 }, { 8, -8 }, { 10, 10 }, { 12, 12 } }, false, LINK, ID);
    }

    @Test
    public void test_insert_9() throws Exception {
        assertEquals(null, nodeService.insert(node, Wrapper.make(9, tdi), -9));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, 6 }, { 8, 8 }, { 9, -9 }, { 10, 10 }, { 12, 12 } }, false,
                LINK, ID);
    }

    @Test
    public void test_insert_10() throws Exception {
        assertEquals(Integer.valueOf(10),
                nodeService.insert(node, Wrapper.make(10, tdi), -10));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, 6 }, { 8, 8 }, { 10, -10 }, { 12, 12 } }, false, LINK, ID);
    }

    @Test
    public void test_insert_11() throws Exception {
        assertEquals(null,
                nodeService.insert(node, Wrapper.make(11, tdi), -11));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, 6 }, { 8, 8 }, { 10, 10 }, { 11, -11 }, { 12, 12 } },
                false, LINK, ID);
    }

    @Test
    public void test_insert_12() throws Exception {
        assertEquals(Integer.valueOf(12),
                nodeService.insert(node, Wrapper.make(12, tdi), -12));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, -12 } }, false, LINK, ID);
    }

    @Test
    public void test_insert_13() throws Exception {
        assertEquals(null,
                nodeService.insert(node, Wrapper.make(13, tdi), -13));
        nodeUtil.verifyNode(node, new Integer[][] { { 2, 2 }, { 4, 4 },
                { 6, 6 }, { 8, 8 }, { 10, 10 }, { 12, 12 }, { 13, -13 } },
                false, LINK, ID);
    }

    @Test
    public void test_insert_to_empty_node() throws Exception {
        node = new NodeImpl<Integer, Integer>(ID, false, nodeDef);
        node.setLink(LINK);
        assertEquals(null,
                nodeService.insert(node, Wrapper.make(13, tdi), -13));
        nodeUtil.verifyNode(node, new Integer[][] { { 13, -13 } }, false, LINK,
                ID);
    }

    @Before
    public void setup() throws Exception {
        tdi = new TypeDescriptorInteger();
        nodeDef = new JbNodeDefImpl<Integer, Integer>(9, tdi, tdi, tdi);
        nodeService = new JbNodeServiceImpl<Integer, Integer>();
        node = new NodeImpl<Integer, Integer>(ID, false, nodeDef);
        node.insertAtPosition(2, 2, 0);
        node.insertAtPosition(4, 4, 1);
        node.insertAtPosition(6, 6, 2);
        node.insertAtPosition(8, 8, 3);
        node.insertAtPosition(10, 10, 4);
        node.insertAtPosition(12, 12, 5);
        node.setLink(LINK);
    }

    @After
    public void tearDown() throws Exception {
        tdi = null;
        nodeDef = null;
        node = null;
        nodeDef = null;
    }

}
