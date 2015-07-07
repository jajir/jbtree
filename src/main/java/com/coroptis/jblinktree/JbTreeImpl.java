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

    private final NodeStore nodeStore;

    private final JbTreeTool tool;

    private final JbTreeService treeService;

    private final TypeDescriptor keyTypeDescriptor;

    private final TypeDescriptor valueTypeDescriptor;

    /**
     * Create and initialize tree.
     * 
     * @param l
     *            requited tree L parameter.
     * @param nodeStore
     *            required node store object
     */
    public JbTreeImpl(final int l, final NodeStore nodeStore, final JbTreeTool tool,
	    final JbTreeService jbTreeService, final TypeDescriptor keyTypeDescriptor,
	    final TypeDescriptor valueTypeDescriptor) {
	this.l = l;
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.tool = Preconditions.checkNotNull(tool);
	this.treeService = Preconditions.checkNotNull(jbTreeService);
	this.keyTypeDescriptor = Preconditions.checkNotNull(keyTypeDescriptor,
		"key TypeDescriptor is null, use .setKeyType in builder");
	this.valueTypeDescriptor = Preconditions.checkNotNull(valueTypeDescriptor,
		"value TypeDescriptor is null, use .setValueType in builder");
	NodeImpl node = new NodeImpl(l, nodeStore.getNextId(), true);
	rootNodeId = node.getId();
	this.nodeStore.writeNode(node);
    }

    @Override
    public Integer insert(final K key, final V value) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(value);
	final Stack<Integer> stack = new Stack<Integer>();
	Integer currentNodeId = treeService.findLeafNodeId((Integer) key, stack, rootNodeId);
	NodeImpl currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = tool.moveRightLeafNode(currentNode, (Integer) key);
	if (currentNode.getValue((Integer) key) == null) {
	    /**
	     * Key and value have to be inserted
	     */
	    Integer tmpValue = (Integer) value;
	    Integer tmpKey = (Integer) key;
	    while (true) {
		if (currentNode.getKeysCount() >= l) {
		    /**
		     * There is no free space for key and value
		     */
		    final NodeImpl newNode = tool.split(currentNode, tmpKey, tmpValue);
		    nodeStore.writeNode(newNode);
		    nodeStore.writeNode(currentNode);
		    if (stack.empty()) {
			/**
			 * There is no previous node, it's root node.
			 */
			ReentrantLock lock = new ReentrantLock(false);
			lock.lock();
			try {
			    if (rootNodeId.equals(currentNode.getId())) {
				Preconditions.checkArgument(rootNodeId.equals(currentNode.getId()));
				rootNodeId = tool.splitRootNode(currentNode, newNode);
				nodeStore.unlockNode(currentNode.getId());
			    } else {
				nodeStore.unlockNode(currentNode.getId());
			    }
			} finally {
			    lock.unlock();
			}
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
	    Integer oldValue = currentNode.getValue((Integer) key);
	    storeValueIntoNode(currentNode, (Integer) key, (Integer) value);
	    return oldValue;
	}
    }

    private void storeValueIntoNode(final NodeImpl currentNode, final Integer key,
	    final Integer value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }

    @Override
    public boolean remove(final K key) {
	Preconditions.checkNotNull(key);
	final Stack<Integer> stack = new Stack<Integer>();
	Integer currentNodeId = treeService.findLeafNodeId((Integer) key, stack, rootNodeId);
	NodeImpl currentNode = nodeStore.getAndLock(currentNodeId);
	currentNode = tool.moveRightLeafNode(currentNode, (Integer) key);
	if (currentNode.getValue((Integer) key) == null) {
	    /**
	     * Node doesn't contains key, there is nothing to delete
	     */
	    nodeStore.unlockNode(currentNode.getId());
	    return false;
	} else {
	    /**
	     * Leaf node contains key so remove it.
	     */
	    Integer tmpKey = (Integer) key;
	    currentNode.remove(tmpKey);
	    nodeStore.writeNode(currentNode);
	    nodeStore.unlockNode(currentNode.getId());
	    return true;
	}
    }

    @Override
    public V search(final K key) {
	Preconditions.checkNotNull(key);
	return (V) findAppropriateNode((Integer) key).getValue((Integer) key);
    }

    private Node findAppropriateNode(final Integer key) {
	Preconditions.checkNotNull(key);
	Integer idNode = treeService.findLeafNodeId(key, new Stack<Integer>(), rootNodeId);
	Node node = nodeStore.get(idNode);
	return tool.moveRightLeafNodeWithoutLocking(node, key);
    }

    @Override
    public int countValues() {
	JbTreeVisitorRecordCounter counter = new JbTreeVisitorRecordCounter();
	visit(counter);
	return counter.getCount();
    }

    @Override
    public boolean containsKey(final K key) {
	Preconditions.checkNotNull(key);
	return findAppropriateNode((Integer) key).getValue((Integer) key) != null;
    }

    @Override
    public String toString() {
	final StringBuilder buff = new StringBuilder();
	buff.append("Detail tree description continues: root node id: ");
	buff.append(rootNodeId);
	buff.append("\n");
	visit(new JbTreeVisitor() {
	    @Override
	    public boolean visited(Node node) {
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
    @Override
    public void visit(final JbTreeVisitor treeVisitor) {
	Preconditions.checkNotNull(treeVisitor, "required JbTreeVisitor instance is null");
	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
	while (!stack.isEmpty()) {
	    final Integer nodeId = stack.pop();
	    if (nodeId == null) {
		throw new JblinktreeException("some node id was null");
	    } else {
		final Node node = nodeStore.get(nodeId);
		if (!treeVisitor.visited(node)) {
		    return;
		}
		if (!node.isLeafNode()) {
		    for (final Integer i : node.getNodeIds()) {
			stack.push(i);
		    }
		}
	    }
	}
    }

    @Override
    public void verify() {
	visit(new JbTreeVisitor() {

	    @Override
	    public boolean visited(Node node) {
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
