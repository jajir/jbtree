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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeDataImpl;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.coroptis.jblinktree.NodeBuilderImpl;
import com.coroptis.jblinktree.NodeImpl;
import com.coroptis.jblinktree.store.FileStorage;
import com.coroptis.jblinktree.store.FileStorageImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.google.common.io.Files;

public class FileStorageTest {

    private final Logger logger = LoggerFactory.getLogger(FileStorageTest.class);

    private final static String FILE_NAME = "test.bin";

    private File tempDirectory;

    private JbTreeData<Integer, Integer> treeData;

    private NodeBuilder<Integer, Integer> nodeBuilder;

    private FileStorage<Integer, Integer> fileStorage;

    private TypeDescriptor<Integer> intDescriptor;

    private Node<Integer, Integer> node;

    @Test
    public void test_store_verify_file() throws Exception {
	fileStorage.store(node);
	fileStorage.close();

	File f = new File(tempDirectory.getAbsolutePath() + File.separator + FILE_NAME);
	assertTrue(f.exists());
	assertTrue(f.isFile());
    }

    @Test
    public void test_store_verify_node() throws Exception {
	fileStorage.store(node);
	
	Node<Integer,Integer> node2 = fileStorage.load(14);
	
	assertEquals(node, node2);
	
	fileStorage.close();
    }

    @Before
    public void setup() {
	tempDirectory = Files.createTempDir();
	logger.debug("templ file: " + tempDirectory.getAbsolutePath());
	intDescriptor = new TypeDescriptorInteger();
	treeData = new JbTreeDataImpl<Integer, Integer>(0, 5, intDescriptor, intDescriptor,
		intDescriptor);
	nodeBuilder = new NodeBuilderImpl<Integer, Integer>(treeData);
	fileStorage = new FileStorageImpl<Integer, Integer>(treeData, nodeBuilder,
		tempDirectory.getAbsolutePath() + File.separator + FILE_NAME);
	node = new NodeImpl<Integer, Integer>(5, 14, false, intDescriptor, intDescriptor,
		intDescriptor);
	node.insert(3, 23);
    }

    @After
    public void tearDown() {
	node = null;
	intDescriptor = null;
	fileStorage = null;
	nodeBuilder = null;
	treeData = null;
	tempDirectory.delete();
	tempDirectory = null;
    }
}
