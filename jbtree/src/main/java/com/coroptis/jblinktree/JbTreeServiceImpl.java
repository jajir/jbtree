package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.Wrapper;
import com.google.common.base.Preconditions;

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

/**
 * Immutable implementation of {@link JbTreeService}.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 *
 */
public final class JbTreeServiceImpl<K, V> implements JbTreeService<K, V> {

    /**
     * Default node store.
     */
    private final NodeStore<K> nodeStore;

    /**
     * Tool for traversing through tree.
     */
    private final JbTreeTraversingService<K, V> treeTraversingService;

    /**
     * Node service.
     */
    private final JbNodeService<K, V> nodeService;

    /**
     * Simple constructor.
     *
     * @param initNodeStore
     *            required node store
     * @param initTreeTraversingService
     *            required tree traversing tool
     * @param jbNodeService
     *            node service
     */
    public JbTreeServiceImpl(final NodeStore<K> initNodeStore,
            final JbTreeTraversingService<K, V> initTreeTraversingService,
            final JbNodeService<K, V> jbNodeService) {
        this.nodeStore = Preconditions.checkNotNull(initNodeStore);
        this.treeTraversingService =
                Preconditions.checkNotNull(initTreeTraversingService);
        this.nodeService = Preconditions.checkNotNull(jbNodeService);
    }

    @Override
    public <S> Node<K, Integer> loadParentNode(final Node<K, S> currentNode,
            final Wrapper<K> tmpKey, final Integer nextNodeId) {
        Node<K, Integer> parentNode = nodeStore.getAndLock(nextNodeId);
        // TODO link to current node which key should be updated can be in
        // different node than tmpKey
        parentNode =
                treeTraversingService.moveRightNonLeafNode(parentNode, tmpKey);
        if (nodeService.updateKeyForValue(parentNode, currentNode.getId(),
                currentNode.getMaxKey())) {
            nodeStore.writeNode(parentNode);
        }
        return parentNode;
    }

    @Override
    public <S> void storeValueIntoNode(final Node<K, S> currentNode,
            final Wrapper<K> key, final S value) {
        nodeService.insert(currentNode, key, value);
        nodeStore.writeNode(currentNode);
        nodeStore.unlockNode(currentNode.getId());
    }

    @Override
    public Node<K, V> findSmallerNode(final Integer rootNodeId) {
        Integer currentNodeId = rootNodeId;
        while (true) {
            final Node<K, V> node = nodeStore.get(currentNodeId);
            if (node.isLeafNode()) {
                return node;
            } else {
                currentNodeId = (Integer) node.getValue(0);
            }
        }
    }

}
