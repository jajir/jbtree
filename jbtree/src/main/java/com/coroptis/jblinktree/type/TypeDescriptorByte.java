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
public final class TypeDescriptorByte implements TypeDescriptor<Byte> {

    /**
     * Default hash code.
     */
    private static final int DEFAULT_HASHCODE = 9014865;

    @Override
    public int getMaxLength() {
        return 1;
    }

    @Override
    public void save(final byte[] data, final int from, final Byte value) {
        data[from] = value.byteValue();
    }

    @Override
    public Byte load(final byte[] data, final int from) {
        return data[from];
    }

    @Override
    public void verifyType(final Object object) {
        if (!(object instanceof Byte)) {
            throw new JblinktreeException("Object of wrong type ("
                    + object.getClass().getName() + ")");
        }
    }

    @Override
    public int compareValues(final Byte value1, final Byte value2) {
        return value1.compareTo(value2);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(TypeDescriptorByte.class)
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

    @Override
    public int cmp(final byte[] node, final int start,
            final Wrapper<Byte> value) {
        return node[start] - value.getBytes()[0];
    }

    @Override
    public byte[] getBytes(final Byte value) {
        byte[] out = new byte[1];
        out[0] = value;
        return out;
    }

}
