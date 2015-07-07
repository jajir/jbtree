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
 * Contains data for node.
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 * 
 * 
 */
public interface Field<K, V> {

    @Deprecated
    public Integer get(int position);

    public K getKey(int position);

    public V getValue(int position);

    public void copy(Field<K, V> src, int srcPos1, int destPos1, int length);

    @Deprecated
    public void set(int position, Integer value);

    public void setKey(int position, K value);

    public void setValue(int position, V value);

    /**
     * @return the field
     */
    public Integer[] getField();

    public byte[] getBytes();

    public int getLength();

}