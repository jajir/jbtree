package com.coroptis.jblinktree;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;

public class NodeImpl implements Node {

	private Lock lock;
	private final boolean isLeafNode;
	private final Integer[] nodeIds;
	private final Integer[] keys;

	NodeImpl(final Integer kparameter, final boolean isLeafNode) {
		Preconditions.checkNotNull(kparameter);
		if (kparameter <= 1) {
			throw new JblinktreeException(
					"Tree k parametes must be greater than 1, current value is '"
							+ kparameter + "'");
		}
		this.isLeafNode = isLeafNode;
		this.lock = new ReentrantLock();
		this.keys = new Integer[kparameter];
		this.nodeIds = new Integer[kparameter + 2];
	}

	public void insert(final Integer key, final Integer value) {
		for (int i = 0; i < keys.length; i++) {
			if (keys[i] == key) {
				throw new JblinktreeException("value is already in node");
			}
			if (keys[i] > key) {
				// value should be in nodeIds index i
				nodeIds[i] = value;
			}
		}
	}

	@Override
	public Integer getCorrespondingNodeId(Integer key) {
		if (isLeafNode) {
			throw new JblinktreeException(
					"Leaf node doesn't have any child nodes.");
		}
		for (int i = 0; i < keys.length; i++) {
			if (key <= keys[i]) {
				return nodeIds[i];
			}
		}
		return null;
	}

	@Override
	public Integer getValue(Integer key) {
		if (!isLeafNode) {
			throw new JblinktreeException(
					"Non-leaf node doesn't have leaf value.");
		}
		for (int i = 0; i < keys.length; i++) {
			if (key == keys[i]) {
				return nodeIds[i];
			}
		}
		return null;
	}

	@Override
	public boolean isLeafNode() {
		return isLeafNode;
	}

	@Override
	public Lock getLock() {
		return lock;
	}

}
