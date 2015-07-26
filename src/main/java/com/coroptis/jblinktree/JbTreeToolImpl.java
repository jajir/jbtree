package com.coroptis.jblinktree;

import java.util.Stack;

import com.coroptis.jblinktree.type.TypeDescriptor;
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
 * Implementation of {@link JbTreeTool}.
 * 
 * @author jajir
 * 
 */
public class JbTreeToolImpl<K, V> implements JbTreeTool<K, V> {

    private final NodeStore<K, V> nodeStore;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final NodeBuilder<K, V> nodeBuilder;

    /**
     * Default constructor.
     * 
     * @param nodeStore
     *            required node store service
     */
    public JbTreeToolImpl(final NodeStore<K, V> nodeStore,
	    final TypeDescriptor<K> keyTypeDescriptor, final NodeBuilder<K, V> nodeBuilder) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.keyTypeDescriptor = Preconditions.checkNotNull(keyTypeDescriptor);
	this.nodeBuilder = nodeBuilder;
    }

    @Override
    public Node<K, Integer> moveRightNonLeafNode(Node<K, Integer> current, final K key) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(current);
	if (current.isLeafNode()) {
	    throw new JblinktreeException("method is for non-leaf nodes, but given node is leaf: "
		    + current.toString());
	}
	Integer nextNodeId = current.getCorrespondingNodeId(key);
	while (!NodeImpl.EMPTY_INT.equals(nextNodeId) && nextNodeId.equals(current.getLink())) {
	    current = moveToNextNode(current, nextNodeId);
	    nextNodeId = current.getCorrespondingNodeId(key);
	}
	return current;
    }

    private boolean canMoveToNextNode(final Node<K, ?> node, final K key) {
	if (NodeImpl.EMPTY_INT.equals(node.getLink())) {
	    return false;
	}
	if (node.isEmpty()) {
	    return true;
	}
	return (node.getMaxKey() != null && keyTypeDescriptor.compare(key, node.getMaxKey()) > 0);
    }

    @Override
    public Node<K, V> moveRightLeafNode(Node<K, V> current, final K key) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(current);
	if (!current.isLeafNode()) {
	    throw new JblinktreeException("method is for leaf nodes, but given node is non-leaf");
	}
	while (canMoveToNextNode(current, key)) {
	    current = moveToNextNode(current, current.getLink());
	}
	return current;
    }

    private <S> Node<K, S> moveToNextNode(final Node<K, ?> currentNode, final Integer nextNodeId) {
	final Node<K, S> n = nodeStore.getAndLock(nextNodeId);
	nodeStore.unlockNode(currentNode.getId());
	return n;
    }

    @Override
    public Node<K, V> moveRightLeafNodeWithoutLocking(Node<K, V> current, final K key) {
	if (current.isLeafNode()) {
	    while (current.getLink() != null
		    && keyTypeDescriptor.compare(key, current.getMaxKey()) > 0) {
		current = nodeStore.get(current.getLink());
	    }
	    return current;
	} else {
	    throw new JblinktreeException("method is for leaf nodes, but given node is non-leaf");
	}
    }

    @Override
    public <S> Node<K, S> split(final Node<K, S> currentNode, final K key, final S value,
	    TypeDescriptor<S> valueTypeDescriptor) {
	Node<K, S> newNode = new NodeImpl<K, S>(currentNode.getL(), nodeStore.getNextId(), true,
		keyTypeDescriptor, valueTypeDescriptor);
	currentNode.moveTopHalfOfDataTo(newNode);
	if (keyTypeDescriptor.compare(currentNode.getMaxKey(), key) < 0) {
	    newNode.insert(key, value);
	} else {
	    currentNode.insert(key, value);
	}
	return newNode;
    }

    @Override
    public <S> void updateMaxValueWhenNecessary(final Node<K, S> currentNode, final K insertedKey,
	    final Stack<Integer> stack) {
	// if(currentNode.getmax)
	// TODO finish implementation
    }

    /**
     * 
     * @return new root id
     */
    @Override
    public <S> Integer splitRootNode(final Node<K, S> currentRootNode, final Node<K, S> newNode) {
	// TODO consider case when new node is smaller that currentRootNode
	Node<K, Integer> newRoot = nodeBuilder.makeNonLeafNode(nodeStore.getNextId(),
		currentRootNode.getId(), currentRootNode.getMaxKey(), newNode.getId(),
		newNode.getMaxKey());
	nodeStore.writeNode(newRoot);
	return newRoot.getId();
    }

    @Override
    public <S> void updateMaxIfNecessary(final Node<K, Integer> parentNode,
	    final Node<K, S> childNode) {
	if (keyTypeDescriptor.compare(childNode.getMaxKey(), parentNode.getMaxKey()) > 0) {
	    Preconditions.checkState(NodeImpl.EMPTY_INT.equals(parentNode.getLink()),
		    "parent not rightemost node in tree");
	    parentNode.setMaxKey(childNode.getMaxKey());
	    nodeStore.writeNode(parentNode);
	}
    }
}
