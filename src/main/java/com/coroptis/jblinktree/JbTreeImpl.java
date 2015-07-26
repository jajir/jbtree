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

import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.type.TypeDescriptor;
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

    /**
     * Main node parameter, it's number of nodes.
     */
    private final int l;

    private Integer rootNodeId;

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> treeTool;

    private final JbTreeService<K> treeService;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    /**
     * Create and initialize tree.
     * 
     * @param l
     *            requited tree L parameter.
     * @param nodeStore
     *            required node store object
     */
    public JbTreeImpl(final int l, final NodeStore<K> nodeStore, final JbTreeTool<K, V> treeTool,
	    final JbTreeService<K> jbTreeService, final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.l = l;
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTool = Preconditions.checkNotNull(treeTool);
	this.treeService = Preconditions.checkNotNull(jbTreeService);
	this.valueTypeDescriptor = Preconditions.checkNotNull(valueTypeDescriptor,
		"value TypeDescriptor is null, use .setValueType in builder");
	this.linkTypeDescriptor = Preconditions.checkNotNull(linkTypeDescriptor,
		"link TypeDescriptor is null");
	rootNodeId = treeTool.createRootNode();
    }

    @Override
    public V insert(final K key, final V value) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(value);
	final Stack<Integer> stack = new Stack<Integer>();
	Integer currentNodeId = treeService.findLeafNodeId(key, stack, rootNodeId);
	Node<K, V> currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = treeTool.moveRightLeafNode(currentNode, key);
	if (currentNode.getValue(key) == null) {
	    return insertToLeafNode(currentNode, key, value, stack);
	} else {
	    /**
	     * Key already exists. Rewrite value.
	     */
	    V oldValue = currentNode.getValue(key);
	    storeValueIntoLeafNode(currentNode, key, value);
	    return oldValue;
	}
    }

    private V insertToLeafNode(Node<K, V> currentNode, final K key, final V value,
	    final Stack<Integer> stack) {
	if (currentNode.getKeysCount() >= l) {
	    /**
	     * There is no free space for key and value
	     */
	    final Node<K, V> newNode = treeTool.split(currentNode, key, value, valueTypeDescriptor);
	    nodeStore.writeNode(newNode);
	    nodeStore.writeNode(currentNode);
	    if (stack.empty()) {
		/**
		 * There is no previous node, it's root node.
		 */
		splitRootNode(currentNode, newNode);
		return null;
	    } else {
		/**
		 * There is a previous node, so move there.
		 */
		Integer tmpValue = newNode.getId();
		K tmpKey = newNode.getMaxKey();
		final Integer previousCurrentNodeId = currentNode.getId();
		Node<K, Integer> previousNode = treeService.loadParentNode(currentNode, tmpKey,
			stack.pop());
		nodeStore.unlockNode(previousCurrentNodeId);
		return insert(previousNode, tmpKey, tmpValue, stack);
	    }
	} else {
	    /**
	     * There is a free space for new key and value.
	     */
	    storeValueIntoLeafNode(currentNode, key, value);
	    return null;
	}
    }

    private V insert(Node<K, Integer> currentNode, final K key, final Integer value,
	    final Stack<Integer> stack) {
	/**
	 * Key and value have to be inserted
	 */
	Integer tmpValue = value;
	K tmpKey = key;
	while (true) {
	    if (currentNode.getKeysCount() >= l) {
		/**
		 * There is no free space for key and value
		 */
		final Node<K, Integer> newNode = treeTool.split(currentNode, tmpKey, tmpValue,
			linkTypeDescriptor);
		nodeStore.writeNode(newNode);
		nodeStore.writeNode(currentNode);
		if (stack.empty()) {
		    /**
		     * There is no previous node, it's root node.
		     */
		    splitRootNode(currentNode, newNode);
		    return null;
		} else {
		    /**
		     * There is a previous node, so move there.
		     */
		    tmpValue = newNode.getId();
		    tmpKey = newNode.getMaxKey();
		    final Integer previousCurrentNodeId = currentNode.getId();
		    currentNode = treeService.loadParentNode(currentNode, tmpKey, stack.pop());
		    nodeStore.unlockNode(previousCurrentNodeId);
		}
	    } else {
		/**
		 * There is a free space for new key and value.
		 */
		storeValueIntoNonLeafNode(currentNode, tmpKey, tmpValue);
		return null;
	    }
	}
    }

    private <S> void splitRootNode(Node<K, S> currentNode, Node<K, S> newNode) {
	ReentrantLock lock = new ReentrantLock(false);
	lock.lock();
	try {
	    if (rootNodeId.equals(currentNode.getId())) {
		Preconditions.checkArgument(rootNodeId.equals(currentNode.getId()));
		rootNodeId = treeTool.splitRootNode(currentNode, newNode);
		nodeStore.unlockNode(currentNode.getId());
	    } else {
		nodeStore.unlockNode(currentNode.getId());
	    }
	} finally {
	    lock.unlock();
	}

    }

    private void storeValueIntoLeafNode(final Node<K, V> currentNode, final K key, final V value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }

    private void storeValueIntoNonLeafNode(final Node<K, Integer> currentNode, final K key,
	    final Integer value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }

    @Override
    public V remove(final K key) {
	Preconditions.checkNotNull(key);
	final Stack<Integer> stack = new Stack<Integer>();
	Integer currentNodeId = treeService.findLeafNodeId(key, stack, rootNodeId);
	Node<K, V> currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = treeTool.moveRightLeafNode(currentNode, key);
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
	return findAppropriateLeafNode(key).getValue(key);
    }

    private Node<K, V> findAppropriateLeafNode(final K key) {
	Preconditions.checkNotNull(key);
	Integer idNode = treeService.findLeafNodeId(key, new Stack<Integer>(), rootNodeId);
	Node<K, V> node = nodeStore.get(idNode);
	return treeTool.moveRightLeafNodeWithoutLocking(node, key);
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
	return findAppropriateLeafNode(key).getValue(key) != null;
    }

    @Override
    public String toString() {
	final StringBuilder buff = new StringBuilder();
	buff.append("Detail tree description continues: root node id: ");
	buff.append(rootNodeId);
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
	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
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
