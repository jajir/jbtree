package com.coroptis.jblinktree.type;

import com.google.common.base.MoreObjects;

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
 * Wrap value. It speed up comparison because it allows compare parts of fields
 * instead of comparing values.
 *
 *
 * @author jajir
 *
 * @param <T>
 *            wrapped type
 */
public final class Wrapper<T> {

    /**
     * Wrapped value.
     */
    private final T value;

    /**
     * Value represented by byte array.
     */
    private final byte[] bytes;

    /**
     * Static factory.
     *
     * @param value
     *            required value
     * @param td
     *            required type descriptor that allows to convert value to byte
     *            array
     * @param <S>
     *            type that will be wrapped
     * @return {@link Wrapper} instance
     */
    public static <S> Wrapper<S> make(final S value,
            final TypeDescriptor<S> td) {
        return new Wrapper<S>(value, td.getBytes(value));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Wrapper.class).add("value", value)
                .toString();
    }

    /**
     * Hidden constructor.
     *
     * @param newValue
     *            optional new value
     * @param newBytes
     *            optional byte representation
     */
    private Wrapper(final T newValue, final byte[] newBytes) {
        this.value = newValue;
        this.bytes = newBytes;
    }

    /**
     * Get value.
     *
     * @return value
     */
    public T getValue() {
        return value;
    }

    /**
     * Get value as byte array.
     *
     * @return byte array
     */
    public byte[] getBytes() {
        return bytes;
    }

}
