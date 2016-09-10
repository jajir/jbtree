package com.coroptis.jblinktree;

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

import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.type.Wrapper;
import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayList;
import com.google.common.base.Preconditions;

/**
 * Immutable implementation of {@link JbTreeHelper}.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class JbTreeHelperImpl<K, V> implements JbTreeHelper<K, V> {

    /**
     * Node store.
     */
    private final NodeStore<K> nodeStore;

    /**
     * Tree tools. Helps with basic tree operations.
     */
    private final JbTreeTool<K, V> treeTool;

    /**
     * Tree service. More complex tree operations.
     */
    private final JbTreeService<K, V> treeService;

    /**
     * Tree data definition.
     */
    private final JbTreeData<K, V> treeData;

    /**
     * Simple constructor.
     *
     * @param initNodeStore
     *            required node storage
     * @param initTreeTool
     *            required tree tool
     * @param initTreeService
     *            required tree service
     * @param initTreeData
     *            required tree data definition
     */
    public JbTreeHelperImpl(final NodeStore<K> initNodeStore,
            final JbTreeTool<K, V> initTreeTool,
            final JbTreeService<K, V> initTreeService,
            final JbTreeData<K, V> initTreeData) {
        this.nodeStore = Preconditions.checkNotNull(initNodeStore);
        this.treeTool = Preconditions.checkNotNull(initTreeTool);
        this.treeService = Preconditions.checkNotNull(initTreeService);
        this.treeData = Preconditions.checkNotNull(initTreeData);
    }

    @Override
    public Node<K, V> findAppropriateLeafNode(final Wrapper<K> key) {
        Preconditions.checkNotNull(key);
        Integer idNode = treeTool.findLeafNodeId(key, new JbStackArrayList(),
                treeData.getRootNodeId());
        Node<K, V> node = nodeStore.get(idNode);
        return treeTool.moveRightLeafNodeWithoutLocking(node, key);
    }

    // TODO following methods should be refactored

    @Override
    public V insertToLeafNode(final Node<K, V> currentNode,
            final Wrapper<K> key, final V value, final JbStack stack) {
        if (currentNode.getKeyCount() >= treeData.getL()) {
            final Node<K, V> newNode = storeSplitLeafNode(currentNode, key,
                    value);
            if (stack.isEmpty()) {
                splitRootNode(currentNode, newNode);
                return null;
            } else {
                Integer tmpValue = newNode.getId();
                Wrapper<K> tmpKey = newNode.getMaxKey();
                final Integer previousCurrentNodeId = currentNode.getId();
                Node<K, Integer> previousNode = treeService
                        .loadParentNode(currentNode, tmpKey, stack.pop());
                nodeStore.unlockNode(previousCurrentNodeId);
                return insertNonLeaf(previousNode, tmpKey, tmpValue, stack);
            }
        } else {
            treeService.storeValueIntoNode(currentNode, key, value);
            return null;
        }
    }

    /**
     * Write given key value pair into non-leaf node.
     *
     * @param node
     *            required leaf node
     * @param key
     *            required key
     * @param value
     *            required value
     * @param stack
     *            required stack useful for back tracing through tree
     * @return <code>null</code> when it's new key otherwise return old value
     */
    @SuppressWarnings("unchecked")
    private V insertNonLeaf(final Node<K, Integer> node, final Wrapper<K> key,
            final Integer value, final JbStack stack) {
        Node<K, Integer> currentNode = node;
        /**
         * Key and value have to be inserted
         */
        Integer tmpValue = value;
        Wrapper<K> tmpKey = key;
        while (true) {
            if (currentNode.getKeyCount() >= treeData.getL()) {
                final Node<K, Integer> newNode = storeSplitNonLeafNode(
                        currentNode, tmpKey, tmpValue);
                if (stack.isEmpty()) {
                    splitRootNode((Node<K, V>) currentNode,
                            (Node<K, V>) newNode);
                    return null;
                } else {
                    tmpValue = newNode.getId();
                    tmpKey = newNode.getMaxKey();
                    final Integer previousCurrentNodeId = currentNode.getId();
                    currentNode = treeService.loadParentNode(currentNode,
                            tmpKey, stack.pop());
                    nodeStore.unlockNode(previousCurrentNodeId);
                }
            } else {
                treeService.storeValueIntoNode(currentNode, tmpKey, tmpValue);
                return null;
            }
        }
    }

    // FIXME pair methods move to separate typed class

    /**
     * Split node and store new and old node.
     *
     * @param currentNode
     *            required node which will be split
     * @param key
     *            required inserted key
     * @param value
     *            required inserted value
     * @return newly created node, this node contains higher part of keys.
     */
    private Node<K, V> storeSplitLeafNode(final Node<K, V> currentNode,
            final Wrapper<K> key, final V value) {
        final Node<K, V> newNode = treeTool.splitLeafNode(currentNode, key,
                value);
        nodeStore.writeNode(newNode);
        nodeStore.writeNode(currentNode);
        return newNode;
    }

    /**
     * Split node and store new and old node.
     *
     * @param currentNode
     *            required node which will be split
     * @param key
     *            required inserted key
     * @param value
     *            required inserted value
     * @return newly created node, this node contains higher part of keys.
     */
    private Node<K, Integer> storeSplitNonLeafNode(
            final Node<K, Integer> currentNode, final Wrapper<K> key,
            final Integer value) {
        final Node<K, Integer> newNode = treeTool.splitNonLeafNode(currentNode,
                key, value);
        nodeStore.writeNode(newNode);
        nodeStore.writeNode(currentNode);
        return newNode;
    }

    /**
     * Split node.
     *
     * @param currentNode
     *            required node which will be split
     * @param newNode
     *            required node to which will moved data from current node
     * @return id of new tree root node
     */
    private Integer splitRootNode(final Node<K, V> currentNode,
            final Node<K, V> newNode) {
        ReentrantLock lock = new ReentrantLock(false);
        lock.lock();
        try {
            if (treeData.getRootNodeId().equals(currentNode.getId())) {
                Preconditions.checkArgument(
                        treeData.getRootNodeId().equals(currentNode.getId()));
                treeData.setRootNodeId(
                        treeTool.splitRootNode(currentNode, newNode));
                nodeStore.unlockNode(currentNode.getId());
            } else {
                nodeStore.unlockNode(currentNode.getId());
            }
        } finally {
            lock.unlock();
        }
        return treeData.getRootNodeId();
    }

    @Override
    public void visit(final JbDataVisitor<K, V> dataVisitor) {
        Node<K, V> currentNode = treeService
                .findSmallerNode(treeData.getRootNodeId());
        while (true) {
            for (int i = 0; i < currentNode.getKeyCount(); i++) {
                if (!dataVisitor.visited(currentNode.getKey(i),
                        currentNode.getValue(i))) {
                    return;
                }
            }
            if (Node.EMPTY_INT.equals(currentNode.getLink())) {
                return;
            } else {
                currentNode = nodeStore.get(currentNode.getLink());
            }
        }
    }

}
