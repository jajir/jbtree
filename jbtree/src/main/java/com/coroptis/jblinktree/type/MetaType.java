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
 * Information about data types have to be stored. This class define meta data
 * about data type.
 *
 * @author jajir
 *
 */
public interface MetaType {

    /**
     * Integer type id.
     */
    byte TYPE_INTEGER = 0;

    /**
     * String type id.
     */
    byte TYPE_STRING = 2;

    /**
     * Byte type id.
     */
    byte TYPE_BYTE = 3;

    /**
     * Return type unique data type code. It allows to detect which data type is
     * stored.
     *
     * @return byte data type code
     */
    byte getCode();

    /**
     * Return class that is described.
     *
     *
     * TODO ? should be parameterized
     *
     *
     * @return described class
     */
    Class<?> getMetaTypeClass();

    /**
     * Return data type instance.
     *
     * TODO should be done in same factory. Instance can't be always created
     * without params.
     *
     * TODO &gt;S&lt; should be parameter of class
     *
     * @return data type instance
     * @param <S>
     *            data type
     */
    <S> S getInstance();

}
