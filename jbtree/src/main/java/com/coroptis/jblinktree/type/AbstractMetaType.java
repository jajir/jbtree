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
 * Abstract class for simple meta data definition.
 *
 * @author jajir
 *
 * @param <T>
 *            described data type
 */
public abstract class AbstractMetaType<T>
        implements TypeDescriptor<T>, MetaType<T> {

    @Override
    public final void save(final byte[] data, final int from, final T value) {
        Wrapper<T> w = Wrapper.make(value, this);
        save(data, from, w);
    }

    @Override
    public final void verifyType(final Object object) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

    @Override
    public final int cmp(final byte[] node, final int start,
            final Wrapper<T> value) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

    @Override
    public final byte[] getRawBytes(final T value) {
        return getBytes(value);
    }

}
