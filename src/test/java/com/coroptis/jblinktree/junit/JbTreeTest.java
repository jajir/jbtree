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

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.junit.Test;

import com.coroptis.jblinktree.JbTreeImpl;
import com.coroptis.jblinktree.JbTreeToolImpl;
import com.coroptis.jblinktree.NodeStore;

/**
 * Junit test form {@link JbTreeImpl}.
 * 
 * @author jan
 * 
 */
public class JbTreeTest extends TestCase {

    private JbTreeImpl jbTree;

    private NodeStore nodeStore;

    @Test
    public void test_countValues() throws Exception {
	EasyMock.replay(nodeStore);
	jbTree.countValues();
	EasyMock.verify(nodeStore);
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	nodeStore = EasyMock.createMock(NodeStore.class);
	jbTree = new JbTreeImpl(3, nodeStore, new JbTreeToolImpl());
    }

    @Override
    protected void tearDown() throws Exception {
	jbTree = null;
	nodeStore = null;
	super.tearDown();
    }

}
