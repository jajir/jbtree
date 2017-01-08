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
 * Factory instance for fixed length nodes.
 *
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public final class JbNodeBuilderFixedLength<K, V>
        extends AbstractJbNodeBuilder<K, V> {

    /**
     * Simple constructor.
     *
     * @param jbTreeData
     *            required tree data
     */
    public JbNodeBuilderFixedLength(final JbTreeData<K, V> jbTreeData) {
        super(jbTreeData);
    }

    @Override
    public <T> Node<K, T> makeNode(final Integer idNode, final byte[] field,
            final JbNodeDef<K, T> jbNodeDef) {
        return new NodeFixedLength<K, T>(idNode, field, jbNodeDef);
    }

    @Override
    public <T> Node<K, T> makeNode(final Integer nodeId,
            final boolean isLeafNode, final JbNodeDef<K, T> jbNodeDef) {
        return new NodeFixedLength<K, T>(nodeId, isLeafNode, jbNodeDef);
    }
}
