package com.coroptis.jblinktree.store;

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
 * Class persist tree metadata. Metadata are stored in following order:
 * <ul>
 * <li>file description</li>
 * <li>root node id</li>
 * <li>key data type description</li>
 * <li>value data type description</li>
 * <li>link data type description</li>
 * </ul>
 *
 * @author jajir
 */
public interface MetaDataStore {

    /**
     * Write all meta data to file and close file.
     */
    void close();

}