package com.coroptis.jblinktree.store;

/**
 * Simple storing values to special file. Class doesn't use caching. Class is
 * not thread safe.
 *
 * @author jajir
 *
 * @param <V>
 *            value type
 */
public interface ValueFileStorage<V> {

    /**
     * Value File Name file extension.
     */
    String FILE_NAME_SUFFIX = "vfs";

    void store(Integer valueId, V value);

    V load(Integer valueId);

    void close();

}
