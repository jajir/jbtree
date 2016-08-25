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
 * @param <T>
 *            type descriptor which is defined by extended class
 */
public interface MetaType<T> {

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
     * @return described class
     */
    Class<T> getMetaTypeClass();

    /**
     * Return data type instance.
     *
     * @return data type instance
     */
    T getInstance();

}
