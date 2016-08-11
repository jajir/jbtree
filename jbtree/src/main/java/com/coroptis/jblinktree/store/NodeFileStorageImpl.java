package com.coroptis.jblinktree.store;

import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.Node;
import com.coroptis.jblinktree.NodeBuilder;
import com.google.common.base.Preconditions;

/**
 * Simple thread safe node storage. Could be used just in case when values
 * associated with keys occupy 4 or less bytes.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class NodeFileStorageImpl<K, V> implements NodeFileStorage<K, V> {

    private final NodeBuilder<K, V> nodeBuilder;

    private final JbNodeDef<K, V> nodeDef;

    private final ReentrantLock lock = new ReentrantLock(false);

    private final ValueFileStorage<V> valueFileStorage;

    private final KeyIntFileStorage<K> keyIntFileStorage;

    public NodeFileStorageImpl(final JbNodeDef<K, V> nodeDef,
            final NodeBuilder<K, V> nodeBuilder, String directory) {

        this.nodeDef = Preconditions.checkNotNull(nodeDef);
        this.nodeBuilder = Preconditions.checkNotNull(nodeBuilder);
        Preconditions.checkNotNull(directory);

        this.valueFileStorage = new ValueFileStorageImpl<V>(
                Paths.get(directory, "value.str").toFile(),
                nodeDef.getValueTypeDescriptor());
        this.keyIntFileStorage = new KeyIntFileStorage<K>(
                Paths.get(directory, "value.str").toFile(),
                (JbNodeDef<K, Integer>) nodeDef.getKeyTypeDescriptor(),
                (NodeBuilder<K, Integer>) nodeBuilder);
    }

    @Override
    public void store(Node<K, V> node) {
        lock.lock();
        try {
            if(node.isLeafNode()){
                //TODO nejak splitnout
            }else{
                keyIntFileStorage.store((Node<K, Integer>)node);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Node<K, V> load(Integer nodeId) {
        lock.lock();
        try {
            Node<K, Integer> node = keyIntFileStorage.load(nodeId);
            if(node.isLeafNode()){
                
            }else{
                return (Node<K, V>)node;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        keyIntFileStorage.close();
        valueFileStorage.close();
    }

}
