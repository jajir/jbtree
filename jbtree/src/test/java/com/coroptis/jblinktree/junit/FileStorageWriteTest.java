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
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.coroptis.jblinktree.FileStorageRule;
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.JbNodeService;
import com.coroptis.jblinktree.JbNodeServiceImpl;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeShort;
import com.coroptis.jblinktree.type.Wrapper;

public class FileStorageWriteTest {

    @Rule
    public FileStorageRule fsRule = new FileStorageRule();

    private Node<Integer, Integer> node;

    private JbNodeService<Integer, Integer> nodeService;

    @Test
    public void test_store_verify_file() throws Exception {
        fsRule.getFileStorage().store(node);
        fsRule.getFileStorage().close();

        assertTrue(fsRule.getTempFile().exists());
        assertTrue(fsRule.getTempFile().isFile());
    }

    @Test
    public void test_store_verify_node() throws Exception {
        fsRule.getFileStorage().store(node);

        Node<Integer, Integer> node2 = fsRule.getFileStorage().load(14);

        assertEquals(node, node2);

        fsRule.getFileStorage().close();
    }

    @Before
    public void setup() {
        nodeService = new JbNodeServiceImpl<Integer, Integer>();

        final JbNodeDefImpl.Initializator init = new JbNodeDefImpl.InitializatorShort();
        final JbNodeDef<Integer, Integer> leafNodeDescriptor = new JbNodeDefImpl<Integer, Integer>(
                5, fsRule.getIntDescriptor(), fsRule.getIntDescriptor(),
                fsRule.getIntDescriptor(), init);
        final JbNodeDef<Integer, Integer> nonLeafNodeDescriptor = new JbNodeDefImpl<Integer, Integer>(
                5, fsRule.getIntDescriptor(), fsRule.getIntDescriptor(),
                fsRule.getIntDescriptor(), init);

        JbTreeData<Integer, Integer> treeData = new JbTreeDataImpl<Integer, Integer>(
                1, 5, leafNodeDescriptor, nonLeafNodeDescriptor);
        node = new NodeShort<Integer, Integer>(14, false,
                treeData.getLeafNodeDescriptor());
        nodeService.insert(node, Wrapper.make(3, fsRule.getIntDescriptor()),
                23);
    }

    @After
    public void tearDown() {
        node = null;
        nodeService = null;
    }
}
