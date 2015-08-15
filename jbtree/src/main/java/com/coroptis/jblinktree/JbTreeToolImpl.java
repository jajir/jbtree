package com.coroptis.jblinktree;

import java.util.Stack;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
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

    private final NodeStore<K> nodeStore;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final NodeBuilder<K, V> nodeBuilder;

    /**
     * Default constructor.
     * 
     * @param nodeStore
     *            required node store service
     */
    public JbTreeToolImpl(final NodeStore<K> nodeStore, final TypeDescriptor<K> keyTypeDescriptor,
	    final NodeBuilder<K, V> nodeBuilder) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.keyTypeDescriptor = Preconditions.checkNotNull(keyTypeDescriptor);
	this.nodeBuilder = nodeBuilder;
    }

    @Override
    public boolean canMoveToNextNode(final Node<K, ?> node, final K key) {
	if (NodeImpl.EMPTY_INT.equals(node.getLink())) {
	    return false;
	}
	if (node.isEmpty()) {
	    return true;
	}
	return (node.getMaxKey() != null && keyTypeDescriptor.compare(key, node.getMaxKey()) > 0);
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
    public Integer findLeafNodeId(final K key, final Stack<Integer> stack, final Integer rootNodeId) {
	Node<K, Integer> currentNode = nodeStore.get(rootNodeId);
	while (!currentNode.isLeafNode()) {
	    Integer nextNodeId = currentNode.getCorrespondingNodeId(key);
	    if (NodeImpl.EMPTY_INT.equals(nextNodeId)) {
		/**
		 * This is rightmost node and next link is <code>null</code> so
		 * use node id associated with bigger key.
		 */
		stack.push(currentNode.getId());
		nextNodeId = currentNode.getCorrespondingNodeId(currentNode.getMaxKey());
		if (NodeImpl.EMPTY_INT.equals(nextNodeId)) {
		    throw new JblinktreeException("There is no node id for max value '"
			    + currentNode.getMaxKey() + "' in node " + currentNode.toString());
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
    public <S> Node<K, S> split(final Node<K, S> currentNode, final K key, final S value,
	    TypeDescriptor<S> valueTypeDescriptor) {
	// FIXME create leaf and non leaf versions of this method and call
	// proper node builder
	Node<K, S> newNode = new NodeImpl<K, S>(currentNode.getL(), nodeStore.getNextId(), true,
		keyTypeDescriptor, valueTypeDescriptor, new TypeDescriptorInteger());
	currentNode.moveTopHalfOfDataTo(newNode);
	if (keyTypeDescriptor.compare(currentNode.getMaxKey(), key) < 0) {
	    newNode.insert(key, value);
	} else {
	    currentNode.insert(key, value);
	}
	return newNode;
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
    public Integer createRootNode() {
	Node<K, V> node = nodeBuilder.makeEmptyLeafNode(nodeStore.getNextId());
	this.nodeStore.writeNode(node);
	return node.getId();
    }

    @Override
    public <S> Node<K, S> moveToNextNode(final Node<K, ?> currentNode, final Integer nextNodeId) {
	final Node<K, S> n = nodeStore.getAndLock(nextNodeId);
	nodeStore.unlockNode(currentNode.getId());
	return n;
    }
}
