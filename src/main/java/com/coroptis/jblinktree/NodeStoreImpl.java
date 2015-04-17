package com.coroptis.jblinktree;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

public class NodeStoreImpl implements NodeStore {

	private final Map<Integer, Node> nodes;

	private final Logger logger = LoggerFactory.getLogger(NodeStoreImpl.class);

	@Inject
	NodeStoreImpl() {
		nodes = new HashMap<Integer, Node>();
	}

	@Override
	public void lockNode(final Integer nodeId) {
		Node node = readNode(nodeId);
		node.getLock().lock();
		logger.debug("Node with id '{}' is locked", nodeId);
	}

	@Override
	public void unlockNode(final Integer nodeId) {
		Node node = readNode(nodeId);
		node.getLock().unlock();
		logger.debug("Node with id '{}' unlocked", nodeId);
	}

	@Override
	public Node readNode(final Integer nodeId) {
		Node node = nodes.get(Preconditions.checkNotNull(nodeId));
		if (node == null) {
			throw new JblinktreeException("there is no node with id '" + nodeId
					+ "'");
		}
		return node;
	}

	@Override
	public void writeNode(final Node node) {
		nodes.put(1, node);
	}

}
