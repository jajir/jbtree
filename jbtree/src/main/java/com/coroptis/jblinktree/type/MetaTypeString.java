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

import java.nio.charset.Charset;

/**
 * Class holding meta data about string data type.
 *
 * @author jajir
 *
 */
public final class MetaTypeString
        extends AbstractMetaType<TypeDescriptorString> {

    /**
     * Type descriptor for working with integers.
     */
    private final TypeDescriptor<Integer> tdInteger
    /**
     *
     */
            = new TypeDescriptorInteger();

    /**
     * String type descriptor for saving/loading charset.
     */
    private final TypeDescriptor<String> tdString = new TypeDescriptorString(
            RESERVED_BYTES_FOR_CHARSET - tdInteger.getMaxLength(),
            Charset.forName("ISO-8859-1"));

    /**
     * How many chars is reserved for charset name. Available number of string
     * will be 46.
     */
    private static final Integer RESERVED_BYTES_FOR_CHARSET = 50;

    @Override
    public byte getCode() {
        return MetaType.TYPE_STRING;
    }

    @Override
    public int getMaxLength() {
        return tdInteger.getMaxLength() + RESERVED_BYTES_FOR_CHARSET;
    }

    @Override
    public TypeDescriptorString load(final byte[] data, final int from) {
        Integer maxLength = tdInteger.load(data, from);
        String charsetName = tdString.load(data, tdInteger.getMaxLength());
        return new TypeDescriptorString(
                maxLength - TypeDescriptorString.LENGTH_OF_METADATA_IN_BYTES,
                Charset.forName(charsetName));
    }

    @Override
    public void save(final byte[] data, final int from,
            final Wrapper<TypeDescriptorString> wrapper) {
        System.arraycopy(wrapper.getBytes(), 0, data, from,
                wrapper.getBytes().length);
    }

    @Override
    public byte[] getBytes(final TypeDescriptorString value) {
        final byte[] out = new byte[getMaxLength()];
        tdInteger.save(out, 0, value.getMaxLength());
        tdString.save(out, tdInteger.getMaxLength(), value.getCharset().name());
        return out;
    }

    @Override
    public Class<TypeDescriptorString> getMetaTypeClass() {
        return TypeDescriptorString.class;
    }

    @Override
    public TypeDescriptorString getInstance() {
        return new TypeDescriptorString(1, Charset.defaultCharset());
    }

}
