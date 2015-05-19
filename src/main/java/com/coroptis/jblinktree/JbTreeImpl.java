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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;

/**
 * Implementation of {@link JbTree}.
 * 
 * @author jajir
 * 
 */
public class JbTreeImpl implements JbTree {

    /**
     * Main node parameter, it's number of nodes.
     */
    private final int l;

    private Integer rootNodeId;

    private final NodeStore nodeStore;

    private final JbTreeTool tool;

    private final JbTreeService treeService;

    private final Logger logger = LoggerFactory.getLogger(JbTreeImpl.class);

    /**
     * Create and initialize tree.
     * 
     * @param l
     *            requited tree L parameter.
     * @param nodeStore
     *            required node store object
     */
    public JbTreeImpl(final int l, final NodeStore nodeStore, final JbTreeTool tool,
	    final JbTreeService jbTreeService) {
	this.l = l;
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.tool = Preconditions.checkNotNull(tool);
	this.treeService = Preconditions.checkNotNull(jbTreeService);
	Node node = new Node(l, 0, true);
	rootNodeId = node.getId();
	this.nodeStore.writeNode(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.JbTree#insert(java.lang.Integer,
     * java.lang.Integer)
     */
    @Override
    public Integer insert(final Integer key, final Integer value) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(value);
	final Stack<Integer> stack = new Stack<Integer>();
	Integer currentNodeId = treeService.findLeafNodeId(key, stack, rootNodeId);
	Node currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = tool.moveRightLeafNode(currentNode, key);
	if (currentNode.getValue(key) == null) {
	    /**
	     * Key and value have to be inserted
	     */
	    Integer tmpValue = value;
	    Integer tmpKey = key;
	    while (true) {
		if (currentNode.getKeysCount() >= l) {
		    /**
		     * There is no free space for key and value
		     */
		    final Node newNode = tool.split(currentNode, tmpKey, tmpValue);
		    nodeStore.writeNode(newNode);
		    nodeStore.writeNode(currentNode);
		    if (stack.empty()) {
			/**
			 * There is no previous node, it's root node.
			 */
			rootNodeId = tool.splitRootNode(currentNode, newNode);
			nodeStore.unlockNode(currentNode.getId());
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
		    storeValueIntoNode(currentNode, tmpKey, tmpValue);
		    return null;
		}
	    }
	} else {
	    /**
	     * Key already exists. Rewrite value.
	     */
	    Integer oldValue = currentNode.getValue(key);
	    storeValueIntoNode(currentNode, key, value);
	    return oldValue;
	}
    }

    private void storeValueIntoNode(final Node currentNode, final Integer key, final Integer value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.JbTree#remove(java.lang.Integer)
     */
    @Override
    public boolean remove(final Integer key) {
	Preconditions.checkNotNull(key);
	final Stack<Integer> stack = new Stack<Integer>();
	Integer currentNodeId = treeService.findLeafNodeId(key, stack, rootNodeId);
	Node currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = tool.moveRightLeafNode(currentNode, key);
	if (currentNode.getValue(key) == null) {
	    /**
	     * Node doesn't contains key, there is nothing to delete
	     */
	    return false;
	} else {
	    List<Integer> nodesToRemove = new ArrayList<Integer>();
	    /**
	     * Leaf node contains key so remove it.
	     */
	    Integer tmpKey = key;
	    while (true) {
		Integer oldMaxKey = currentNode.getMaxKey();
		currentNode.remove(tmpKey);
		if (currentNode.getKeysCount() == 0) {
		    /**
		     * It's empty node, so remove it.
		     */
		    if (rootNodeId.equals(currentNode.getId())) {
			removeNodes(nodesToRemove);
			nodeStore.unlockNode(currentNode.getId());
			return true;
		    }
		    /**
		     * Node to remove should be locked, if it's not than another
		     * insert process could insert some value into it.
		     */
		    nodesToRemove.add(currentNode.getId());
		    Node toDelete = currentNode;
		    currentNode = nodeStore.getAndLock(stack.pop());
		    currentNode = tool.moveRightNonLeafNode(currentNode, tmpKey);
		    updatePreviousNodeLink(toDelete, currentNode, tmpKey);
		    tmpKey = oldMaxKey;
		} else {
		    /**
		     * There are more than 1 key in node, so it's safe to remove
		     * key.
		     */
		    if (!currentNode.getMaxKey().equals(oldMaxKey)) {
			updateMaxKey(currentNode, stack, tmpKey);
			removeNodes(nodesToRemove);
			return true;
		    }
		    nodeStore.unlockNode(currentNode.getId());
		    removeNodes(nodesToRemove);
		    return true;
		}
	    }// end of while
	}
    }

    private void updateMaxKey(Node currentNode, final Stack<Integer> stack, Integer tmpKey) {
	/**
	 * Max value was changed in current node. So max value have to be
	 * updated in upper nodes.
	 */
	while (true) {
	    if (stack.empty()) {
		nodeStore.unlockNode(currentNode.getId());
		return;
	    }
	    Node nextNode = nodeStore.getAndLock(stack.pop());
	    nextNode = tool.moveRightNonLeafNode(nextNode, tmpKey);
	    Integer oldMaxKey = nextNode.getMaxKey();
	    nextNode.updateNodeValue(currentNode.getId(), currentNode.getMaxValue());
	    nodeStore.writeNode(nextNode);
	    nodeStore.unlockNode(currentNode.getId());
	    currentNode = nextNode;
	    if (currentNode.getMaxKey().equals(oldMaxKey)) {
		nodeStore.unlockNode(currentNode.getId());
		return;
	    }
	}
    }

    private void updatePreviousNodeLink(Node nodeToRemove, Node parentNode, Integer removedKey) {
	// FIXME following code will cause dead locks, locks should go from left
	// to right
	Integer previousNodeId = parentNode.getPreviousCorrespondingNode(removedKey);
	if (previousNodeId != null) {
	    Node previousNode = nodeStore.getAndLock(previousNodeId);
	    Preconditions.checkArgument(previousNode.getLink().equals(nodeToRemove.getId()),
		    "node %s should have link value %s instead if %s", previousNodeId,
		    previousNode.getLink(), nodeToRemove.getId());
	    // previousNode.remove(nodeToRemove.getId());
	    previousNode.setLink(nodeToRemove.getLink());
	    nodeStore.writeNode(previousNode);
	    nodeStore.unlockNode(previousNodeId);
	}
    }

    private void removeNodes(final List<Integer> nodesToRemove) {
	for (final Integer i : nodesToRemove) {
	    nodeStore.unlockNode(i);
	    nodeStore.deleteNode(i);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.JbTree#search(java.lang.Integer)
     */
    @Override
    public Integer search(final Integer key) {
	Preconditions.checkNotNull(key);
	Integer idNode = treeService.findLeafNodeId(key, new Stack<Integer>(), rootNodeId);
	Node node = nodeStore.get(idNode);
	node = tool.moveRightLeafNodeWithoutLocking(node, key);
	return node.getValue(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.JbTree#countValues()
     */
    @Override
    public int countValues() {
	int out = 0;
	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
	while (!stack.isEmpty()) {
	    final Node node = nodeStore.get(stack.pop());
	    if (node.isLeafNode()) {
		out += node.getKeysCount();
	    } else {
		for (final Integer i : node.getNodeIds()) {
		    stack.push(i);
		}
	    }
	}
	return out;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.JbTree#containsKey(java.lang.Integer)
     */
    @Override
    public boolean containsKey(final Integer key) {
	Preconditions.checkNotNull(key);
	Integer idNode = rootNodeId;
	Node node = nodeStore.get(rootNodeId);
	// TODO reuse code from search
	while (!node.isLeafNode()) {
	    idNode = node.getCorrespondingNodeId(key);
	    node = nodeStore.get(idNode);
	}

	while (node.getLink().equals(node.getCorrespondingNodeId(key))) {
	    idNode = node.getLink();
	    node = nodeStore.get(idNode);
	}

	return node.getValue(key) != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.JbTree#toString()
     */
    @Override
    public String toString() {
	StringBuilder buff = new StringBuilder();
	buff.append("Detail tree description continues: root node id: ");
	buff.append(rootNodeId);
	buff.append("\n");

	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
	while (!stack.isEmpty()) {
	    final Integer nodeId = stack.pop();
	    if (nodeId == null) {
		buff.append("\nprevious node id is null");
		return buff.toString();
	    } else {
		final Node node = nodeStore.get(nodeId);
		node.verify();
		buff.append(node.toString());
		buff.append("\n");
		if (!node.isLeafNode()) {
		    for (final Integer i : node.getNodeIds()) {
			stack.push(i);
		    }
		}
	    }
	}

	return buff.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.JbTree#verify()
     */
    @Override
    public void verify() {
	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
	while (!stack.isEmpty()) {
	    final Integer nodeId = stack.pop();
	    if (nodeId == null) {
		logger.error("some node id was null");
	    } else {
		final Node node = nodeStore.get(nodeId);
		node.verify();
		if (!node.isLeafNode()) {
		    for (final Integer i : node.getNodeIds()) {
			stack.push(i);
		    }
		}
	    }
	}

    }

    private final String intendation = "    ";

    @Override
    public String toDotFile(final File file) {
	final StringBuilder buff = new StringBuilder();

	buff.append("digraph graphname {\n");
	buff.append(intendation);
	buff.append("edge [label=0];\n");
	buff.append(intendation);
	buff.append("graph [ranksep=1];\n");
	buff.append(intendation);
	buff.append("node [shape=record]\n");

	for (final Integer i : nodeStore.getKeys()) {
	    nodeStore.get(i).writeTo(buff, intendation);
	}

	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
	while (!stack.isEmpty()) {
	    final Integer nodeId = stack.pop();
	    if (nodeId == null) {
		return buff.toString();
	    } else {
		final Node node = nodeStore.get(nodeId);
		if (node.getLink() != null) {
		    // buff.append(intendation);
		    // buff.append("\"node");
		    // buff.append(node.getId());
		    // buff.append("\"");
		    // buff.append(" -> ");
		    // buff.append("\"node");
		    // buff.append(node.getLink());
		    // buff.append("\"");
		    // buff.append("\n");
		}
		if (!node.isLeafNode()) {
		    for (final Integer i : node.getNodeIds()) {
			buff.append(intendation);
			buff.append("\"node");
			buff.append(node.getId());
			buff.append("\":F");
			buff.append(i);
			buff.append(" -> ");
			buff.append("\"node");
			buff.append(i);
			buff.append("\" [label=\"");
			buff.append(i);
			buff.append("\"];");
			buff.append("\n");
		    }

		    for (final Integer i : node.getNodeIds()) {
			stack.push(i);
		    }
		}
	    }
	}

	buff.append("");
	buff.append("}");

	try {
	    Files.write(buff, file, Charsets.ISO_8859_1);
	} catch (IOException e) {
	    logger.error(e.getMessage(), e);
	    throw new JblinktreeException(e.getMessage());
	}
	return buff.toString();
    }

    @Override
    public int countLockedNodes() {
	return nodeStore.countLockedNodes();
    }
}
