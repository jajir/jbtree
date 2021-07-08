package com.coroptis.jblinktree;

import java.util.Objects;

import com.coroptis.jblinktree.type.Wrapper;

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

import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayList;
import com.coroptis.jblinktree.util.JblinktreeException;

/**
 * Immutable implementation of {@link JbTree}.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 *
 *
 */
public final class JbTreeImpl<K, V> implements JbTree<K, V> {

    /**
     * Node store.
     */
    private final NodeStore<K> nodeStore;

    /**
     * Simple tree tool.
     */
    private final JbTreeTool<K, V> treeTool;

    /**
     * Tree helper.
     */
    private final JbTreeHelper<K, V> treeHelper;

    /**
     * Tree data definition.
     */
    private final JbTreeData<K, V> treeData;

    /**
     * Tool for traversing in tree.
     */
    private final JbTreeTraversingService<K, V> treeTraversingService;

    /**
     * Tree service.
     */
    private final JbTreeService<K, V> treeService;

    /**
     * Node service.
     */
    private final JbNodeService<K, V> nodeService;

    /**
     * Create and initialize tree.
     *
     * @param jbNodeStore
     *            required {@link NodeStore} object
     * @param jbTreeTool
     *            required {@link JbTreeTool} object
     * @param jbTreeHelper
     *            required {@link JbTreeHelper} object
     * @param initTreeData
     *            required {@link JbTreeData} object
     * @param jbTreeTraversingService
     *            required {@link JbTreeTraversingService} object
     * @param jbTreeService
     *            required {@link JbTreeService} object
     * @param jbNodeService
     *            required {@link JbNodeService} object
     */
    public JbTreeImpl(final NodeStore<K> jbNodeStore,
            final JbTreeTool<K, V> jbTreeTool,
            final JbTreeHelper<K, V> jbTreeHelper,
            final JbTreeData<K, V> initTreeData,
            final JbTreeTraversingService<K, V> jbTreeTraversingService,
            final JbTreeService<K, V> jbTreeService,
            final JbNodeService<K, V> jbNodeService) {
        this.nodeStore = Objects.requireNonNull(jbNodeStore);
        this.treeTool = Objects.requireNonNull(jbTreeTool);
        this.treeHelper = Objects.requireNonNull(jbTreeHelper);
        this.treeData = Objects.requireNonNull(initTreeData);
        this.treeTraversingService =
                Objects.requireNonNull(jbTreeTraversingService);
        this.treeService = Objects.requireNonNull(jbTreeService);
        this.nodeService = Objects.requireNonNull(jbNodeService);
    }

    @Override
    public V insert(final Wrapper<K> key, final V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        final JbStack stack = new JbStackArrayList();
        final Integer currentNodeId =
                treeTool.findLeafNodeId(key, stack, treeData.getRootNodeId());
        Node<K, V> currentNode = nodeStore.getAndLock(currentNodeId);
        currentNode = treeTraversingService.moveRightLeafNode(currentNode, key);
        final V oldValue = nodeService.getValueByKey(currentNode, key);
        if (oldValue == null) {
            return treeHelper.insertToLeafNode(currentNode, key, value, stack);
        } else {
            /**
             * Key already exists. Rewrite value.
             */
            treeService.storeValueIntoNode(currentNode, key, value);
            return oldValue;
        }
    }

    @Override
    public V remove(final Wrapper<K> key) {
        Objects.requireNonNull(key);
        final JbStack stack = new JbStackArrayList();
        Integer currentNodeId =
                treeTool.findLeafNodeId(key, stack, treeData.getRootNodeId());
        Node<K, V> currentNode = nodeStore.getAndLock(currentNodeId);
        currentNode = treeTraversingService.moveRightLeafNode(currentNode, key);
        if (nodeService.getValueByKey(currentNode, key) == null) {
            /**
             * Node doesn't contains key, there is nothing to delete
             */
            nodeStore.unlockNode(currentNode.getId());
            return null;
        } else {
            /**
             * Leaf node contains key so remove it.
             */
            final V oldValue = nodeService.remove(currentNode, key);
            nodeStore.writeNode(currentNode);
            nodeStore.unlockNode(currentNode.getId());
            return oldValue;
        }
    }

    @Override
    public V search(final Wrapper<K> key) {
        Objects.requireNonNull(key);
        return nodeService
                .getValueByKey(treeHelper.findAppropriateLeafNode(key), key);
    }

    @Override
    public int countValues() {
        JbTreeVisitorRecordCounter<K, V> counter =
                new JbTreeVisitorRecordCounter<K, V>();
        visitLeafNodes(counter);
        return counter.getCount();
    }

    @Override
    public boolean containsKey(final Wrapper<K> key) {
        Objects.requireNonNull(key);
        return nodeService.getValueByKey(
                treeHelper.findAppropriateLeafNode(key), key) != null;
    }

    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        buff.append("Detail tree description continues: root node id: ");
        buff.append(treeData.getRootNodeId());
        buff.append("\n");
        visit(new JbTreeVisitor<K, V>() {
            @Override
            public boolean visitedLeaf(final Node<K, V> node) {
                buff.append(node.toString());
                buff.append("\n");
                return true;
            }

            @Override
            public boolean visitedNonLeaf(final Node<K, Integer> node) {
                buff.append(node.toString());
                buff.append("\n");
                return true;
            }
        });
        return buff.toString();
    }

    /**
     * Current implementation doesn't visit nodes that are available with link
     * pointer and don't have parent node.
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    public void visit(final JbTreeVisitor<K, V> treeVisitor) {
        Objects.requireNonNull(treeVisitor,
                "required JbTreeVisitor instance is null");
        final JbStack stack = new JbStackArrayList();
        stack.push(treeData.getRootNodeId());
        while (!stack.isEmpty()) {
            final Integer nodeId = stack.pop();
            if (nodeId == null) {
                throw new JblinktreeException("some node id was null");
            } else {
                final Node<K, V> node = nodeStore.get(nodeId);
                if (node.isLeafNode()) {
                    if (!treeVisitor.visitedLeaf(node)) {
                        return;
                    }
                } else {
                    if (!treeVisitor.visitedNonLeaf((Node<K, Integer>) node)) {
                        return;
                    }
                    for (final Integer i : nodeService
                            .getNodeIds((Node<K, Integer>) node)) {
                        stack.push(i);
                    }
                }
            }
        }
    }

    /**
     */
    @SuppressWarnings("unchecked")
    @Override
    public void visitLeafNodes(final JbTreeVisitor<K, V> treeVisitor) {
        Objects.requireNonNull(treeVisitor,
                "required JbTreeVisitor instance is null");
        // find smaller node.
        Node<K, V> leafNode = null;
        Integer nodeId = treeData.getRootNodeId();
        while (leafNode == null) {
            final Node<K, V> node = nodeStore.get(nodeId);
            if (node.isLeafNode()) {
                leafNode = node;
            } else {
                // move to the next smaller node.
                Node<K, Integer> n = (Node<K, Integer>) node;
                nodeId = nodeService.getNodeIds(n).get(0);
            }
        }
        // iterate all leaf nodes until last one.
        while (leafNode != null) {
            if (!treeVisitor.visitedLeaf(leafNode)) {
                return;
            }
            if (leafNode.getLink() < 0) {
                return;
            } else {
                leafNode = nodeStore.get(leafNode.getLink());
            }
        }
    }

    @Override
    public int countLockedNodes() {
        return nodeStore.countLockedNodes();
    }

    @Override
    public void close() {
        nodeStore.close();
    }

    @Override
    public void visit(final JbDataVisitor<K, V> dataVisitor) {
        treeHelper.visit(dataVisitor);
    }

}
