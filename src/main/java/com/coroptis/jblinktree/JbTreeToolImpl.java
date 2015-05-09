package com.coroptis.jblinktree;

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
    public Node moveRightNonLeafNode(Node current, final Integer key) {
	if (current.isLeafNode()) {
	    throw new JblinktreeException("method is for non-leaf nodes, but given node is leaf");
	} else {
	    Integer nextNodeId = current.getCorrespondingNodeId(key);
	    while (nextNodeId != null && nextNodeId.equals(current.getLink())) {
		current = moveToNextNede(current, nextNodeId);

		nextNodeId = current.getCorrespondingNodeId(key);
	    }
	    return current;
	}
    }

    @Override
    public Node moveRightLeafNode(Node current, final Integer key) {
	if (current.isLeafNode()) {
	    while (current.getLink() != null && key > current.getMaxKeyValue()) {
		current = moveToNextNede(current, current.getLink());
	    }
	    return current;
	} else {
	    throw new JblinktreeException("method is for leaf nodes, but given node is non-leaf");
	}
    }

    private Node moveToNextNede(final Node currentNode, final Integer nextNodeId) {
	final Node n = nodeStore.getAndLock(nextNodeId);
	nodeStore.unlockNode(currentNode.getId());
	return n;
    }

    @Override
    public Node split(final Node currentNode, final Integer key, final Integer tmpValue) {
	Node newNode = new Node(currentNode.getL(), nodeStore.size(), true);
	currentNode.moveTopHalfOfDataTo(newNode);
	if (currentNode.getMaxKey() < key) {
	    newNode.insert(key, tmpValue);
	} else {
	    currentNode.insert(key, tmpValue);
	}
	return newNode;
    }
}