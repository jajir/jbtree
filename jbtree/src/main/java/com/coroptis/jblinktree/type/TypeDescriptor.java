package com.coroptis.jblinktree.type;

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
public interface TypeDescriptor<T> extends ValueComparator<T> {

    /**
     * Get maximum length of field when it's stored in bytes.
     *
     * @return maximum length of type.
     */
    int getMaxLength();

    /**
     * Write given value to byte array.
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
     * Verify that given type is same as described.
     *
     * @param object
     *            required verified object
     * @throws NullPointerException
     *             when object is <code>null</code>
     * @throws com.coroptis.jblinktree.JblinktreeException
     *             when given object is not described
     */
    void verifyType(Object object);

    int cmp(byte[] node, int start, byte[] value);

    byte[] getBytes(T value);
}
