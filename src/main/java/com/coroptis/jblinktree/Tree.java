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

    public void insertOld(final Integer key, final Integer value) {
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
		    insertOld(key, value);
		    return;
		} else {
		    Integer previousNodeId = stack.pop();
		}
	    } else {
		node.insert(key, value);
	    }
	} else {
	    // inserting into non-leaf node
	    final Integer nextNodeId = node.getCorrespondingNodeId(key);
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

    /**
     * Insert method according to Lehman & Yao
     * 
     * @param key
     * @return
     */
    public void insert(final Integer key, final Integer value) {
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
	 * In node is leaf where should be new kye & value inserted.
	 */
	currentNode.getLock().lock();
	currentNode = moveRight(currentNode, key);
	if (currentNode.getValue(key) == null) {
	    /**
	     * Key and value have to be inserted
	     */
	    Integer tmpValue = value;
	    while (true) {
		if (currentNode.getKeysCount() >= Node.L) {
		    /**
		     * There is no free space for key and value
		     */
		    Node newNode = split(currentNode, key, tmpValue);
		    Integer currentNodeMaxKey = currentNode.getMaxKey();
		    nodeStore.writeNode(newNode);
		    nodeStore.writeNode(currentNode);
		    tmpValue = newNode.getId();
		    Node oldNode = currentNode;

		    currentNode = nodeStore.get(stack.pop());
		    currentNode.getLock().lock();
		    moveRight(currentNode, currentNodeMaxKey);

		    oldNode.getLock().unlock();
		} else {
		    /**
		     * There is free space for key and value
		     */
		    currentNode.insert(key, tmpValue);
		    nodeStore.writeNode(currentNode);
		    currentNode.getLock().unlock();
		    return;
		}
	    }
	} else {
	    /**
	     * Key already exists. Rewrite value.
	     */
	    currentNode.insert(key, value);
	    nodeStore.writeNode(currentNode);
	    currentNode.getLock().unlock();
	}
    }

    private Node split(final Node currentNode, final Integer key,
	    final Integer tmpValue) {
	Node newNode = new Node(nodeStore.size(), true);
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
		n.getLock().lock();
		current.getLock().unlock();
		current = n;
	    }
	    return current;
	} else {
	    while ((n = findCorrespondingNode(current, key)).getId().equals(
		    current.getLink())) {
		n.getLock().lock();
		current.getLock().unlock();
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
