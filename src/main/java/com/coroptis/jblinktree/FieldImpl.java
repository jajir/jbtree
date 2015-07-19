package com.coroptis.jblinktree;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

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

    private final TypeDescriptor keyTypeDescriptor;

    private final TypeDescriptor valueTypeDescriptor;

    public FieldImpl(final int numberOfField) {
	keyTypeDescriptor = new TypeDescriptorInteger();
	valueTypeDescriptor = new TypeDescriptorInteger();
	this.field = new byte[getPosition(numberOfField)];
    }

    public FieldImpl(final Integer[] field) {
	this(field.length);
	for (int i = 0; i < field.length; i++) {
	    set(i, field[i]);
	}
    }

    public FieldImpl(final byte[] field) {
	this(0);
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
     * @see com.coroptis.jblinktree.Filed#get(int)
     */
    @Override
    public Integer get(int position) {
	return load(field, getPosition(position));
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
    public void copy(Field<K, V> src, int srcPos1, int destPos1, int length) {
	int p = getPosition(srcPos1 + length) - getPosition(srcPos1);
	int srcPos = getPosition(srcPos1);
	int destPos = getPosition(destPos1);
	System.arraycopy(src.getBytes(), srcPos, field, destPos, p);
	setFlag(src.getFlag());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.Filed#set(int, java.lang.Integer)
     */
    @Override
    public void set(int position, Integer value) {
	int index = getPosition(position);
	save(field, index, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.coroptis.jblinktree.Filed#getField()
     */
    @Override
    public Integer[] getField() {
	Integer[] out = new Integer[getLength()];
	for (int i = 0; i < out.length; i++) {
	    out[i] = get(i);
	}
	return out;
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
	int length = field.length - 4; // remove link length
	int rest = length % (keyTypeDescriptor.getMaxLength() + valueTypeDescriptor.getMaxLength());
	int out = length / (keyTypeDescriptor.getMaxLength() + valueTypeDescriptor.getMaxLength())
		* 2;
	if (rest == 0) {
	    return out;
	} else {
	    return out + 1;
	}
    }

    private Integer load(byte[] data, int from) {
	return data[from] << 24 | (data[from + 1] & 0xFF) << 16 | (data[from + 2] & 0xFF) << 8
		| (data[from + 3] & 0xFF);
    }

    private void save(byte[] data, int from, Integer value) {
	int v = value.intValue();
	data[from] = (byte) ((v >>> 24) & 0xFF);
	data[from + 1] = (byte) ((v >>> 16) & 0xFF);
	data[from + 2] = (byte) ((v >>> 8) & 0xFF);
	data[from + 3] = (byte) ((v >>> 0) & 0xFF);
    }

    @Override
    public K getKey(int position) {
	return (K) load(field, getPosition(position));
    }

    @Override
    public V getValue(int position) {
	return (V) load(field, getPosition(position));
    }

    @Override
    public void setKey(int position, K value) {
	save(field, getPosition(position), (Integer) value);
    }

    @Override
    public void setValue(int position, V value) {
	save(field, getPosition(position), (Integer) value);
    }

    @Override
    public byte getFlag() {
	return field[0];
    }

    @Override
    public void setFlag(final byte flag) {
	this.field[0] = flag;
    }

}