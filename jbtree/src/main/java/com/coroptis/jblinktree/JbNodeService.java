package com.coroptis.jblinktree;

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
    Integer getCorrespondingNodeId(Node<K,Integer> node, K key);

}
