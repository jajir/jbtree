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
 * Implementation of {@link JbTreeService}.
 * 
 * @author jajir
 * 
 */
public class JbTreeServiceImpl<K, V> implements JbTreeService<K> {

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> tool;

    public JbTreeServiceImpl(final NodeStore<K> nodeStore, final JbTreeTool<K, V> tool) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.tool = Preconditions.checkNotNull(tool);
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
    public <S> Node<K, Integer> loadParentNode(final Node<K, S> currentNode, final K tmpKey,
	    final Integer nextNodeId) {
	Node<K, Integer> parentNode = nodeStore.getAndLock(nextNodeId);
	// TODO link to current node which key should be updated can be in
	// different node than tmpKey
	parentNode = tool.moveRightNonLeafNode(parentNode, tmpKey);
	if (parentNode.updateNodeValue(currentNode.getId(), currentNode.getMaxKey())) {
	    nodeStore.writeNode(parentNode);
	}
	return parentNode;
    }

}
