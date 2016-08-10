package com.coroptis.jblinktree.type;

import java.io.Serializable;

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
public final class TypeDescriptorByte
        implements Serializable, TypeDescriptor<Byte> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

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
    public int compare(final Byte value1, final Byte value2) {
        return value1.compareTo(value2);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(TypeDescriptorByte.class)
                .add("maxLength", getMaxLength()).toString();
    }

}
