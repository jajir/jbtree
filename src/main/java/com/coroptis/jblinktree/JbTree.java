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
 * Provide operations with tree. In tree could be stored key,value pairs.
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 * 
 */
public interface JbTree<K, V> {

    /**
     * Insert value and key into tree.
     * 
     * @param key
     *            required parameter key
     * @return previously associated value with given key.
     */
    Integer insert(K key, V value);

    /**
     * Remove key from tree. Associated value will be also removed.
     * 
     * @param key
     *            required key
     * @return return <code>true</code> when key was found and removed otherwise
     *         return <code>false</code>.
     */
    boolean remove(K key);

    /**
     * Find and return specific value for given key.
     * 
     * @param key
     *            required key
     * @return find value if there is any otherwise return <code>null</code>
     */
    V search(K key);

    /**
     * Count all keys stored in tree.
     * 
     * @return number of all keys in tree.
     */
    int countValues();

    /**
     * Inform about presence of key in tree.
     * 
     * @return if key is in tree return <code>true</code> otherwise return
     *         <code>false</code>.
     */
    boolean containsKey(K key);

    /**
     * Override {@link System#toString()} method.
     */
    String toString();

    /**
     * Verify that tree is consistent.
     */
    void verify();

    /**
     * return number of nodes that are currently locked.
     * <p>
     * Method is thread safe.
     * </p>
     * 
     * @return number of locked nodes.
     */
    int countLockedNodes();

    /**
     * Traverse through entire tree and visit all nodes.
     * 
     * @param treeVisitor
     *            required visitor implementation
     */
    void visit(JbTreeVisitor<K, V> treeVisitor);
}