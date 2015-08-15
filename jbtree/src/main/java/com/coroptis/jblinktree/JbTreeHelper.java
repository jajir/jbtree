package com.coroptis.jblinktree;

import java.util.Stack;

public interface JbTreeHelper<K, V> {

    Node<K, V> findAppropriateLeafNode(K key);

    V insertToLeafNode(Node<K, V> currentNode, final K key, final V value,
	    final Stack<Integer> stack);

    void storeValueIntoLeafNode(final Node<K, V> currentNode, final K key, final V value);
}
