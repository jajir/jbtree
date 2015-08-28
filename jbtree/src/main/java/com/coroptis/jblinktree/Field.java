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
 * Contains data for node. It contains in flag[byte], value, key pairs and link
 * as Integer.
 * 
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 * 
 * @author jajir
 * 
 */
public interface Field<K, V> {

    /**
     * Get key from specific position.
     * 
     * @param position
     *            required key position
     * @return key
     */
    public K getKey(int position);

    /**
     * Get value from specific position.
     * 
     * @param position
     *            required value position
     * @return value
     */
    public V getValue(int position);

    /**
     * Copy data from {@link Field} <code>src</code> parameter to this object.
     * 
     * @param src
     *            required field from which are data copied
     * @param srcPos
     *            required from which position are data copied
     * @param destPos
     *            required from which position are data written
     * @param length
     *            required how many bytes will be copied
     * @throws ArrayIndexOutOfBoundsException
     *             when some positions are out of array limits
     */
    public void copy(Field<K, V> src, int srcPos, int destPos, int length);

    /**
     * Allows to set key at specific position.
     * 
     * @param position
     *            required position
     * @param value
     *            required key
     */
    public void setKey(int position, K value);

    /**
     * Allows to set value at specific position.
     * 
     * @param position
     *            required position
     * @param value
     *            required value
     */
    public void setValue(int position, V value);

    /**
     * Get byte array containing all field data including:
     * <ul>
     * <li>flag</li>
     * <li>key & value pairs</li>
     * <li>link</li>
     * </ul>
     * <p>
     * Method should return defensive copy.
     * </p>
     * 
     * @return field byte array
     */
    public byte[] getBytes();

    /**
     * Get number of stored keys and values. Keys and values are counted
     * separately.
     * 
     * FIXME now count form 1 change it to 0
     * 
     * TODO check if it's not better to count key,value pairs
     * 
     * @return number of stored keys and values
     */
    public int getLength();

    /**
     * Get flag byte.
     * 
     * @return flab byte
     */
    public byte getFlag();

    /**
     * Allow to set flab byte
     * 
     * @param b
     *            required flag byte
     */
    public void setFlag(byte b);

    /**
     * Get link value.
     * 
     * @return link value, could be {@link Node#EMPTY_INT}
     */
    public Integer getLink();

    /**
     * Allows to set link value.
     * 
     * @param link
     *            link value, could be {@link Node#EMPTY_INT}
     * @throws NullPointerException
     *             when link is <code>null</code>
     */
    public void setLink(Integer link);
}