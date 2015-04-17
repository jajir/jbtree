package com.coroptis.jblinktree;

import com.google.common.base.Preconditions;

public class TreeManager {

	private Integer idRootNode;

	private final NodeStore nodeStore;

	public TreeManager(final NodeStore nodeStore) {
		this.nodeStore = nodeStore;
	}

	public Integer getValue(final Integer key) {
		if (idRootNode == null) {
			return null;
		}
		Integer nodeId = idRootNode;
		int counter = 100;
		while (counter > 0) {
			final Node node = nodeStore.readNode(nodeId);
			if (node.isLeafNode()) {
				return node.getValue(key);
			}
			nodeId = node.getCorrespondingNodeId(key);
			counter--;
		}
		throw new JblinktreeException(
				"Can't find corresponding value after traversing about 100 layers");
	}

	public void add(final Integer key, final Integer value) {
		Preconditions.checkNotNull(value);
	}

	public void remove(final Integer key) {

	}

}
