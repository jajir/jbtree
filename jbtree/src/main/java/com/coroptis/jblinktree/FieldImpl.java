package com.coroptis.jblinktree;

import java.util.Arrays;

import com.coroptis.jblinktree.type.TypeDescriptor;

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

/**
 * Holds node data. Data are stored as v1,k1,v2,k2 ...
 * 
 * @author jajir
 * 
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 * 
 */
public class FieldImpl<K, V> implements Field<K, V> {

    private byte[] field;

    /**
     * Object doesn't support length changes.
     */
    private final int size;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    /**
     * Basic constructor.
     * 
     * @param numberOfField
     *            required number of items that could be stored in field.
     * @param keyTypeDescriptor
     *            required key type descriptor
     * @param valueTypeDescriptor
     *            required value type descriptor
     * @param linkTypeDescriptor
     *            required link type descriptor
     */
    public FieldImpl(final int numberOfField, final TypeDescriptor<K> keyTypeDescriptor,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.linkTypeDescriptor = linkTypeDescriptor;
	this.keyTypeDescriptor = keyTypeDescriptor;
	this.valueTypeDescriptor = valueTypeDescriptor;
	this.field = new byte[getPosition(numberOfField) + linkTypeDescriptor.getMaxLength()];
	size = computeLength();
    }

    /**
     * Constructor create field from byte array.
     * 
     * @param field
     *            required byte array, method create defensive copy of this
     *            array
     * @param keyTypeDescriptor
     *            required key type descriptor
     * @param valueTypeDescriptor
     *            required value type descriptor
     * @param linkTypeDescriptor
     *            required link type descriptor
     */
    public FieldImpl(final byte[] field, final TypeDescriptor<K> keyTypeDescriptor,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.linkTypeDescriptor = linkTypeDescriptor;
	this.keyTypeDescriptor = keyTypeDescriptor;
	this.valueTypeDescriptor = valueTypeDescriptor;
	this.field = new byte[field.length];
	System.arraycopy(field, 0, this.field, 0, this.field.length);
	size = computeLength();
    }

    /**
     * Compute index in byte array where should be item at given position
     * stored.
     * 
     * @param position
     *            required position
     * @return byte array position
     */
    private int getPosition(int position) {
	final int p1 = position >>> 1;
	final int p2 = (position + 1) >>> 1;
	return p1 * keyTypeDescriptor.getMaxLength() + p2 * valueTypeDescriptor.getMaxLength() + 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.Filed#toString()
     */
    @Override
    public String toString() {
	StringBuilder buff = new StringBuilder();
	buff.append("Field{field=[");
	for (byte i = 0; i < field.length; i++) {
	    if (i != 0) {
		buff.append(", ");
	    }
	    buff.append(field[i]);
	}
	buff.append("]}");
	return buff.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.Filed#copy(com.coroptis.jblinktree.Filed,
     * int, int, int)
     */
    @Override
    public void copy(final Field<K, V> src, final int srcPos1, final int destPos1,
	    final int length) {
	final int srcPos = getPosition(srcPos1);
	final int p = getPosition(srcPos1 + length) - srcPos;
	final int destPos = getPosition(destPos1);
	final FieldImpl<K, V> f = (FieldImpl<K, V>) src;
	System.arraycopy(f.field, srcPos, field, destPos, p);
	setFlag(src.getFlag());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.Filed#getBytes()
     */
    @Override
    public byte[] getBytes() {
	final byte out[] = new byte[field.length];
	System.arraycopy(field, 0, out, 0, field.length);
	return out;
    }

    /**
     * Equals two fields objects. Juswt byte field is counted.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object obj) {
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof FieldImpl)) {
	    return false;
	}
	final FieldImpl<K, V> n = (FieldImpl<K, V>) obj;
	if (field.length == n.field.length) {
	    for (int i = 0; i < field.length; i++) {
		if (field[i] != n.field[i]) {
		    return false;
		}
	    }
	    return true;
	} else {
	    return false;
	}
    }

    @Override
    public int hashCode() {
	return Arrays.hashCode(field);
    }

    private int computeLength() {
	final int length = field.length - linkTypeDescriptor.getMaxLength() - 1;
	final int recordLength = keyTypeDescriptor.getMaxLength()
		+ valueTypeDescriptor.getMaxLength();
	final int out = length / recordLength * 2;
	return out + 1;
    }

    @Override
    public int getLength() {
	return size;
    }

    @Override
    public K getKey(final int position) {
	return keyTypeDescriptor.load(field, getPosition(position));
    }

    @Override
    public V getValue(final int position) {
	return valueTypeDescriptor.load(field, getPosition(position));
    }

    @Override
    public void setKey(final int position, final K value) {
	keyTypeDescriptor.save(field, getPosition(position), value);
    }

    @Override
    public void setValue(final int position, final V value) {
	valueTypeDescriptor.save(field, getPosition(position), value);
    }

    @Override
    public byte getFlag() {
	return field[0];
    }

    @Override
    public void setFlag(final byte flag) {
	this.field[0] = flag;
    }

    @Override
    public Integer getLink() {
	return linkTypeDescriptor.load(field, field.length - 4);
    }

    @Override
    public void setLink(final Integer link) {
	linkTypeDescriptor.save(field, field.length - 4, link);
    }

}