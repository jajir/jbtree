package com.coroptis.jblinktree.type;

import java.nio.charset.Charset;

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
 * Allows to set correct data type in tree creating DSL.
 *
 * @author jajir
 *
 */
public final class Types {

    /**
     * Hidden constructor.
     */
    private Types() {
    }

    /**
     * Create Integer type descriptor.
     *
     * @return integer type descriptor
     */
    public static TypeDescriptor<Integer> integer() {
        return new TypeDescriptorInteger();
    }

    /**
     * Create String type descriptor.
     *
     * @param length
     *            required maximum length of string
     * @return String type descriptor
     */
    public static TypeDescriptor<String> string(final int length) {
        return new TypeDescriptorString(length, Charset.forName("ISO_8859_1"));
    }

    /**
     * Create byte type descriptor.
     *
     * @return byte type descriptor
     */
    public static TypeDescriptor<Byte> byteType() {
        return new TypeDescriptorByte();
    }

}
