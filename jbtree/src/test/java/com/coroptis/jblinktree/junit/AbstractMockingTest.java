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
import static org.easymock.EasyMock.createMock;

import org.easymock.EasyMock;

import com.coroptis.jblinktree.JbNodeBuilder;
import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeLockProvider;
import com.coroptis.jblinktree.JbNodeService;
import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.JbTreeHelper;
import com.coroptis.jblinktree.JbTreeService;
import com.coroptis.jblinktree.JbTreeTool;
import com.coroptis.jblinktree.JbTreeTraversingService;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeStore;
import com.coroptis.jblinktree.store.CacheListener;
import com.coroptis.jblinktree.store.NodeFileStorage;
import com.coroptis.jblinktree.store.NodeLoader;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.Wrapper;

/**
 * Class
 *
 * @author jajir
 *
 */
public abstract class AbstractMockingTest {

    protected JbTreeTool<Integer, Integer> treeTool;
    protected JbTreeTraversingService<Integer, Integer> treeTraversingService;
    protected NodeStore<Integer> nodeStore;
    protected NodeFileStorage<Integer, Integer> nodeFileStorage;
    protected JbTreeService<Integer, Integer> jbTreeService;
    protected JbTreeHelper<Integer, Integer> treeHelper;
    protected JbTreeData<Integer, Integer> treeData;
    protected JbNodeDef<Integer, Integer> nodeDef;
    protected TypeDescriptorInteger tdi;
    protected JbNodeBuilder<Integer, Integer> nodeBuilder;
    protected Node<Integer, Integer> n1, n2, n3;
    protected Node<Integer, String> n4;
    protected JbNodeService<Integer, Integer> nodeService;
    protected CacheListener<Integer, Integer> cacheListener;
    protected Wrapper<Integer> w1, w2, w3;
    protected NodeLoader<Integer, Integer> nodeLoader;
    protected JbNodeLockProvider jbNodeLockProvider;
    protected Object[] mocks;

    @SuppressWarnings("unchecked")
    protected void setUp() throws Exception {
        nodeStore = createMock(NodeStore.class);
        nodeBuilder = createMock(JbNodeBuilder.class);
        treeTool = createMock(JbTreeTool.class);
        n1 = createMock(Node.class);
        n2 = createMock(Node.class);
        n3 = createMock(Node.class);
        n4 = createMock(Node.class);
        jbTreeService = createMock(JbTreeService.class);
        treeHelper = createMock(JbTreeHelper.class);
        treeData = createMock(JbTreeData.class);
        nodeService = createMock(JbNodeService.class);
        cacheListener = createMock(CacheListener.class);
        treeTraversingService = createMock(JbTreeTraversingService.class);
        nodeDef = createMock(JbNodeDef.class);
        tdi = new TypeDescriptorInteger();
        w1 = Wrapper.make(12, tdi);
        w2 = Wrapper.make(93, tdi);
        w3 = Wrapper.make(74, tdi);
        nodeLoader = createMock(NodeLoader.class);
        jbNodeLockProvider = createMock(JbNodeLockProvider.class);
        nodeFileStorage = createMock(NodeFileStorage.class);
        mocks = new Object[] { nodeStore, nodeBuilder, treeTool, n1, n2, n3, n4,
                jbTreeService, treeHelper, treeData, treeTraversingService,
                nodeService, cacheListener, nodeDef, nodeLoader,
                jbNodeLockProvider, nodeFileStorage };
    }

    protected void tearDown() throws Exception {
        n1 = null;
        n2 = null;
        n3 = null;
        n4 = null;
        treeTraversingService = null;
        nodeStore = null;
        mocks = null;
        nodeService = null;
        cacheListener = null;
        treeData = null;
        nodeDef = null;
        tdi = null;
        nodeLoader = null;
        jbNodeLockProvider = null;
        nodeFileStorage = null;
    }

    protected void replay(final Object... otherMocks) {
        EasyMock.replay(mocks);
        EasyMock.replay(otherMocks);
    }

    protected void verify(final Object... otherMocks) {
        EasyMock.verify(mocks);
        EasyMock.verify(otherMocks);
    }

}
