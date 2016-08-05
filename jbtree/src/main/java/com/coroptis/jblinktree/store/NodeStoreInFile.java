package com.coroptis.jblinktree.store;

import java.util.concurrent.atomic.AtomicInteger;

import com.coroptis.jblinktree.JbTreeData;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.coroptis.jblinktree.NodeLocks;
import com.coroptis.jblinktree.NodeStore;
import com.google.common.base.Preconditions;

/**
 * Implementation of {@link NodeStore}. Stores nodes in file system with cache.
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public class NodeStoreInFile<K, V> implements NodeStore<K> {

    private final NodeLocks nodeLocks;

    private final AtomicInteger nextId;

    private final LruCache<K, V> nodeCache;

    private final FileStorage<K, V> fileStorage;

    public NodeStoreInFile(final JbTreeData<K, V> treeData, final NodeBuilder<K, V> nodeBuilder,
	    String fileName, int numberOfNodesCacheSize) {
	this.nextId = new AtomicInteger(FIRST_NODE_ID);
	fileStorage = new FileStorageImpl<K,V>(treeData, nodeBuilder, fileName);
	nodeLocks = new NodeLocks();
	nodeCache = new LruCache<K, V>(nodeBuilder, numberOfNodesCacheSize, new OnEvict<K, V>() {

	    @Override
	    public void evict(Node<K, V> node) {
		fileStorage.store(node);
	    }

	    @Override
	    public Node<K, V> load(Integer nodeId) {
		return fileStorage.load(nodeId);
	    }
	});
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
	Node<K, S> node = (Node<K, S>) nodeCache.get(Preconditions.checkNotNull(nodeId));
	return node;
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
	nodeCache.put((Node<K, V>) node);
    }

    @Override
    public void deleteNode(final Integer idNode) {
	nodeCache.remove(Preconditions.checkNotNull(idNode));
    }

    @Override
    public int countLockedNodes() {
	return nodeLocks.countLockedThreads();
    }

    @Override
    public Integer getNextId() {
	return nextId.getAndIncrement();
    }

    @Override
    public int getMaxNodeId() {
	return nextId.get();
    }

}
