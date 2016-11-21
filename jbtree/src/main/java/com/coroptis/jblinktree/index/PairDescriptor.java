package com.coroptis.jblinktree.index;

import com.coroptis.jblinktree.type.TypeDescriptor;

public class PairDescriptor<K, V> {

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    public PairDescriptor(final TypeDescriptor<K> keyTypeDesc,
            final TypeDescriptor<V> valueTypeDesc) {
        this.keyTypeDescriptor = keyTypeDesc;
        this.valueTypeDescriptor = valueTypeDesc;
    }

    /**
     * @return the keyTypeDescriptor
     */
    public TypeDescriptor<K> getKeyTypeDescriptor() {
        return keyTypeDescriptor;
    }

    /**
     * @return the valueTypeDescriptor
     */
    public TypeDescriptor<V> getValueTypeDescriptor() {
        return valueTypeDescriptor;
    }

}
