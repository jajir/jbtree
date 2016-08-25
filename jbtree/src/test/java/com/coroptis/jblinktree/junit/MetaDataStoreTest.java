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
import static org.junit.Assert.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.store.MetaDataStore;
import com.coroptis.jblinktree.store.MetaDataStoreImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.TypeDescriptorString;
import com.google.common.io.Files;

public class MetaDataStoreTest {

    private MetaDataStore metaDataStore;

    private File tempDirectory;

    private File metaFile;

    private JbTreeData<Integer, String> treeData;

    @Test
    public void test_init_and_close() throws Exception {
        assertTrue(true);
        metaDataStore.close();
    }

    @Test
    public void test_reopen() throws Exception {
        metaDataStore.close();

        metaDataStore = new MetaDataStoreImpl<Integer, String>(metaFile,
                treeData);
        assertEquals(Integer.valueOf(8765), treeData.getRootNodeId());
        assertEquals(Integer.valueOf(31), treeData.getMaxNodeId());
        metaDataStore.close();
    }

    @Test(expected = JblinktreeException.class)
    public void test_invalid_header() throws Exception {
        metaDataStore.close();

        RandomAccessFile raf = new RandomAccessFile(metaFile, "rw");
        raf.seek(0);
        raf.write("Invalid header  ".getBytes());
        raf.close();

        metaDataStore = new MetaDataStoreImpl<Integer, String>(metaFile,
                treeData);
    }

    @Before
    public void setup() {
        tempDirectory = Files.createTempDir();
        metaFile = new File(
                tempDirectory.getAbsolutePath() + File.separator + "meta.vfs");
        TypeDescriptor<Integer> tdKey = new TypeDescriptorInteger();
        TypeDescriptor<String> tdValue = new TypeDescriptorString(9,
                Charset.forName("ISO-8859-1"));
        TypeDescriptor<Integer> tdLink = new TypeDescriptorInteger();
        treeData = new JbTreeDataImpl<Integer, String>(0, 2, tdKey, tdValue,
                tdLink);
        treeData.setRootNodeId(8765);
        treeData.setMaxNodeId(31);
        metaDataStore = new MetaDataStoreImpl<Integer, String>(metaFile,
                treeData);
    }

    @After
    public void tearDown() {
        tempDirectory = null;
        metaFile = null;
        treeData = null;
        metaDataStore = null;
    }
}
