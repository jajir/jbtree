package com.coroptis.jblinktree.store;

import com.coroptis.jblinktree.Node;

/**
 * Simple storing nodes to file and reding from file.
 * 
 * @author jan
 *
 * @param <K>
 * @param <V>
 */
public interface FileStorage<K, V> {

    void store(Node<K, V> node);

    Node<K, V> load(Integer nodeId);
    
    void close();

}
