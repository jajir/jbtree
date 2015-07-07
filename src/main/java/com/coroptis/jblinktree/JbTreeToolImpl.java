package com.coroptis.jblinktree;

import java.util.Stack;

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
public class JbTreeToolImpl implements JbTreeTool {

    private final NodeStore nodeStore;

    /**
     * Default constructor.
     * 
     * @param nodeStore
     *            required node store service
     */
    public JbTreeToolImpl(final NodeStore nodeStore) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
    }

    @Override
    public Node findCorrespondingNode(final Node node, final Integer key) {
	Integer nextNodeId = node.getCorrespondingNodeId(key);
	return nodeStore.get(nextNodeId);
    }

    @Override
    public NodeImpl moveRightNonLeafNode(NodeImpl current, final Integer key) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(current);
	if (current.isLeafNode()) {
	    throw new JblinktreeException("method is for non-leaf nodes, but given node is leaf: "
		    + current.toString());
	}
	Integer nextNodeId = current.getCorrespondingNodeId(key);
	while (nextNodeId != null && nextNodeId.equals(current.getLink())) {
	    current = moveToNextNode(current, nextNodeId);

	    nextNodeId = current.getCorrespondingNodeId(key);
	}
	return current;
    }

    private boolean canMoveToNextNode(final Node node, final Integer key) {
	if (node.getLink() == null) {
	    return false;
	}
	if (node.isEmpty()) {
	    return true;
	}
	return (node.getMaxValue() != null && key > node.getMaxValue());
    }

    @Override
    public NodeImpl moveRightLeafNode(NodeImpl current, final Integer key) {
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

    private NodeImpl moveToNextNode(final Node currentNode, final Integer nextNodeId) {
	final NodeImpl n = nodeStore.getAndLock(nextNodeId);
	nodeStore.unlockNode(currentNode.getId());
	return n;
    }

    @Override
    public Node moveRightLeafNodeWithoutLocking(Node current, final Integer key) {
	if (current.isLeafNode()) {
	    while (current.getLink() != null && key > current.getMaxValue()) {
		current = nodeStore.get(current.getLink());
	    }
	    return current;
	} else {
	    throw new JblinktreeException("method is for leaf nodes, but given node is non-leaf");
	}
    }

    @Override
    public NodeImpl split(final Node currentNode, final Integer key, final Integer value) {
	NodeImpl newNode = new NodeImpl(currentNode.getL(), nodeStore.getNextId(), true);
	currentNode.moveTopHalfOfDataTo(newNode);
	if (currentNode.getMaxKey() < key) {
	    newNode.insert(key, value);
	} else {
	    currentNode.insert(key, value);
	}
	return newNode;
    }

    @Override
    public void updateMaxValueWhenNecessary(final Node currentNode, final Integer insertedKey,
	    final Stack<Integer> stack) {
	// if(currentNode.getmax)
	// TODO finish implementation
    }

    /**
     * 
     * @return new root id
     */
    @Override
    public Integer splitRootNode(final Node currentRootNode, final Node newNode) {
	// TODO consider case when new node is smaller that currentRootNode
	NodeImpl newRoot = NodeImpl.makeNode(
		currentRootNode.getL(),
		nodeStore.getNextId(),
		new Integer[] { currentRootNode.getId(), currentRootNode.getMaxKey(),
			newNode.getId(), newNode.getMaxKey(), null });
	nodeStore.writeNode(newRoot);
	return newRoot.getId();
    }

    @Override
    public void updateMaxIfNecessary(final NodeImpl parentNode, final Node childNode) {
	if (childNode.getMaxValue() > parentNode.getMaxValue()) {
	    Preconditions.checkState(parentNode.getLink() == null,
		    "parent not rightemost node in tree");
	    parentNode.setMaxKeyValue(childNode.getMaxValue());
	    nodeStore.writeNode(parentNode);
	}
    }
}
