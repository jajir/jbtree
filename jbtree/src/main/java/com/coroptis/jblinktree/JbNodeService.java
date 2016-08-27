package com.coroptis.jblinktree;

import java.util.List;

public interface JbNodeService<K, V> {

    /**
     * When it's non-leaf node it return pointer to next node where should be
     * given key stored.
     * <p>
     * When key is bigger that all keys in node than link is returned. In case
     * of rightmost node next link is <code>null</code>
     * </p>
     * <p>
     * Correct working of method depends on correct setting of max keys.
     * </p>
     * <p>
     * There is possible performance improvement, when search not insert
     * procedure called this method than when key is bigger than max key than
     * null can be returned.
     * </p>
     *
     * @param key
     *            required key
     * @return node id, in case of rightmost node it returns <code>null</code>
     *         because link is empty
     */
    Integer getCorrespondingNodeId(Node<K, Integer> node, K key);

    /**
     * Insert or override some value in node.
     *
     * @param key
     *            required key
     * @param value
     *            required value
     * @throws NullPointerException
     *             when key or value is null
     */
    <S> void insert(Node<K, S> node, K key, S value);

    /**
     * For non-leaf tree it update key of some tree. It's useful for update
     * sub-node max key.
     *
     * @param valueToUpdate
     *            required value which will be find
     * @param keyToSet
     *            required key that will be set to find value
     * @return return <code>true</code> when valueToUpdate was found and key was
     *         really updated otherwise return <code>false</code>
     */
    boolean updateKeyForValue(Node<K, Integer> node, Integer valueToUpdate,
            K keyToSet);

    /**
     * Get list of all node id stored in this node.
     *
     * @return list of id
     */
    List<Integer> getNodeIds(Node<K, Integer> node);

    /**
     * Write node content into {@link StringBuilder}.
     *
     * @param buff
     *            required {@link StringBuilder} instance
     * @param intendation
     *            how many white spaces should be added before each line.
     */
    <S> void writeTo(Node<K, S> node, StringBuilder buff, String intendation);

    /**
     * Remove key and associated value from node.
     *
     * @param key
     *            required key to remove
     * @return when key was found and removed it return <code>true</code>
     *         otherwise it return <code>false</code>
     * @throws NullPointerException
     *             when key or value is null
     */
    <S> S remove(final Node<K, S> node, final K key);

    /**
     * Find value for given key.
     * <p>
     * Method is not fast and should not be called in main search algorithm.
     * </p>
     *
     * @param key
     *            required key
     * @return found value if there is any, when value is <code>null</code> or
     *         there is no such key <code>null</code> is returned.
     */
    V getValueByKey(Node<K, V> node, K key);

}
