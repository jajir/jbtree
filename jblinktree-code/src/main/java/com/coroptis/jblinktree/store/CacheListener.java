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

import com.coroptis.jblinktree.Node;

/**
 * Allows to detect when node is no longer needed in cache and also when it's
 * needed back.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface CacheListener<K, V> {

    /**
     * Called when node is no longer needed in cache. Parameter wasChanges helps
     * to control node persisting.
     *
     * @param node
     *            required node
     * @param wasChanged
     *            it's <code>true</code> when object was changed in cache in
     *            that case have to be persisted
     */
    void onUnload(Node<K, V> node, boolean wasChanged);

}
