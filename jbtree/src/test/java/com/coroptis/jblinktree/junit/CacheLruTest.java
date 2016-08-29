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

import static org.junit.Assert.assertSame;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.store.CacheLru;

public class CacheLruTest extends AbstractMockingTest {

    private CacheLru<Integer, Integer> cache;

    private byte[] bytes;

    @Test
    public void test_get_simple() throws Exception {
        loadNode(23, n1);
        replay();
        Node<Integer, Integer> ret = cache.get(23);

        verify();
        assertSame(ret, n1);
    }

    @Test
    public void test_get_cached() throws Exception {
        loadNode(23, n1);
        EasyMock.expect(nodeBuilder.makeNode(23, bytes)).andReturn((Node) n1);
        replay();
        cache.get(23);
        Node<Integer, Integer> ret = cache.get(23);

        verify();
        assertSame(ret, n1);
    }

    @Test
    public void test_get_evicting_1() throws Exception {
        loadNode(23, n1);
        loadNode(12, n2);
        EasyMock.expect(nodeBuilder.makeNode(23, bytes)).andReturn((Node) n1);
        cacheListener.onUnload(n1, false);
        replay();
        cache.get(23);
        Node<Integer, Integer> ret = cache.get(12);

        verify();
        assertSame(ret, n2);
    }

    @Test
    public void test_get_evicting_order() throws Exception {
        loadNode(11, n1);
        loadNode(22, n2);
        loadNode(33, n3);
        //evicting 1
        EasyMock.expect(nodeBuilder.makeNode(11, bytes)).andReturn((Node) n1);
        cacheListener.onUnload(n1, false);
        //evicting 2
        EasyMock.expect(nodeBuilder.makeNode(22, bytes)).andReturn((Node) n2);
        cacheListener.onUnload(n2, false);
        replay();
        cache.get(11);
        cache.get(22);
        Node<Integer, Integer> ret = cache.get(33);

        verify();
        assertSame(ret, n3);
    }

    @Test
    public void test_put() throws Exception {
        EasyMock.expect(n1.getFieldBytes()).andReturn(bytes);
        EasyMock.expect(n1.getId()).andReturn(23).times(1);
        replay();
        cache.put(n1);

        verify();
    }

    @Test
    public void test_get_evicting_1_changed() throws Exception {
        EasyMock.expect(n1.getId()).andReturn(23).times(1);
        EasyMock.expect(n1.getFieldBytes()).andReturn(bytes);
        // loadNode(23, n1);
        loadNode(12, n2);
        EasyMock.expect(nodeBuilder.makeNode(23, bytes)).andReturn((Node) n1);
        cacheListener.onUnload(n1, true);
        replay();
        cache.put(n1);
        Node<Integer, Integer> ret = cache.get(12);

        verify();
        assertSame(ret, n2);
    }
    
    @Test
    public void test_remove_not_in_cache() throws Exception {
        loadNode(11, n1);
        loadNode(22, n2);
        //evicting 1
        EasyMock.expect(nodeBuilder.makeNode(11, bytes)).andReturn((Node) n1);
        cacheListener.onUnload(n1, false);
        replay();
        cache.get(11);
        cache.get(22);
        cache.remove(11);
        
        verify();
    }
    
    @Test
    public void test_remove() throws Exception {
        loadNode(11, n1);
        loadNode(22, n2);
        //evicting 1
        EasyMock.expect(nodeBuilder.makeNode(11, bytes)).andReturn((Node) n1);
        cacheListener.onUnload(n1, false);
        //evicting from remove
        EasyMock.expect(nodeBuilder.makeNode(22, bytes)).andReturn((Node) n2);
        cacheListener.onUnload(n2, false);
        replay();
        cache.get(11);
        cache.get(22);
        cache.remove(22);

        verify();
    }


    private void loadNode(final Integer nodeId, Node<Integer, Integer> node) {
        EasyMock.expect(cacheListener.onLoad(nodeId)).andReturn(node);
        EasyMock.expect(node.getFieldBytes()).andReturn(bytes);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        bytes = new byte[23];
        cache = new CacheLru<Integer, Integer>(nodeBuilder, 1, cacheListener);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        cache = null;
        bytes = null;
        super.tearDown();
    }

}
