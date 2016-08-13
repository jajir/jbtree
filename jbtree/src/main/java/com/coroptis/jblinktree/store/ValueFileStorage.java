package com.coroptis.jblinktree.store;

import com.coroptis.jblinktree.Node;

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
 * Simple storing values to special file. Class doesn't use caching. Class is
 * not thread safe.
 *
 * @author jajir
 *
 * @param <V>
 *            value type
 */
public interface ValueFileStorage<K, V> {

    /**
     * Value File Name file extension.
     */
    String FILE_NAME_SUFFIX = "vfs";

    void storeValues(Node<K, V> node);

    Node<K, V> loadValues(Node<K, V> node);

    void close();

}
