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
public abstract class AbstractTypeDescriptorMetaData<T>
        implements TypeDescriptor<T>, MetaType {

    @Override
    public int compareValues(final T o1, final T o2) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meda tada about metadata.");
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

    @Override
    public void save(final byte[] data, final int from, final T value) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meta data about metadata.");
    }

    @Override
    public T load(final byte[] data, final int from) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meda tada about metadata.");
    }

    @Override
    public void verifyType(final Object object) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meda tada about metadata.");
    }

}
