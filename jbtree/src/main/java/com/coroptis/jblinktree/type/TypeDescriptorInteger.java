package com.coroptis.jblinktree.type;

import com.coroptis.jblinktree.JblinktreeException;
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
 * Integer type descriptor.
 *
 * @author jajir
 *
 */
public final class TypeDescriptorInteger
        implements TypeDescriptor<Integer> {

    /**
     * How many bytes is required to store Integer.
     */
    private static final int REQUIRED_BYTES = 4;

    /**
     * With byte AND allows to select required part of bytes.
     */
    private static final int BYTE_MASK = 0xFF;

    /**
     * Bite shift for 0 bits.
     */
    private static final int BYTE_SHIFT_0 = 0;

    /**
     * Bite shift for 8 bits.
     */
    private static final int BYTE_SHIFT_8 = 8;

    /**
     * Bite shift for 16 bits.
     */
    private static final int BYTE_SHIFT_16 = 16;

    /**
     * Bite shift for 24 bits.
     */
    private static final int BYTE_SHIFT_24 = 24;

    /**
     * Default hash code.
     */
    private static final int DEFAULT_HASHCODE = 7312485;

    @Override
    public int getMaxLength() {
        return REQUIRED_BYTES;
    }

    @Override
    public void save(final byte[] data, final int from, final Integer value) {
        int pos = from;
        int v = value.intValue();
        data[pos++] = (byte) ((v >>> BYTE_SHIFT_24) & BYTE_MASK);
        data[pos++] = (byte) ((v >>> BYTE_SHIFT_16) & BYTE_MASK);
        data[pos++] = (byte) ((v >>> BYTE_SHIFT_8) & BYTE_MASK);
        data[pos] = (byte) ((v >>> BYTE_SHIFT_0) & BYTE_MASK);
    }

    @Override
    public Integer load(final byte[] data, final int from) {
        int pos = from;
        return data[pos++] << BYTE_SHIFT_24
                | (data[pos++] & BYTE_MASK) << BYTE_SHIFT_16
                | (data[pos++] & BYTE_MASK) << BYTE_SHIFT_8
                | (data[pos] & BYTE_MASK);
    }

    @Override
    public void verifyType(final Object object) {
        if (!(object instanceof Integer)) {
            throw new JblinktreeException("Object of wrong type ("
                    + object.getClass().getName() + ")");
        }
    }

    @Override
    public int compareValues(final Integer value1, final Integer value2) {
        return value1.compareTo(value2);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(TypeDescriptorInteger.class)
                .add("maxLength", getMaxLength()).toString();
    }

    /**
     * Always return same number. All instances of this class are same.
     */
    @Override
    public int hashCode() {
        return DEFAULT_HASHCODE;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass();
    }

}
