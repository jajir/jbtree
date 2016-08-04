package com.coroptis.jblinktree.type;

import java.io.Serializable;
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
 * {@link String} type descriptor.
 * 
 * FIXME current all chars are stored in 1 byte, it's not true in case UTF-8
 * 
 * @author jajir
 * 
 */
public class TypeDescriptorString implements Serializable,
	TypeDescriptor<String> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final int maxLength;

    private transient final Charset charset;

    private final TypeDescriptorInteger typeDescriptorInteger;

    public TypeDescriptorString(final int maxBytes, final Charset charset) {
	this.maxLength = maxBytes;
	this.charset = charset;
	this.typeDescriptorInteger = new TypeDescriptorInteger();
    }

    @Override
    public int getMaxLength() {
	return maxLength + 4;
    }

    @Override
    public void save(final byte[] data, final int from, final String value) {
	byte b[] = value.getBytes(charset);
	final Integer currentLength = Math.min(b.length, maxLength);
	typeDescriptorInteger.save(data, from, currentLength);
	System.arraycopy(b, 0, data,
		from + typeDescriptorInteger.getMaxLength(), currentLength);
    }

    @Override
    public String load(final byte[] data, final int from) {
	final Integer currentLength = typeDescriptorInteger.load(data, from);
	byte b[] = new byte[currentLength];
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
    public int compare(final String value1, final String value2) {
	return value1.compareTo(value2);
    }

    @Override
    public String toString() {
	return MoreObjects.toStringHelper(TypeDescriptorString.class).add("maxLength", getMaxLength())
		.toString();
    }

}
