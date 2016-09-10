package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.Wrapper;
import com.coroptis.jblinktree.util.JbStack;
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
 * Immutable implementation of {@link JbTreeTool}.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class JbTreeToolImpl<K, V> implements JbTreeTool<K, V> {

    /**
     * Node store.
     */
    private final NodeStore<K> nodeStore;

    /**
     * Node builder factory.
     */
    private final JbNodeBuilder<K, V> nodeBuilder;

    /**
     * Tree meta data.
     */
    private final JbTreeData<K, V> treeData;

    /**
     * Node service.
     */
    private final JbNodeService<K, V> nodeService;

    /**
     * Default constructor.
     *
     * @param initNodeStore
     *            required node store service
     * @param jbTreeData
     *            required tree data descriptor
     * @param initNodeBuilder
     *            required node builder
     * @param jbNodeService
     *            node service
     */
    public JbTreeToolImpl(final NodeStore<K> initNodeStore,
            final JbTreeData<K, V> jbTreeData,
            final JbNodeBuilder<K, V> initNodeBuilder,
            final JbNodeService<K, V> jbNodeService) {
        this.nodeStore = Preconditions.checkNotNull(initNodeStore);
        this.treeData = Preconditions.checkNotNull(jbTreeData);
        this.nodeBuilder = Preconditions.checkNotNull(initNodeBuilder);
        this.nodeService = Preconditions.checkNotNull(jbNodeService);
    }

    @Override
    public boolean canMoveToNextNode(final Node<K, ?> node,
            final Wrapper<K> key) {
        if (NodeImpl.EMPTY_INT.equals(node.getLink())) {
            return false;
        }
        if (node.isEmpty()) {
            return true;
        }
        return node.getMaxKey() != null
                && node.compareKey(node.getMaxKeyIndex(), key) < 0;
    }

    @Override
    public Node<K, V> moveRightLeafNodeWithoutLocking(final Node<K, V> node,
            final Wrapper<K> key) {
        Node<K, V> current = node;
        if (current.isLeafNode()) {
            while (!Node.EMPTY_INT.equals(current.getLink())
                    && current.compareKey(current.getMaxKeyIndex(), key) < 0) {
                current = nodeStore.get(current.getLink());
            }
            return current;
        } else {
            throw new JblinktreeException(
                    "method is for leaf nodes, but given node is non-leaf");
        }
    }

    @Override
    public Integer findLeafNodeId(final Wrapper<K> key, final JbStack stack,
            final Integer rootNodeId) {
        Node<K, Integer> currentNode = nodeStore.get(rootNodeId);
        while (!currentNode.isLeafNode()) {
            Integer nextNodeId = nodeService.getCorrespondingNodeId(currentNode,
                    key);
            if (NodeImpl.EMPTY_INT.equals(nextNodeId)) {
                /**
                 * This is rightmost node and next link is <code>null</code> so
                 * use node id associated with bigger key.
                 */
                stack.push(currentNode.getId());
                nextNodeId = nodeService.getCorrespondingNodeId(currentNode,
                        currentNode.getMaxKey());
                if (NodeImpl.EMPTY_INT.equals(nextNodeId)) {
                    throw new JblinktreeException(
                            "There is no node id for max value '"
                                    + currentNode.getMaxKey() + "' in node "
                                    + currentNode.toString());
                }
            } else if (!nextNodeId.equals(currentNode.getLink())) {
                /**
                 * I don't want to store nodes when cursor is moved right.
                 */
                stack.push(currentNode.getId());
            }
            currentNode = nodeStore.get(nextNodeId);
        }
        return currentNode.getId();
    }

    @Override
    public Node<K, V> splitLeafNode(final Node<K, V> currentNode,
            final Wrapper<K> key, final V value) {
        final Node<K, V> newNode = nodeBuilder
                .makeEmptyLeafNode(treeData.getNextId());
        return splitNode(currentNode, newNode, key, value);
    }

    @Override
    public Node<K, Integer> splitNonLeafNode(final Node<K, Integer> currentNode,
            final Wrapper<K> key, final Integer value) {
        final Node<K, Integer> newNode = nodeBuilder
                .makeEmptyNonLeafNode(treeData.getNextId());
        return splitNode(currentNode, newNode, key, value);
    }

    /**
     * Move top half of data from currentNode to newNode. Than insert given key
     * value par into proper node.
     *
     * @param currentNode
     *            required current node
     * @param newNode
     *            required new node
     * @param key
     *            required inserted key wrapper
     * @param value
     *            required inserted value
     * @param <S>
     *            value data type
     * @return new node with filled data
     */
    private <S> Node<K, S> splitNode(final Node<K, S> currentNode,
            final Node<K, S> newNode, final Wrapper<K> key, final S value) {
        currentNode.moveTopHalfOfDataTo(newNode);
        if (currentNode.compareKey(currentNode.getMaxKeyIndex(), key) < 0) {
            nodeService.insert(newNode, key, value);
        } else {
            nodeService.insert(currentNode, key, value);
        }
        return newNode;
    }

    /**
     *
     * @return new root id
     */
    @Override
    public <S> Integer splitRootNode(final Node<K, S> currentRootNode,
            final Node<K, S> newNode) {
        // TODO consider case when new node is smaller that currentRootNode
        Node<K, Integer> newRoot = nodeBuilder.makeNonLeafNode(
                treeData.getNextId(), currentRootNode.getId(),
                currentRootNode.getMaxKey(), newNode.getId(),
                newNode.getMaxKey());
        nodeStore.writeNode(newRoot);
        return newRoot.getId();
    }

    @Override
    public Integer createRootNode() {
        Node<K, V> node = nodeBuilder.makeEmptyLeafNode(treeData.getNextId());
        this.nodeStore.writeNode(node);
        return node.getId();
    }

    @Override
    public <S> Node<K, S> moveToNextNode(final Node<K, ?> currentNode,
            final Integer nextNodeId) {
        final Node<K, S> n = nodeStore.getAndLock(nextNodeId);
        nodeStore.unlockNode(currentNode.getId());
        return n;
    }
}
