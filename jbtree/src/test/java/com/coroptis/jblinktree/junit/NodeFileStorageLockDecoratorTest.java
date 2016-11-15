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

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.store.NodeFileStorage;
import com.coroptis.jblinktree.store.NodeFileStorageLockDecorator;
import com.coroptis.jblinktree.util.JblinktreeException;

public class NodeFileStorageLockDecoratorTest extends AbstractMockingTest {

    private NodeFileStorage<Integer, Integer> storage;

    @Test
    public void test_load() throws Exception {
        expect(nodeFileStorage.load(23)).andReturn(n1);
        replay();
        Node<Integer, Integer> ret = storage.load(23);

        assertSame(n1, ret);
        verify();
    }

    @Test(expected = JblinktreeException.class)
    public void test_load_exception() throws Exception {
        expect(nodeFileStorage.load(23))
                .andThrow(new JblinktreeException("exception"));
        replay();
        storage.load(23);
    }

    @Test
    public void test_store() throws Exception {
        nodeFileStorage.store(n1);
        replay();
        storage.store(n1);

        verify();
    }

    @Test
    public void test_close() throws Exception {
        nodeFileStorage.close();
        replay();
        storage.close();

        verify();
    }

    @Test
    public void test_isNewlyCreated() throws Exception {
        expect(nodeFileStorage.isNewlyCreated()).andReturn(true);
        replay();
        boolean ret = storage.isNewlyCreated();

        assertTrue(ret);
        verify();
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        storage = new NodeFileStorageLockDecorator<Integer, Integer>(
                nodeFileStorage);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        storage = null;
        super.tearDown();
    }
}
