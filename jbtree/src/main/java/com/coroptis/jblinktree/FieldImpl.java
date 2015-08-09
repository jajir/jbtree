package com.coroptis.jblinktree;

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

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    public FieldImpl(final int numberOfField, final TypeDescriptor<K> keyTypeDescriptor,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.linkTypeDescriptor = linkTypeDescriptor;
	this.keyTypeDescriptor = keyTypeDescriptor;
	this.valueTypeDescriptor = valueTypeDescriptor;
	this.field = new byte[getPosition(numberOfField) + linkTypeDescriptor.getMaxLength()];
    }

    public FieldImpl(final byte[] field, final TypeDescriptor<K> keyTypeDescriptor,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this(0, keyTypeDescriptor, valueTypeDescriptor, linkTypeDescriptor);
	this.field = new byte[field.length];
	System.arraycopy(field, 0, this.field, 0, this.field.length);
    }

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
    public void copy(final Field<K, V> src, final int srcPos1, final int destPos1, final int length) {
	final int srcPos = getPosition(srcPos1);
	final int p = getPosition(srcPos1 + length) - srcPos;
	final int destPos = getPosition(destPos1);
	System.arraycopy(src.getBytes(), srcPos, field, destPos, p);
	setFlag(src.getFlag());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.Filed#getBytes()
     */
    @Override
    public byte[] getBytes() {
	return field;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.Filed#getLength()
     */
    @Override
    public int getLength() {
	final int length = field.length - linkTypeDescriptor.getMaxLength() - 1;
	final int recordLength = keyTypeDescriptor.getMaxLength()
		+ valueTypeDescriptor.getMaxLength();
	final int out = length / recordLength * 2;
	return out + 1;
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