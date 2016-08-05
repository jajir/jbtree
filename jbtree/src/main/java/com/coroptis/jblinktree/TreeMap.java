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

import java.util.Map;

/**
 * {@link Map} extension add Jbtree specific features.
 * 
 * @author jajir
 * 
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface TreeMap<K, V> extends Map<K, V> {

    /**
     * verify tree consistency. It's for testing purposes.
     */
    void verify();

    /**
     * TODO don't expose outside of implementation Traverse through entire tree
     * and visit all nodes.
     * 
     * @param treeVisitor
     *            required visitor implementation
     */
    void visit(JbTreeVisitor<K, V> treeVisitor);

    /**
     * Traverse through all tree key value pairs in tree.
     * 
     * @param dataVisitor
     *            required data visitor
     */
    void visit(JbDataVisitor<K, V> dataVisitor);

    /**
     * return number of nodes that are currently locked.
     * <p>
     * Method is thread safe.
     * </p>
     * 
     * @return number of locked nodes.
     */
    int countLockedNodes();
}
