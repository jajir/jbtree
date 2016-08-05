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

import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayList;
import com.google.common.base.Preconditions;

/**
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class JbTreeHelperImpl<K, V> implements JbTreeHelper<K, V> {

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> treeTool;

    private final JbTreeService<K, V> treeService;

    private final JbTreeData<K, V> treeData;

    JbTreeHelperImpl(final NodeStore<K> nodeStore, final JbTreeTool<K, V> treeTool,
	    final JbTreeService<K, V> treeService, final JbTreeData<K, V> treeData) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTool = Preconditions.checkNotNull(treeTool);
	this.treeService = Preconditions.checkNotNull(treeService);
	this.treeData = Preconditions.checkNotNull(treeData);
    }

    @Override
    public Node<K, V> findAppropriateLeafNode(final K key) {
	Preconditions.checkNotNull(key);
	Integer idNode = treeTool.findLeafNodeId(key, new JbStackArrayList(),
		treeData.getRootNodeId());
	Node<K, V> node = nodeStore.get(idNode);
	return treeTool.moveRightLeafNodeWithoutLocking(node, key);
    }

    // TODO following methods should be refactored

    @Override
    public V insertToLeafNode(Node<K, V> currentNode, final K key, final V value,
	    final JbStack stack) {
	if (currentNode.getKeysCount() >= treeData.getL()) {
	    final Node<K, V> newNode = storeSplitLeafNode(currentNode, key, value);
	    if (stack.isEmpty()) {
		splitRootLeafNode(currentNode, newNode);
		return null;
	    } else {
		Integer tmpValue = newNode.getId();
		K tmpKey = newNode.getMaxKey();
		final Integer previousCurrentNodeId = currentNode.getId();
		Node<K, Integer> previousNode = treeService.loadParentNode(currentNode, tmpKey,
			stack.pop());
		nodeStore.unlockNode(previousCurrentNodeId);
		return insertNonLeaf(previousNode, tmpKey, tmpValue, stack);
	    }
	} else {
	    treeService.storeValueIntoLeafNode(currentNode, key, value);
	    return null;
	}
    }

    /**
     * Write given key value pair into non-leaf node.
     * 
     * @param currentNode
     *            required leaf node
     * @param key
     *            required key
     * @param value
     *            required value
     * @param stack
     *            required stack useful for back tracing through tree
     * @return <code>null</code> when it's new key otherwise return old value
     */
    private V insertNonLeaf(Node<K, Integer> currentNode, final K key, final Integer value,
	    final JbStack stack) {
	/**
	 * Key and value have to be inserted
	 */
	Integer tmpValue = value;
	K tmpKey = key;
	while (true) {
	    if (currentNode.getKeysCount() >= treeData.getL()) {
		final Node<K, Integer> newNode = storeSplitNonLeafNode(currentNode, tmpKey,
			tmpValue);
		if (stack.isEmpty()) {
		    splitRootNonLeafNode(currentNode, newNode);
		    return null;
		} else {
		    tmpValue = newNode.getId();
		    tmpKey = newNode.getMaxKey();
		    final Integer previousCurrentNodeId = currentNode.getId();
		    currentNode = treeService.loadParentNode(currentNode, tmpKey, stack.pop());
		    nodeStore.unlockNode(previousCurrentNodeId);
		}
	    } else {
		treeService.storeValueIntoNonLeafNode(currentNode, tmpKey, tmpValue);
		return null;
	    }
	}
    }

    // FIXME pair methods move to separate typed class

    /**
     * Split node and store new and old node.
     * 
     * @param currentNode
     * @param key
     * @param value
     * @param valueTypeDescriptor
     * @return new {@link Node}
     */
    private Node<K, V> storeSplitLeafNode(final Node<K, V> currentNode, final K key,
	    final V value) {
	final Node<K, V> newNode = treeTool.splitLeafNode(currentNode, key, value);
	nodeStore.writeNode(newNode);
	nodeStore.writeNode(currentNode);
	return newNode;
    }

    /**
     * Split node and store new and old node.
     * 
     * @param currentNode
     * @param key
     * @param value
     * @param valueTypeDescriptor
     * @return new {@link Node}
     */
    private Node<K, Integer> storeSplitNonLeafNode(final Node<K, Integer> currentNode, final K key,
	    final Integer value) {
	final Node<K, Integer> newNode = treeTool.splitNonLeafNode(currentNode, key, value);
	nodeStore.writeNode(newNode);
	nodeStore.writeNode(currentNode);
	return newNode;
    }

    private Integer splitRootLeafNode(final Node<K, V> currentNode, final Node<K, V> newNode) {
	ReentrantLock lock = new ReentrantLock(false);
	lock.lock();
	try {
	    if (treeData.getRootNodeId().equals(currentNode.getId())) {
		Preconditions.checkArgument(treeData.getRootNodeId().equals(currentNode.getId()));
		treeData.setRootNodeId(treeTool.splitRootNode(currentNode, newNode));
		nodeStore.unlockNode(currentNode.getId());
	    } else {
		nodeStore.unlockNode(currentNode.getId());
	    }
	} finally {
	    lock.unlock();
	}
	return treeData.getRootNodeId();
    }

    private Integer splitRootNonLeafNode(final Node<K, Integer> currentNode,
	    final Node<K, Integer> newNode) {
	ReentrantLock lock = new ReentrantLock(false);
	lock.lock();
	try {
	    if (treeData.getRootNodeId().equals(currentNode.getId())) {
		Preconditions.checkArgument(treeData.getRootNodeId().equals(currentNode.getId()));
		treeData.setRootNodeId(treeTool.splitRootNode(currentNode, newNode));
		nodeStore.unlockNode(currentNode.getId());
	    } else {
		nodeStore.unlockNode(currentNode.getId());
	    }
	} finally {
	    lock.unlock();
	}
	return treeData.getRootNodeId();
    }

}
