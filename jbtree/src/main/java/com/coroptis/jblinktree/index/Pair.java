package com.coroptis.jblinktree.index;

public class Pair<K, V> {

    private final K key;

    private final V value;

    private Pair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public static <S, T> Pair<S, T> make(final S key, final T value) {
        return new Pair<S, T>(key, value);
    }

    /**
     * @return the key
     */
    public K getKey() {
        return key;
    }

    /**
     * @return the value
     */
    public V getValue() {
        return value;
    }

}
