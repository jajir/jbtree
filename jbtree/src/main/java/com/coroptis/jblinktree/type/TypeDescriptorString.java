package com.coroptis.jblinktree.type;

import java.nio.charset.Charset;

import com.coroptis.jblinktree.JblinktreeException;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

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
 * {@link String} type descriptor. When stored string is longer than available
 * space, than string is silently trim.
 * <p>
 * Note that number of chars that could be stored depends on charset.
 * <p>
 *
 * @author jajir
 *
 */
public final class TypeDescriptorString implements TypeDescriptor<String> {

    /**
     * In this bytes will be stored actual string length.
     */
    public static final int LENGTH_OF_METADATA_IN_BYTES = 4;

    /**
     * Maximum length of stored string.
     */
    private final int maxLength;

    /**
     * Charset used for converting from Java UTF-8 to byte array.
     */
    private final Charset charset;

    /**
     * Helps to store actual length of string in bytes.
     */
    private final TypeDescriptorInteger typeDescriptorInteger;

    /**
     *
     * @param maxBytes
     *            required maximum length of field in bytes
     * @param chset
     *            required {@link Charset}
     */
    public TypeDescriptorString(final int maxBytes, final Charset chset) {
        this.maxLength = maxBytes;
        this.charset = Preconditions.checkNotNull(chset);
        this.typeDescriptorInteger = new TypeDescriptorInteger();
    }

    @Override
    public int getMaxLength() {
        return maxLength + LENGTH_OF_METADATA_IN_BYTES;
    }

    @Override
    public void save(final byte[] data, final int from, final String value) {
        byte[] b = value.getBytes(charset);
        final Integer currentLength = Math.min(b.length, maxLength);
        typeDescriptorInteger.save(data, from, currentLength);
        System.arraycopy(b, 0, data,
                from + typeDescriptorInteger.getMaxLength(), currentLength);
    }

    @Override
    public String load(final byte[] data, final int from) {
        final Integer currentLength = typeDescriptorInteger.load(data, from);
        byte[] b = new byte[currentLength];
        System.arraycopy(data, from + typeDescriptorInteger.getMaxLength(), b,
                0, currentLength);
        return new String(b, charset);
    }

    @Override
    public void verifyType(final Object object) {
        Preconditions.checkNotNull(object);
        if (!(object instanceof String)) {
            throw new JblinktreeException("Object of wrong type ("
                    + object.getClass().getName() + ")");
        }
    }

    @Override
    public int compareValues(final String value1, final String value2) {
        return value1.compareTo(value2);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(TypeDescriptorString.class)
                .add("maxLength", getMaxLength()).toString();
    }

    /**
     * @return the charset
     */
    public Charset getCharset() {
        return charset;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + charset.hashCode();
        result = prime * result + maxLength;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TypeDescriptorString other = (TypeDescriptorString) obj;
        if (charset == null) {
            if (other.charset != null) {
                return false;
            }
        } else if (!charset.equals(other.charset)) {
            return false;
        }
        return maxLength == other.maxLength;
    }

    @Override
    public int cmp(final byte[] node, final int start,
            final Wrapper<String> wrapper) {
        byte[] value = wrapper.getBytes();
        final Integer currentLength = typeDescriptorInteger.load(value, 0);
        final int start2 = typeDescriptorInteger.getMaxLength() + start;
        for (int i = 0; i < currentLength; i++) {
            final int cmp = node[start2 + i]
                    - value[i + typeDescriptorInteger.getMaxLength()];
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    @Override
    public byte[] getBytes(final String value) {
        byte[] b = value.getBytes(charset);
        final Integer currentLength = Math.min(b.length, maxLength);
        byte[] out = new byte[currentLength
                + typeDescriptorInteger.getMaxLength()];
        typeDescriptorInteger.save(out, 0, currentLength);
        System.arraycopy(b, 0, out, typeDescriptorInteger.getMaxLength(),
                currentLength);
        return out;
    }

}
