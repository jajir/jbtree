package com.coroptis.jblinktree.integration;

import static org.junit.Assert.*;
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
import com.coroptis.jblinktree.type.Types;
import com.google.common.io.Files;

/**
 * Verify basic operations with file storage.
 *
 * @author jajir
 *
 */
public class TreeFileStorageTest {

    private File tempDirectory;

    private final static int NUMBER_OF_CYCLES = 10;

    private TreeMap<Integer, String> tree;

    @Test
    public void test_insert_few_moves() throws Exception {
        insertMoves(0);
        verifyMoves(0);
        printAll(tree);
    }

    @Test
    public void test_containsKey() throws Exception {
        insertMoves(0);
        verifyMoves(0);
        assertTrue(tree.containsKey(Integer.valueOf(7)));
        assertFalse(tree.containsKey(Integer.valueOf(732)));
        assertFalse(tree.containsKey(Integer.valueOf(-272)));
    }

    @Test
    public void test_close_and_reopen_tree() throws Exception {
        insertMoves(0);
        tree.close();
        tree = makeTree();
        verifyMoves(0);
    }

    @Test
    public void test_close_and_reopen_and_insert_again() throws Exception {
        insertMoves(0);
        tree.close();
        tree = makeTree();
        verifyMoves(0);
        insertMoves(10);
        printAll(tree);
        verifyMoves(0);
        verifyMoves(10);
    }

    private TreeMap<Integer, String> makeTree() {
        return TreeBuilder.builder().setKeyType(Types.integer())
                .setValueType(Types.string(14)).setL(2)
                .setNodeStoreInFileBuilder(
                        TreeBuilder.getNodeStoreInFileBuilder()
                                .setFileName(tempDirectory.getAbsolutePath())
                                .setNoOfCachedNodes(1))
                .build();
    }

    private void printAll(TreeMap<Integer, String> tree) {
        tree.visit(new JbDataVisitor<Integer, String>() {

            @Override
            public boolean visited(final Integer key, final String value) {
                System.out.println("<" + key + ", " + value + ">");
                return true;
            }
        });
    }

    private void insertMoves(final int start) {
        for (int i = start; i < NUMBER_OF_CYCLES + start; i++) {
            tree.put(i, "Old monkey-" + i);
        }

    }

    private void verifyMoves(final int start) {
        for (int i = start; i < NUMBER_OF_CYCLES + start; i++) {
            String val = tree.get(i);
            assertEquals("Old monkey-" + i, val);
        }
    }

    @Before
    public void setUp() {
        tempDirectory = Files.createTempDir();
        tree = makeTree();
    }

    @After
    public void tearDown() {
        tempDirectory.delete();
        tempDirectory = null;
        tree.close();
        tree = null;
    }
}
