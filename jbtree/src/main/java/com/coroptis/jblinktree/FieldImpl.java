package com.coroptis.jblinktree;

import java.util.Arrays;

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
 * Holds node data. Data are stored as v1,k1,v2,k2 ...For example:
 * <ul>
 * <li>key length - kl bytes</li>
 * <li>value length is fixed- 4 bytes</li>
 * <li>filed length - length</li>
 * </ul>
 * <table border="1" style="border-collapse:collapse" summary=
 * "keys and values meaning in node">
 * <tr>
 * <td>Meaning</td>
 * <td>flags</td>
 * <td>P(0)</td>
 * <td>K(0)</td>
 * <td>P(1)</td>
 * <td>K(1)</td>
 * <td>&nbsp;...&nbsp;</td>
 * <td colspan="4">link</td>
 * </tr>
 * <tr>
 * <td>byte index</td>
 * <td>0</td>
 * <td>1</td>
 * <td>5</td>
 * <td>5 + kl</td>
 * <td>9 + kl</td>
 * <td>&nbsp;...&nbsp;</td>
 * <td>length - 4</td>
 * <td>length - 3</td>
 * <td>length - 2</td>
 * <td>length - 1</td>
 * </tr>
 * </table>
 * <p>
 * Filed required data type descriptor for:
 * </p>
 * <ul>
 * <li>key</li>
 * <li>value - if it's non leaf node than this is {@link TypeDescriptorInteger}
 * </li>
 * <li>link - always {@link TypeDescriptorInteger}</li>
 * </ul>
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

    private final JbNodeDef<K, V> nodeDef;

    /**
     * Basic constructor.
     * 
     * @param numberOfField
     *            required number of items that could be stored in field.
     * @param nodeDef
     *            required tree definition
     */
    public FieldImpl(final int numberOfField, final JbNodeDef<K, V> nodeDef) {
	this.nodeDef = nodeDef;
	this.field = new byte[getPosition(numberOfField)
		+ nodeDef.getLinkTypeDescriptor().getMaxLength()];
	size = computeLength();
    }

    /**
     * Constructor create field from byte array.
     * 
     * @param field
     *            required byte array, method create defensive copy of this
     *            array
     * @param treeData
     *            required tree definition
     */
    public FieldImpl(final byte[] field, final JbNodeDef<K, V> treeData) {
	this.nodeDef = treeData;
	this.field = new byte[field.length];
	System.arraycopy(field, 0, this.field, 0, this.field.length);
	size = computeLength();
	if (getFlag() != Node.M
		&& !(nodeDef.getValueTypeDescriptor() instanceof TypeDescriptorInteger)) {
	    throw new JblinktreeException("Non-leaf node doesn't have value of type integer, it's "
		    + nodeDef.getValueTypeDescriptor().getClass().getName());
	}
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
	return p1 * nodeDef.getKeyTypeDescriptor().getMaxLength()
		+ p2 * nodeDef.getValueTypeDescriptor().getMaxLength() + 1;
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
	return field;
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
	final int length = field.length - nodeDef.getLinkTypeDescriptor().getMaxLength() - 1;
	final int recordLength = nodeDef.getKeyAndValueSize();
	final int out = length / recordLength * 2;
	return out + 1;
    }

    @Override
    public int getLength() {
	return size;
    }

    @Override
    public K getKey(final int position) {
	return nodeDef.getKeyTypeDescriptor().load(field, getPosition(position));
    }

    @Override
    public V getValue(final int position) {
	return nodeDef.getValueTypeDescriptor().load(field, getPosition(position));
    }

    @Override
    public void setKey(final int position, final K value) {
	nodeDef.getKeyTypeDescriptor().save(field, getPosition(position), value);
    }

    @Override
    public void setValue(final int position, final V value) {
	nodeDef.getValueTypeDescriptor().save(field, getPosition(position), value);
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
	return nodeDef.getLinkTypeDescriptor().load(field, field.length - 4);
    }

    @Override
    public void setLink(final Integer link) {
	nodeDef.getLinkTypeDescriptor().save(field, field.length - 4, link);
    }

    /**
     * @return the nodeDef
     */
    @Override
    public JbNodeDef<K, V> getNodeDef() {
	return nodeDef;
    }

}