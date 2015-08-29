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

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayDeque;
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

    /**
     * Main node parameter, it's number of nodes.
     */
    private final int l;

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> treeTool;

    private final JbTreeService<K, V> treeService;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    private final JbTreeData<K> treeData;

    JbTreeHelperImpl(final int l, final NodeStore<K> nodeStore, final JbTreeTool<K, V> treeTool,
	    final JbTreeService<K, V> treeService, final JbTreeData<K> treeData,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.l = l;
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTool = Preconditions.checkNotNull(treeTool);
	this.treeService = Preconditions.checkNotNull(treeService);
	this.treeData = Preconditions.checkNotNull(treeData);
	this.valueTypeDescriptor = Preconditions.checkNotNull(valueTypeDescriptor,
		"value TypeDescriptor is null, use .setValueType in builder");
	this.linkTypeDescriptor = Preconditions.checkNotNull(linkTypeDescriptor,
		"link TypeDescriptor is null");
    }

    @Override
    public Node<K, V> findAppropriateLeafNode(final K key) {
	Preconditions.checkNotNull(key);
	Integer idNode = treeTool.findLeafNodeId(key, new JbStackArrayDeque(),
		treeData.getRootNodeId());
	Node<K, V> node = nodeStore.get(idNode);
	return treeTool.moveRightLeafNodeWithoutLocking(node, key);
    }

    @Override
    public V insertToLeafNode(Node<K, V> currentNode, final K key, final V value,
	    final JbStack stack) {
	if (currentNode.getKeysCount() >= l) {
	    /**
	     * There is no free space for key and value
	     */
	    final Node<K, V> newNode = treeTool.split(currentNode, key, value, valueTypeDescriptor);
	    nodeStore.writeNode(newNode);
	    nodeStore.writeNode(currentNode);
	    if (stack.isEmpty()) {
		/**
		 * There is no previous node, it's root node.
		 */
		treeData.splitRootNode(currentNode, newNode);
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
		return insertNonLeaf(previousNode, tmpKey, tmpValue, stack);
	    }
	} else {
	    /**
	     * There is a free space for new key and value.
	     */
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
	    if (currentNode.getKeysCount() >= l) {
		/**
		 * There is no free space for key and value
		 */
		final Node<K, Integer> newNode = treeTool.split(currentNode, tmpKey, tmpValue,
			linkTypeDescriptor);
		nodeStore.writeNode(newNode);
		nodeStore.writeNode(currentNode);
		if (stack.isEmpty()) {
		    /**
		     * There is no previous node, it's root node.
		     */
		    treeData.splitRootNode(currentNode, newNode);
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
		treeService.storeValueIntoNonLeafNode(currentNode, tmpKey, tmpValue);
		return null;
	    }
	}
    }

}
