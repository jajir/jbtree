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
 * Tree visitor which count keys stored in tree.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
class JbTreeVisitorRecordCounter<K, V> implements JbTreeVisitor<K, V> {

    /**
     * Records counter.
     */
    private int count = 0;

    @Override
    public boolean visitedLeaf(final Node<K, V> node) {
        if (node.isLeafNode()) {
            count += node.getKeysCount();
        }
        return true;
    }

    @Override
    public boolean visitedNonLeaf(final Node<K, Integer> node) {
        if (node.isLeafNode()) {
            count += node.getKeysCount();
        }
        return true;
    }

    /**
     * return total number of keys.
     *
     * @return number of keys
     */
    public int getCount() {
        return count;
    }

}
