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
 * Class holding meta data about Integer data type.
 *
 * @author jajir
 *
 */
public final class MetaTypeInteger
        extends AbstractMetaType<TypeDescriptorInteger> {

    @Override
    public byte getCode() {
        return MetaType.TYPE_INTEGER;
    }

    @Override
    public Class<TypeDescriptorInteger> getMetaTypeClass() {
        return TypeDescriptorInteger.class;
    }

    @Override
    public TypeDescriptorInteger getInstance() {
        return new TypeDescriptorInteger();
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

    @Override
    public void save(final byte[] data, final int from,
            final Wrapper<TypeDescriptorInteger> value) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

    @Override
    public byte[] getBytes(final TypeDescriptorInteger value) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

    @Override
    public TypeDescriptorInteger load(final byte[] data, final int from) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

}
