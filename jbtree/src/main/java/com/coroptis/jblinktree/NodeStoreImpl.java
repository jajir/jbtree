package com.coroptis.jblinktree;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Preconditions;

/**
 * Implementation of {@link NodeStore}.
 * 
 * @author jajir
 * 
 */
public class NodeStoreImpl<K, V> implements NodeStore<K> {

    private final Map<Integer, byte[]> nodes;

    private final NodeLocks nodeLocks;

    private final NodeBuilder<K, V> nodeBuilder;

    private final IdGenerator idGenerator;

    public NodeStoreImpl(final IdGenerator idGenerator, final NodeBuilder<K, V> nodeBuilder) {
	this.idGenerator = Preconditions.checkNotNull(idGenerator);
	this.nodeBuilder = Preconditions.checkNotNull(nodeBuilder);
	nodes = new ConcurrentHashMap<Integer, byte[]>();
	nodeLocks = new NodeLocks();
    }

    @Override
    public void lockNode(final Integer nodeId) {
	nodeLocks.lockNode(Preconditions.checkNotNull(nodeId));
    }

    @Override
    public void unlockNode(final Integer nodeId) {
	nodeLocks.unlockNode(Preconditions.checkNotNull(nodeId));
    }

    @Override
    public <S> Node<K, S> get(final Integer nodeId) {
	byte[] field = nodes.get(Preconditions.checkNotNull(nodeId));
	if (field == null) {
	    throw new JblinktreeException("There is no node with id '" + nodeId + "'");
	}
	return nodeBuilder.makeNode(nodeId, field);
    }

    @Override
    public <S> Node<K, S> getAndLock(final Integer nodeId) {
	lockNode(nodeId);
	return get(nodeId);
    }

    @Override
    public <S> void writeNode(final Node<K, S> node) {
	Preconditions.checkNotNull(node.getId());
	Preconditions.checkNotNull(node);
	node.verify();
	nodes.put(node.getId(), node.getFieldBytes());
    }

    @Override
    public void deleteNode(final Integer idNode) {
	nodes.remove(Preconditions.checkNotNull(idNode));
    }

    @Override
    public int countLockedNodes() {
	return nodeLocks.countLockedThreads();
    }

    @Override
    public Integer getNextId() {
	return idGenerator.getNextId();
    }

}
