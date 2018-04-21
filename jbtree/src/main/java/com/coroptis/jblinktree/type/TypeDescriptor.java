package com.coroptis.jblinktree.type;

import java.io.InputStream;

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
 * Interface describing data type entering to tree as key or value.
 *
 * @author jajir
 *
 * @param <T>
 *            type that will be described
 *
 */
public interface TypeDescriptor<T> {

    /**
     * Get maximum length of field when it's stored in bytes.
     *
     * @return maximum length of type.
     */
    int getMaxLength();

    /**
     * Write given value to byte array. Internally this method should be
     * implemented by called same with wrapper instead of key.
     *
     * @param data
     *            required byte array
     * @param from
     *            required position when will be object written
     * @param value
     *            required type instance
     * @throws NullPointerException
     *             when value is <code>null</code>
     */
    void save(byte[] data, int from, T value);

    /**
     * Write given value to byte array.
     *
     * @param data
     *            required byte array
     * @param from
     *            required position when will be object written
     * @param value
     *            required type wrapper instance
     * @throws NullPointerException
     *             when value is <code>null</code>
     */
    void save(byte[] data, int from, Wrapper<T> value);

    /**
     * Load type instance from byte array.
     *
     * @param data
     *            required byte field where will be data stored.
     * @param from
     *            required position from where will be object loaded
     * @return loaded type instance
     */
    T load(byte[] data, int from);

    /**
     * Load value from input stream.
     *
     * @param inputStream
     *            required input stream
     * @return loaded data type instance
     */
    T load(InputStream inputStream);

    /**
     * Verify that given type is same as described.
     *
     * @param object
     *            required verified object
     * @throws NullPointerException
     *             when object is <code>null</code>
     * @throws com.coroptis.jblinktree.util.JblinktreeException
     *             when given object is not described
     */
    void verifyType(Object object);

    /**
     * Compare node with given value. Start is position in node where starts
     * value to be compared.
     * <p>
     * Compare should return same results as node.value.compare(value).
     * </p>
     *
     * @param node
     *            required node
     * @param start
     *            required position in node byte array
     * @param value
     *            require value wrapper to compare
     * @return comparison result
     */
    int cmp(byte[] node, int start, Wrapper<T> value);

    /**
     * Convert value to byte representation. This representation is space
     * saving.
     *
     * @param value
     *            required value
     * @return byte array representing given value
     */
    byte[] getBytes(T value);

    /**
     * Return byte array representing unlimited field value. Length of this
     * field could be longer than {@link #getMaxLength()} value. This value
     * doesn't contains any metadata.
     *
     * @param value
     *            required value
     * @return byte array representing given value
     */
    byte[] getRawBytes(final T value);

}
