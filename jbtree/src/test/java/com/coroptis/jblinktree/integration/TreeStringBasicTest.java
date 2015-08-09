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

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeMap;
import com.coroptis.jblinktree.type.Types;

/**
 * Test Map with {@link String} as key and value. It verify that field size is
 * used correctly.
 * 
 * @author jajir
 * 
 */
public class TreeStringBasicTest extends TestCase {

    private Logger logger = LoggerFactory.getLogger(TreeStringBasicTest.class);

    private TreeMap<String, String> tree;

    @Test
    public void testJustOneNode() throws Exception {

	tree.put("3", "-30");
	tree.put("1", "-10");

	logger.debug(tree.toString());
	assertEquals(2, tree.size());
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	tree = TreeBuilder.builder().setL(2).setKeyType(Types.string())
		.setValueType(Types.string()).build();
    }

    @Override
    protected void tearDown() throws Exception {
	assertEquals("All locks should be unlocked ", 0, tree.countLockedNodes());
	tree = null;
	super.tearDown();
    }

}
