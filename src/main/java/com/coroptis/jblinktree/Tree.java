package com.coroptis.jblinktree;

import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class Tree {

    /**
     * Main node parameter, it's number of nodes.
     */
    private final int l;

    private Integer rootNodeId;

    private final NodeStore nodeStore;

    private final Logger logger = LoggerFactory.getLogger(Tree.class);

    public Tree(final int l, final NodeStore nodeStore) {
	this.l = l;
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	Node node = new Node(l, 0, true);
	rootNodeId = node.getId();
	this.nodeStore.put(rootNodeId, node);
    }

    /**
     * Insert method according to Lehman & Yao
     * 
     * @param key
     *            required parameter key
     * @return previously associated value with given key.
     */
    public Integer insert(final Integer key, final Integer value) {
	final Stack<Integer> stack = new Stack<Integer>();
	Node currentNode = nodeStore.get(rootNodeId);
	while (!currentNode.isLeafNode()) {
	    final Node previousNode = currentNode;
	    currentNode = findCorrespondingNode(currentNode, key);
	    if (!currentNode.getId().equals(previousNode.getLink())) {
		/**
		 * I don't want to store nodes when cursor is moved right.
		 */
		stack.push(previousNode.getId());
	    }
	}

	/**
	 * In node is leaf where should be new key & value inserted.
	 */
	// just node id should be passed here, because node object could be
	// completly different in store
	nodeStore.lockNode(currentNode.getId());
	currentNode = moveRight(currentNode, key);
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
		    Node newNode = split(currentNode, tmpKey, tmpValue);
		    Integer currentNodeMaxKey = currentNode.getMaxKey();
		    nodeStore.writeNode(newNode);
		    nodeStore.writeNode(currentNode);
		    tmpValue = newNode.getId();
		    tmpKey = currentNode.getMaxKeyValue();
		    Node oldNode = currentNode;
		    if (stack.empty()) {
			/**
			 * It's root node.
			 */
			nodeStore.unlockNode(oldNode.getId());
			Node newRoot = new Node(l, nodeStore.size(), false);
			newRoot.insert(currentNode.getMaxKey(), newNode.getId());
			newRoot.setP0(currentNode.getId());
			newRoot.setMaxKeyValue(newNode.getMaxKey());
			nodeStore.put(newRoot.getId(), newRoot);
			rootNodeId = newRoot.getId();
			return null;
		    } else {
			/**
			 * There is a previous node, so move there.
			 */
			currentNode = nodeStore.get(stack.pop());
			nodeStore.lockNode(currentNode.getId());
			moveRight(currentNode, currentNodeMaxKey);
			if (newNode.getMaxKeyValue() > currentNode.getMaxKeyValue()) {
			    currentNode.setMaxKeyValue(newNode.getMaxKeyValue());
			    nodeStore.writeNode(currentNode);
			}
			nodeStore.unlockNode(oldNode.getId());
		    }
		} else {
		    /**
		     * There is free space for key and value
		     */
		    currentNode.insert(tmpKey, tmpValue);
		    nodeStore.writeNode(currentNode);
		    nodeStore.unlockNode(currentNode.getId());
		    return null;
		}
	    }
	} else {
	    /**
	     * Key already exists. Rewrite value.
	     */
	    Integer oldValue = currentNode.getValue(key);
	    currentNode.insert(key, value);
	    nodeStore.writeNode(currentNode);
	    nodeStore.unlockNode(currentNode.getId());
	    return oldValue;
	}
    }

    private Node split(final Node currentNode, final Integer key, final Integer tmpValue) {
	Node newNode = new Node(l, nodeStore.size(), true);
	currentNode.moveTopHalfOfDataTo(newNode);
	if (currentNode.getMaxKey() < key) {
	    newNode.insert(key, tmpValue);
	} else {
	    currentNode.insert(key, tmpValue);
	}
	return newNode;
    }

    /**
     * Move right method according to Lehman & Yao.
     * <p>
     * When there is move right than current node is unlocked and new one is
     * locked.
     * </p>
     * 
     * @param current
     *            required current node, this node should be locked
     * @param key
     *            required key
     * @return moved right node
     */
    private Node moveRight(Node current, final Integer key) {
	Node n;
	if (current.isLeafNode()) {
	    while (current.getLink() != null && key > current.getLink()) {
		n = nodeStore.get(current.getLink());
		nodeStore.lockNode(n.getId());
		nodeStore.unlockNode(current.getId());
		current = n;
	    }
	    return current;
	} else {
	    while ((n = findCorrespondingNode(current, key)).getId().equals(current.getLink())) {
		nodeStore.lockNode(n.getId());
		nodeStore.unlockNode(current.getId());
		current = n;
	    }
	    return current;
	}
    }

    private Node findCorrespondingNode(final Node node, final Integer key) {
	Integer nextNodeId = node.getCorrespondingNodeId(key);
	return nodeStore.get(nextNodeId);
    }

    /**
     * Search method according to Lehman & Yao
     * 
     * @param key
     * @return
     */
    public Integer search(final Integer key) {
	Integer idNode = rootNodeId;
	Node node = nodeStore.get(rootNodeId);
	while (!node.isLeafNode()) {
	    idNode = node.getCorrespondingNodeId(key);
	    node = nodeStore.get(idNode);
	}

	while (node.getLink().equals(node.getCorrespondingNodeId(key))) {
	    idNode = node.getLink();
	    node = nodeStore.get(idNode);
	}

	return node.getValue(key);
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

}
