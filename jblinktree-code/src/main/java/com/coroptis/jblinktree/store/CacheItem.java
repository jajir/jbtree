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
 * Class holds node data and information if data was changed.
 *
 * @author jajir
 *
 */
public final class CacheItem {

    /**
     * Is <code>true</code> when node was changed otherwise contains
     * <code>false</code>.
     */
    private boolean changed;

    /**
     * Node data.
     */
    private byte[] nodeData;

    /**
     * Simple protected constructor.
     *
     * @param data
     *            required node data
     * @param wasChanged
     *            initial value
     */
    private CacheItem(final byte[] data, final boolean wasChanged) {
        this.changed = wasChanged;
        this.nodeData = data;
    }

    /**
     * Static factory method. Created {@link CacheItem} is mark as unchanged.
     *
     * @param data
     *            required node data
     * @return created {@link CacheItem}
     */
    public static CacheItem make(final byte[] data) {
        return new CacheItem(data, false);
    }

    /**
     * Static factory method.
     *
     * @param data
     *            required node data
     * @param wasChanged
     *            required if node was changed information
     * @return created {@link CacheItem}
     */
    public static CacheItem make(final byte[] data, final boolean wasChanged) {
        return new CacheItem(data, wasChanged);
    }

    /**
     * @return the nodeData
     */
    public byte[] getNodeData() {
        return nodeData;
    }

    /**
     * @param data
     *            required node data
     */
    public void setNodeData(final byte[] data) {
        this.nodeData = data;
    }

    /**
     * @return the wasChanged
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @param wasChanged
     *            the wasChanged to set
     */
    public void setChanged(final boolean wasChanged) {
        this.changed = wasChanged;
    }

}
