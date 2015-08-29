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

import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayDeque;
import com.google.common.base.Preconditions;

/**
 * Implementation of {@link JbTree}.
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
public class JbTreeImpl<K, V> implements JbTree<K, V> {

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> treeTool;

    private final JbTreeHelper<K, V> jbTreeHelper;

    private final JbTreeData<K> treeData;

    private final JbTreeTraversingService<K, V> treeTraversingService;

    private final JbTreeService<K, V> treeService;

    /**
     * Create and initialize tree.
     * 
     * @param l
     *            requited tree L parameter.
     * @param nodeStore
     *            required node store object
     */
    public JbTreeImpl(final NodeStore<K> nodeStore, final JbTreeTool<K, V> treeTool,
	    final JbTreeHelper<K, V> jbTreeHelper, final JbTreeData<K> treeData,
	    final JbTreeTraversingService<K, V> treeTraversingService,
	    final JbTreeService<K, V> treeService) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTool = Preconditions.checkNotNull(treeTool);
	this.jbTreeHelper = Preconditions.checkNotNull(jbTreeHelper);
	this.treeData = Preconditions.checkNotNull(treeData);
	this.treeTraversingService = Preconditions.checkNotNull(treeTraversingService);
	this.treeService = Preconditions.checkNotNull(treeService);
    }

    @Override
    public V insert(final K key, final V value) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(value);
	final JbStack stack = new JbStackArrayDeque();
	final Integer currentNodeId = treeTool.findLeafNodeId(key, stack, treeData.getRootNodeId());
	Node<K, V> currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = treeTraversingService.moveRightLeafNode(currentNode, key);
	if (currentNode.getValue(key) == null) {
	    return jbTreeHelper.insertToLeafNode(currentNode, key, value, stack);
	} else {
	    /**
	     * Key already exists. Rewrite value.
	     */
	    V oldValue = currentNode.getValue(key);
	    treeService.storeValueIntoLeafNode(currentNode, key, value);
	    return oldValue;
	}
    }

    @Override
    public V remove(final K key) {
	Preconditions.checkNotNull(key);
	final JbStack stack = new JbStackArrayDeque();
	Integer currentNodeId = treeTool.findLeafNodeId(key, stack, treeData.getRootNodeId());
	Node<K, V> currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = treeTraversingService.moveRightLeafNode(currentNode, key);
	if (currentNode.getValue(key) == null) {
	    /**
	     * Node doesn't contains key, there is nothing to delete
	     */
	    nodeStore.unlockNode(currentNode.getId());
	    return null;
	} else {
	    /**
	     * Leaf node contains key so remove it.
	     */
	    final V oldValue = currentNode.remove(key);
	    nodeStore.writeNode(currentNode);
	    nodeStore.unlockNode(currentNode.getId());
	    return oldValue;
	}
    }

    @Override
    public V search(final K key) {
	Preconditions.checkNotNull(key);
	return jbTreeHelper.findAppropriateLeafNode(key).getValue(key);
    }

    @Override
    public int countValues() {
	JbTreeVisitorRecordCounter<K, V> counter = new JbTreeVisitorRecordCounter<K, V>();
	visit(counter);
	return counter.getCount();
    }

    @Override
    public boolean containsKey(final K key) {
	Preconditions.checkNotNull(key);
	return jbTreeHelper.findAppropriateLeafNode(key).getValue(key) != null;
    }

    @Override
    public String toString() {
	final StringBuilder buff = new StringBuilder();
	buff.append("Detail tree description continues: root node id: ");
	buff.append(treeData.getRootNodeId());
	buff.append("\n");
	visit(new JbTreeVisitor<K, V>() {
	    @Override
	    public boolean visitedLeaf(Node<K, V> node) {
		buff.append(node.toString());
		buff.append("\n");
		return true;
	    }

	    @Override
	    public boolean visitedNonLeaf(Node<K, Integer> node) {
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
     */
    @SuppressWarnings("unchecked")
    @Override
    public void visit(final JbTreeVisitor<K, V> treeVisitor) {
	Preconditions.checkNotNull(treeVisitor, "required JbTreeVisitor instance is null");
	final JbStack stack = new JbStackArrayDeque();
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
		    for (final Integer i : node.getNodeIds()) {
			stack.push(i);
		    }
		}
	    }
	}
    }

    @Override
    public void verify() {
	visit(new JbTreeVisitor<K, V>() {

	    @Override
	    public boolean visitedLeaf(Node<K, V> node) {
		node.verify();
		return true;
	    }

	    @Override
	    public boolean visitedNonLeaf(Node<K, Integer> node) {
		node.verify();
		return true;
	    }
	});
    }

    @Override
    public int countLockedNodes() {
	return nodeStore.countLockedNodes();
    }
}
