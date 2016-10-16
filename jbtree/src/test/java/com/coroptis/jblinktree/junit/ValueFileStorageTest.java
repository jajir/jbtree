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
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.Charset;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeShort;
import com.coroptis.jblinktree.store.ValueFileStorage;
import com.coroptis.jblinktree.store.ValueFileStorageImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.TypeDescriptorString;
import com.coroptis.jblinktree.type.Wrapper;
import com.google.common.io.Files;

/**
 * Simple tests for {@link ValueFileStorage}.
 *
 * @author jajir
 *
 */
public class ValueFileStorageTest extends AbstractMockingTest {

    private ValueFileStorage<Integer, String> valueStorage;

    private File tempDirectory;

    private TypeDescriptor<String> tds;

    private TypeDescriptorInteger tdi;

    private JbNodeDef<Integer, String> nodeDef;

    private Node<Integer, String> node;

    @Test
    public void test_read_and_write() throws Exception {
        valueStorage.storeValues(node);

        Node<Integer, String> n = new NodeShort<Integer, String>(6, true,
                nodeDef);

        n.insertAtPosition(Wrapper.make(3, tdi), "", 0);
        n.insertAtPosition(Wrapper.make(4, tdi), "", 1);
        valueStorage.loadValues(n);
        assertEquals("Ahoj", n.getValue(0));
        assertEquals("Lidi", n.getValue(1));
    }

    @Test
    public void test_write() throws Exception {
        EasyMock.expect(n4.isLeafNode()).andReturn(true);
        EasyMock.expect(n4.getId()).andReturn(0);
        EasyMock.expect(n4.getKeyCount()).andReturn(1);
        EasyMock.expect(n4.getValue(0)).andReturn("Ahoj lidi");
        EasyMock.expect(n4.getNodeDef()).andReturn(nodeDef);
        EasyMock.replay(mocks);

        valueStorage.storeValues(n4);

        EasyMock.verify(mocks);
    }

    @Test()
    public void test_store_only_leaf_nodes() throws Exception {
        EasyMock.expect(n4.isLeafNode()).andReturn(false);
        EasyMock.replay(mocks);
        try {
            valueStorage.storeValues(n4);
            fail();
        } catch (JblinktreeException e) {
            EasyMock.verify(mocks);
        }
    }

    @Test()
    public void test_read_invalid_nodeId() throws Exception {
        EasyMock.expect(n4.isLeafNode()).andReturn(true);
        EasyMock.expect(n4.getId()).andReturn(10);
        EasyMock.expect(n4.getNodeDef()).andReturn(nodeDef);
        EasyMock.replay(mocks);
        try {
            valueStorage.loadValues(n4);
            fail();
        } catch (JblinktreeException e) {
            EasyMock.verify(mocks);
        }
    }

    @Before
    public void setup() throws Exception {
        super.setUp();
        tempDirectory = Files.createTempDir();
        tds = new TypeDescriptorString(20, Charset.forName("ISO-8859-1"));
        tdi = new TypeDescriptorInteger();
        nodeDef = new JbNodeDefImpl<Integer, String>(3, tdi, tds, tdi);
        valueStorage = new ValueFileStorageImpl<Integer, String>(new File(
                tempDirectory.getAbsolutePath() + File.separator + "value.str"),
                tds, 3);

        node = new NodeShort<Integer, String>(6, true, nodeDef);
        node.insertAtPosition(Wrapper.make(3, tdi), "Ahoj", 0);
        node.insertAtPosition(Wrapper.make(4, tdi), "Lidi", 1);
        node.setLink(98);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        tempDirectory = null;
        valueStorage.close();
        valueStorage = null;
        tds = null;
        tdi = null;
        nodeDef = null;
        node = null;
    }
}
