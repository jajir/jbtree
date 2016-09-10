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
 * Class holding meta data about Byte data type.
 *
 * @author jajir
 *
 */
public final class MetaTypeByte extends AbstractMetaType<TypeDescriptorByte> {

    @Override
    public byte getCode() {
        return MetaType.TYPE_BYTE;
    }

    @Override
    public Class<TypeDescriptorByte> getMetaTypeClass() {
        return TypeDescriptorByte.class;
    }

    @Override
    public TypeDescriptorByte getInstance() {
        return new TypeDescriptorByte();
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

    @Override
    public void save(final byte[] data, final int from,
            final Wrapper<TypeDescriptorByte> value) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

    @Override
    public byte[] getBytes(final TypeDescriptorByte value) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

    @Override
    public TypeDescriptorByte load(final byte[] data, final int from) {
        throw new UnsupportedOperationException("It's not alowed.");
    }

}
