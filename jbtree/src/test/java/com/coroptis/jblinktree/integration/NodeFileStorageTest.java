package com.coroptis.jblinktree.integration;

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

import java.io.File;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbNodeBuilderImpl;
import com.coroptis.jblinktree.JbNodeService;
import com.coroptis.jblinktree.JbNodeServiceImpl;
import com.coroptis.jblinktree.store.NodeConverter;
import com.coroptis.jblinktree.store.NodeConverterImpl;
import com.coroptis.jblinktree.store.NodeFileStorageImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.TypeDescriptorString;
import com.google.common.io.Files;

public class NodeFileStorageTest {

    private NodeFileStorageImpl<String, String> storage;

    private JbNodeService<String, String> nodeService;

    private File tempDirectory;

    private JbNodeBuilder<String, String> nodeBuilder;

    @Test
    public void test_read_and_write() throws Exception {
        for (int i = 0; i < 10; i++) {
            storage.store(createNode(i));
        }

        for (int i = 0; i < 10; i++) {
            Node<String, String> n = storage.load(i);
            assertEquals("Ahoj lidi!" + i, n.getKey(0));
            assertEquals("Jde to!" + i, n.getValue(0));
            assertEquals(Integer.valueOf(-i * 100), n.getLink());
        }
    }

    Node<String, String> createNode(final Integer i) {
        Node<String, String> n = nodeBuilder.makeEmptyLeafNode(i);
        nodeService.insert(n, "Ahoj lidi!" + i, "Jde to!" + i);
        n.setLink(-i * 100);
        return n;
    }

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        TypeDescriptor<String> tdKey =
                new TypeDescriptorString(13, Charset.forName("ISO-8859-1"));
        TypeDescriptor<String> tdValue =
                new TypeDescriptorString(9, Charset.forName("ISO-8859-1"));
        TypeDescriptor<Integer> tdLink = new TypeDescriptorInteger();
        JbTreeData<String, String> treeData =
                new JbTreeDataImpl<String, String>(0, 2, tdKey, tdValue,
                        tdLink);
        nodeBuilder = new JbNodeBuilderImpl<String, String>(treeData);
        NodeConverter<String, String> initNodeConverter =
                new NodeConverterImpl<String, String>(treeData, nodeBuilder);
        storage = new NodeFileStorageImpl<String, String>(treeData, nodeBuilder,
                tempDirectory.getAbsolutePath(), initNodeConverter);
        nodeService = new JbNodeServiceImpl<String, String>();
    }

    @After
    public void tearDown() {
        tempDirectory = null;
        storage.close();
        storage = null;
        nodeService = null;
    }

}
