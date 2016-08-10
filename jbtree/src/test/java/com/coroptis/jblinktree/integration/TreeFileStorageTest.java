package com.coroptis.jblinktree.integration;

import java.io.File;

import org.junit.After;
import org.junit.Before;

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

import org.junit.Test;

import com.coroptis.jblinktree.JbDataVisitor;
import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeMap;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.google.common.io.Files;

/**
 * Verify basic operations with file storage.
 *
 * @author jan
 *
 */
public class TreeFileStorageTest {

    private File tempDirectory;

    @Test
    public void test_insert_few_moves() throws Exception {
        TreeMap<Integer, Integer> tree = TreeBuilder.builder()
                .setKeyType(new TypeDescriptorInteger())
                .setValueType(new TypeDescriptorInteger()).setL(2)
                .setNodeStoreInFileBuilder(
                        TreeBuilder.getNodeStoreInFileBuilder()
                                .setFileName(tempDirectory + File.separator
                                        + "pok.bin")
                                .setNoOfCachedNodes(1))
                .build();

        for (int i = 0; i < 10; i++) {
            tree.put(i, i);
        }

        printAll(tree);
    }

    private void printAll(TreeMap<Integer, Integer> tree) {
        tree.visit(new JbDataVisitor<Integer, Integer>() {

            @Override
            public boolean visited(final Integer key, final Integer value) {
                System.out.println("<" + key + ", " + value + ">");
                return true;
            }
        });
    }

    @Before
    public void setUp() {
        tempDirectory = Files.createTempDir();
    }

    @After
    public void tearDown() {
        tempDirectory.delete();
        tempDirectory = null;
    }
}
