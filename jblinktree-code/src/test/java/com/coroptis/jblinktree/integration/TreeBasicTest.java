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

import java.io.File;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeMap;
import com.coroptis.jblinktree.TreeUtil;
import com.coroptis.jblinktree.type.Types;

public class TreeBasicTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(TreeBasicTest.class);

    private TreeMap<Integer, Integer> tree;

    private TreeUtil treeUtil;

    @Test
    public void testJustOneNode() throws Exception {

        tree.put(3, -30);
        tree.put(1, -10);

        logger.debug(tree.toString());
        assertEquals(2, tree.size());
        treeUtil.toDotFile(new File("pok.dot"));
    }

    @Test
    public void testSimpleSplitting() throws Exception {
        tree.put(3, -30);
        tree.put(1, -10);
        tree.put(5, -50);

        logger.debug(tree.toString());
        assertEquals(3, tree.size());
    }

    @Test
    public void test_insert_4_values_ascending() throws Exception {
        tree.put(1, -10);
        tree.put(2, -20);
        tree.put(3, -30);
        tree.put(4, -40);
        logger.debug(tree.toString());
    }

    @Test
    public void test_insert_4_values_descending() throws Exception {
        tree.put(5, -50);
        tree.put(4, -40);
        tree.put(3, -30);
        tree.put(2, -20);
        logger.debug(tree.toString());
    }

    @Test
    public void test_overwriting_values() throws Exception {
        assertNull(tree.put(1, -10));
        assertNull(tree.put(2, -20));
        assertNull(tree.put(3, -30));
        assertNull(tree.put(4, -40));
        assertEquals(Integer.valueOf(-10), tree.put(1, -100));
        logger.debug(tree.toString());
    }

    @Test
    public void test_insert_50_values() throws Exception {
        for (int i = 1; i < 51; i++) {
            logger.debug("inserting " + i);
            tree.put(i, -i + 10);
            logger.debug(tree.toString());
        }

        assertEquals(50, tree.size());
    }

    @Test
    public void test_remove_3_values() throws Exception {
        tree.put(1, -10);
        tree.put(2, -20);
        tree.put(3, -30);

        logger.debug(tree.toString());
        treeUtil.toDotFile(new File("pok.dot"));
        tree.remove(1);
        logger.debug(tree.toString());
        assertEquals(2, tree.size());
        assertEquals("All locks should be unlocked ", 0,
                tree.countLockedNodes());
        logger.debug(tree.toString());
        tree.remove(3);
        assertEquals(1, tree.size());
        logger.debug(tree.toString());
        assertEquals("All locks should be unlocked ", 0,
                tree.countLockedNodes());
        tree.remove(2);
        logger.debug(tree.toString());
        assertEquals(0, tree.size());
    }

    @Test
    public void test_insert_10_asc_remove_10_asc() throws Exception {
        insert_10_ascending();
        assertEquals(10, tree.size());
        verify_contains_10();

        remove_10_ascending();
        assertEquals(0, tree.size());
        treeUtil.toDotFile(new File("pok.dot"));
    }

    @Test
    public void test_insert_10_asc_remove_10_desc() throws Exception {
        insert_10_ascending();
        assertEquals(10, tree.size());
        logger.debug(tree.toString());

        verify_search_10();
        remove_10_descending();
        assertEquals(0, tree.size());
    }

    @Test
    public void test_insert_10_desc_remove_10_asc() throws Exception {
        insert_10_descending();
        assertEquals(10, tree.size());

        remove_10_ascending();
        assertEquals(0, tree.size());
    }

    @Test
    public void test_insert_10_desc_remove_10_desc() throws Exception {
        insert_10_descending();
        assertEquals(10, tree.size());

        remove_10_descending();
        assertEquals(0, tree.size());
    }

    private void insert_10_ascending() {
        tree.put(1, -10);
        tree.put(2, -20);
        tree.put(3, -30);
        tree.put(4, -40);
        tree.put(5, -50);
        tree.put(6, -60);
        tree.put(7, -70);
        tree.put(8, -80);
        tree.put(9, -90);
        tree.put(10, -100);
    }

    private void remove_10_ascending() {
        tree.remove(1);
        tree.remove(2);
        tree.remove(3);
        tree.remove(4);
        tree.remove(5);
        tree.remove(6);
        tree.remove(7);
        tree.remove(8);
        tree.remove(9);
        tree.remove(10);
    }

    private void remove_10_descending() {
        tree.remove(10);
        logger.debug(tree.toString());
        assertEquals("All locks should be unlocked ", 0,
                tree.countLockedNodes());
        tree.remove(9);
        logger.debug(tree.toString());
        assertEquals("All locks should be unlocked ", 0,
                tree.countLockedNodes());
        tree.remove(8);
        tree.remove(7);
        logger.debug(tree.toString());
        assertEquals("All locks should be unlocked ", 0,
                tree.countLockedNodes());
        tree.remove(6);
        tree.remove(5);
        tree.remove(4);
        tree.remove(3);
        tree.remove(2);
        tree.remove(1);
    }

    private void verify_search_10() {
        assertEquals(Integer.valueOf(-10), tree.get(1));
        assertEquals(Integer.valueOf(-20), tree.get(2));
        assertEquals(Integer.valueOf(-30), tree.get(3));
        assertEquals(Integer.valueOf(-40), tree.get(4));
        assertEquals(Integer.valueOf(-50), tree.get(5));
        assertEquals(Integer.valueOf(-60), tree.get(6));
        assertEquals(Integer.valueOf(-70), tree.get(7));
        assertEquals(Integer.valueOf(-80), tree.get(8));
        assertEquals(Integer.valueOf(-90), tree.get(9));
        assertEquals(Integer.valueOf(-100), tree.get(10));
    }

    private void verify_contains_10() {
        assertTrue(tree.containsKey(1));
        assertTrue(tree.containsKey(2));
        assertTrue(tree.containsKey(3));
        assertTrue(tree.containsKey(4));
        assertTrue(tree.containsKey(5));
        assertTrue(tree.containsKey(6));
        assertTrue(tree.containsKey(7));
        assertTrue(tree.containsKey(8));
        assertTrue(tree.containsKey(9));
        assertTrue(tree.containsKey(10));
    }

    private void insert_10_descending() {
        tree.put(10, -100);
        tree.put(9, -90);
        tree.put(8, -80);
        tree.put(7, -70);
        tree.put(6, -60);
        tree.put(5, -50);
        tree.put(4, -40);
        tree.put(3, -30);
        tree.put(2, -20);
        tree.put(1, -10);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tree = TreeBuilder.builder().setL(2).setKeyType(Types.integer())
                .setValueType(Types.integer()).build();
        treeUtil = new TreeUtil(tree);
    }

    @Override
    protected void tearDown() throws Exception {
        assertEquals("All locks should be unlocked ", 0,
                tree.countLockedNodes());
        tree = null;
        treeUtil = null;
        super.tearDown();
    }

}
