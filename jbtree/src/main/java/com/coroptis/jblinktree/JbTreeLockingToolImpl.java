package com.coroptis.jblinktree;

import com.google.common.base.Preconditions;

public class JbTreeLockingToolImpl<K, V> implements JbTreeLockingTool<K, V> {

    private final JbTreeTool<K, V> treeTool;

    public JbTreeLockingToolImpl(final JbTreeTool<K, V> treeTool) {
	this.treeTool = Preconditions.checkNotNull(treeTool);
    }

    @Override
    public Node<K, Integer> moveRightNonLeafNode(Node<K, Integer> current, final K key) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(current);
	if (current.isLeafNode()) {
	    throw new JblinktreeException("method is for non-leaf nodes, but given node is leaf: "
		    + current.toString());
	}
	Integer nextNodeId = current.getCorrespondingNodeId(key);
	while (!NodeImpl.EMPTY_INT.equals(nextNodeId) && nextNodeId.equals(current.getLink())) {
	    current = treeTool.moveToNextNode(current, nextNodeId);
	    nextNodeId = current.getCorrespondingNodeId(key);
	}
	return current;
    }

    @Override
    public Node<K, V> moveRightLeafNode(Node<K, V> current, final K key) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(current);
	if (!current.isLeafNode()) {
	    throw new JblinktreeException("method is for leaf nodes, but given node is non-leaf");
	}
	while (treeTool.canMoveToNextNode(current, key)) {
	    current = treeTool.moveToNextNode(current, current.getLink());
	}
	return current;
    }

}
