package com.coroptis.jblinktree;

/*
 * #%L
 * jblinktree
 * %%
 * Copyright (C) 2015 coroptis
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.google.common.base.Preconditions;

/**
 * Contain node definition. It's immutable class. It's following information:
 * <ul>
 * <li>l - maximal number of key value pairs in node</li>
 * <li>key data type description</li>
 * <li>value data type description</li>
 * <li>link data type description</li>
 * </ul>
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 *
 */
public final class JbNodeDefImpl<K, V> implements JbNodeDef<K, V> {

    /**
     * Maximal number of keys in node.
     */
    private final int l;

    /**
     * Key type descriptor.
     */
    private final TypeDescriptor<K> keyTypeDescriptor;

    /**
     * Value type descriptor.
     */
    private final TypeDescriptor<V> valueTypeDescriptor;

    /**
     * Link type descriptor.
     */
    private final TypeDescriptor<Integer> linkTypeDescriptor;

    /**
     * Precomputed key and value pair size.
     */
    private final int keyAndValueSize;

    /**
     * Field contains position of value.
     */
    private final int[] positionOfValue;

    /**
     * Field contains position of key.
     */
    private final int[] positionOfKey;

    /**
     * Basic constructor.
     *
     * @param defaultL
     *            maximal number of keys in node
     * @param keyTypeDesc
     *            required key type descriptor
     * @param valueTypeDesc
     *            required value type descriptor
     * @param linkTypedesc
     *            required link type descriptor
     */
    public JbNodeDefImpl(final int defaultL,
            final TypeDescriptor<K> keyTypeDesc,
            final TypeDescriptor<V> valueTypeDesc,
            final TypeDescriptor<Integer> linkTypedesc) {
        this.l = defaultL;
        this.keyTypeDescriptor = Preconditions.checkNotNull(keyTypeDesc);
        this.valueTypeDescriptor = Preconditions.checkNotNull(valueTypeDesc);
        this.linkTypeDescriptor = Preconditions.checkNotNull(linkTypedesc);
        this.keyAndValueSize = getKeyTypeDescriptor().getMaxLength()
                + getValueTypeDescriptor().getMaxLength();
        positionOfValue = new int[getL() + 1];
        positionOfKey = new int[getL() + 1];
        for (int i = 0; i < getL() + 1; i++) {
            positionOfValue[i] =
                    JbNodeDef.FLAGS_LENGTH + i * getKeyAndValueSize();
            positionOfKey[i] = positionOfValue[i]
                    + getValueTypeDescriptor().getMaxLength();
        }
    }

    @Override
    public int getL() {
        return l;
    }

    @Override
    public TypeDescriptor<K> getKeyTypeDescriptor() {
        return keyTypeDescriptor;
    }

    @Override
    public TypeDescriptor<V> getValueTypeDescriptor() {
        return valueTypeDescriptor;
    }

    @Override
    public TypeDescriptor<Integer> getLinkTypeDescriptor() {
        return linkTypeDescriptor;
    }

    @Override
    public int getFieldMaxLength() {
        return getFieldActualLength(getL());
    }

    @Override
    public int getFieldActualLength(final int numberOfKeys) {
        return getValuePosition(numberOfKeys)
                + getLinkTypeDescriptor().getMaxLength();
    }

    @Override
    public int getKeyAndValueSize() {
        return keyAndValueSize;
    }

    @Override
    public int getKeyPosition(final int position) {
        return positionOfKey[position];
    }

    @Override
    public int getValuePosition(final int position) {
        return positionOfValue[position];
    }

    /**
     * Override {@link System#toString()} method.
     */
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff.append("JbNodeDef{L=");
        buff.append(l);
        buff.append(", keyTypeDescriptor=");
        buff.append(getKeyTypeDescriptor());
        buff.append(", valueTypeDescriptor=");
        buff.append(getValueTypeDescriptor());
        buff.append(", linkTypeDescriptor=");
        buff.append(getLinkTypeDescriptor());
        buff.append("}");
        return buff.toString();
    }

}
