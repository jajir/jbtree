package com.coroptis.jblinktree;

public interface TreeData<K, V> {
    
    Integer getRootNodeId();
    
    <S> Integer splitRootNode(final Node<K, S> currentNode, final Node<K, S> newNode);
    
}
