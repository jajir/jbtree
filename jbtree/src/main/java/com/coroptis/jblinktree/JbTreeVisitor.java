package com.coroptis.jblinktree;

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
 * Interface used for traversing through tree. Visiting process is not thread
 * safe.
 * 
 * @author jajir
 */
public interface JbTreeVisitor<K, V> {

    /**
     * When particular leaf node is visited this method is called.
     * 
     * @param node
     *            required visited leaf node
     * @return when it's <code>true</code> traversing will try to find next
     *         node, when it's <code>false</code> traversing immediately stops.
     */
    boolean visitedLeaf(Node<K, V> node);

    /**
     * When particular non-leaf node is visited this method is called.
     * 
     * @param node
     *            required visited non-leaf node
     * @return when it's <code>true</code> traversing will try to find next
     *         node, when it's <code>false</code> traversing immediately stops.
     */
    boolean visitedNonLeaf(Node<K, Integer> node);

}
