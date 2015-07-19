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
public class JbTreeServiceImpl implements JbTreeService {

    private final NodeStore nodeStore;

    private final JbTreeTool tool;

    public JbTreeServiceImpl(final NodeStore nodeStore, final JbTreeTool tool) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.tool = Preconditions.checkNotNull(tool);
    }

    @Override
    public Integer findLeafNodeId(final Integer key, final Stack<Integer> stack,
	    final Integer rootNodeId) {
	Node currentNode = nodeStore.get(rootNodeId);
	while (!currentNode.isLeafNode()) {
	    Integer nextNodeId = currentNode.getCorrespondingNodeId(key);
	    if (NodeImpl.EMPTY_INT.equals(nextNodeId)) {
		/**
		 * This is rightmost node and next link is <code>null</code> so
		 * use node id associated with bigger key.
		 */
		stack.push(currentNode.getId());
		nextNodeId = currentNode.getCorrespondingNodeId(currentNode.getMaxValue());
		if (NodeImpl.EMPTY_INT.equals(nextNodeId)) {
		    throw new JblinktreeException("There is no node id for max value '"
			    + currentNode.getMaxValue() + "' in node " + currentNode.toString());
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
    public Node loadParentNode(final Node currentNode, final Integer tmpKey,
	    final Integer nextNodeId) {
	Node parentNode = nodeStore.getAndLock(nextNodeId);
	// TODO link to current node which key should be updated can be in
	// different node than tmpKey
	parentNode = tool.moveRightNonLeafNode(parentNode, tmpKey);
	if (parentNode.updateNodeValue(currentNode.getId(), currentNode.getMaxValue())) {
	    nodeStore.writeNode(parentNode);
	}
	return parentNode;
    }

}
