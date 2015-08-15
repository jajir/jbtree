package com.coroptis.jblinktree;

import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;

public class JbTreeDataImpl<K, V> implements JbTreeData<K, V> {

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> treeTool;

    private Integer rootNodeId;

    JbTreeDataImpl(final NodeStore<K> nodeStore, final JbTreeTool<K, V> treeTool) {
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTool = Preconditions.checkNotNull(treeTool);
	rootNodeId = treeTool.createRootNode();
    }

    @Override
    public <S> Integer splitRootNode(final Node<K, S> currentNode, final Node<K, S> newNode) {
	ReentrantLock lock = new ReentrantLock(false);
	lock.lock();
	try {
	    if (rootNodeId.equals(currentNode.getId())) {
		Preconditions.checkArgument(rootNodeId.equals(currentNode.getId()));
		rootNodeId = treeTool.splitRootNode(currentNode, newNode);
		nodeStore.unlockNode(currentNode.getId());
	    } else {
		nodeStore.unlockNode(currentNode.getId());
	    }
	} finally {
	    lock.unlock();
	}
	return rootNodeId;
    }

    @Override
    public Integer getRootNodeId() {
	return rootNodeId;
    }

}
