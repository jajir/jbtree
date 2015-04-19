package com.coroptis.jblinktree;

import java.util.Stack;

import com.google.common.base.Preconditions;

public class Tree {

    private Integer rootNodeId;

    private final NodeStore nodeStore;

    public Tree(final NodeStore nodeStore) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	Node node = new Node(0, true);
	rootNodeId = node.getId();
	this.nodeStore.put(rootNodeId, node);
    }

    public void insert(final Integer key, final Integer value) {
	final Stack<Integer> stack = new Stack<Integer>();
	Node node = nodeStore.get(rootNodeId);
	if (node.isLeafNode()) {
	    // leaf node inserting
	    if (node.getKeysCount() == Node.L) {
		// leaf node have to be split
		Node node2 = new Node(nodeStore.size(), true);
		nodeStore.put(node2.getId(), node2);
		node.moveTopHalfOfDataTo(node2);
		if (stack.isEmpty()) {
		    /**
		     * It's root node and it's also leaf node.
		     */
		    Node newRoot = new Node(nodeStore.size(), false);
		    newRoot.insert(node.getMaxKey(), node2.getId());
		    newRoot.setP0(node.getId());
		    newRoot.setMaxKeyValue(node2.getMaxKey());
		    nodeStore.put(newRoot.getId(), newRoot);
		    rootNodeId = newRoot.getId();
		    insert(key, value);
		    return;
		} else {
		    Integer previousNodeId = stack.pop();
		}
	    } else {
		node.insert(key, value);
	    }
	} else {
	    // inserting into non-leaf node
	    final Integer nextNodeId  = node.getCorrespondingNodeId(key);
	    stack.push(nextNodeId);
	}
    }

    private void insert(final Integer key, final Integer value, final Node node) {
	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
	if (node.isLeafNode()) {
	    if (node.getKeysCount() == Node.L) {
		// leaf node have to be split
		Node node2 = new Node(nodeStore.size(), true);
		node.moveTopHalfOfDataTo(node2);
	    } else {
		node.insert(key, value);
	    }
	} else {

	}
    }

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

    @Override
    public String toString() {
	StringBuilder buff = new StringBuilder();
	buff.append("Detail tree description continues: \n");

	final Stack<Integer> stack = new Stack<Integer>();
	stack.push(rootNodeId);
	while (!stack.isEmpty()) {
	    final Node node = nodeStore.get(stack.pop());
	    buff.append(node.toString());
	    buff.append("\n");
	    if (!node.isLeafNode()) {
		for (final Integer i : node.getNodeIds()) {
		    stack.push(i);
		}
	    }
	}

	return buff.toString();
    }

}
