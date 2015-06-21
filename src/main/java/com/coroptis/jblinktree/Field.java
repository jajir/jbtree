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

/**
 * Holds node data.
 * 
 * @author jajir
 * 
 */
public class Field {

    private final int KEY_LENGTH = 4;

    private final int VALUE_LENGTH = 4;

    private byte[] field;

    public Field(final int numberOfField) {
	this.field = new byte[getPosition(numberOfField)];
	for (int i = 0; i < field.length / 4; i++) {
	    set(i, null);
	}
    }

    public Field(final Integer[] field) {
	this(field.length);
	for (int i = 0; i < field.length; i++) {
	    set(i, field[i]);
	}
    }

    private int getPosition(int position) {
	final int p1 = position >>> 1;
	final int p2 = (position + 1) >>> 1;
	return p1 * KEY_LENGTH + p2 * VALUE_LENGTH;
    }

    public Integer get(int position) {
	return load(field, getPosition(position));
    }

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

    public void copy(Field src, int srcPos1, int destPos1, int length) {
	int p = getPosition(srcPos1 + length) - getPosition(srcPos1);
	int srcPos = getPosition(srcPos1);
	int destPos = getPosition(destPos1);
	System.arraycopy(src.getBytes(), srcPos, field, destPos, p);
    }

    public void set(int position, Integer value) {
	int index = getPosition(position);
	save(field, index, value);
    }

    /**
     * @return the field
     */
    public Integer[] getField() {
	Integer[] out = new Integer[getLength()];
	for (int i = 0; i < out.length; i++) {
	    out[i] = get(i);
	}
	return out;
    }

    public byte[] getBytes() {
	return field;
    }

    public int getLength() {
	int length = field.length - 4; // remove link length
	int rest = length % (KEY_LENGTH + VALUE_LENGTH);
	int out = length / (KEY_LENGTH + VALUE_LENGTH) * 2;
	if (rest == 0) {
	    return out + 1;
	} else {
	    return out + 2;
	}
    }

    private Integer load(byte[] data, int from) {
	if (data[from] == -128 && data[from + 1] == 0 && data[from + 2] == 0 && data[from + 3] == 0) {
	    return null;
	}
	return data[from] << 24 | (data[from + 1] & 0xFF) << 16 | (data[from + 2] & 0xFF) << 8
		| (data[from + 3] & 0xFF);
    }

    private void save(byte[] data, int from, Integer value) {
	int v = value == null ? Integer.MIN_VALUE : value.intValue();
	data[from] = (byte) ((v >>> 24) & 0xFF);
	data[from + 1] = (byte) ((v >>> 16) & 0xFF);
	data[from + 2] = (byte) ((v >>> 8) & 0xFF);
	data[from + 3] = (byte) ((v >>> 0) & 0xFF);
    }

}