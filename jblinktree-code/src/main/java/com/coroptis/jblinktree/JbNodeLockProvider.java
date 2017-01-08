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
 * Class holds list of locks.
 * <p>
 * When some lock is unlocked there is no sure that will be anymore needed so
 * could be dropped from memory.
 * </p>
 * <p>
 * because inserting procedure itself prevent from accessing new node with lock
 * lock surrounding nodes put into {@link java.util.Map} doesn't have to be
 * thread safe.
 * </p>
 *
 * @author jajir
 *
 */
public interface JbNodeLockProvider {

    /**
     * Lock node. It's thread safe method.
     *
     * @param nodeId
     *            required node id
     */
    void lockNode(Integer nodeId);

    /**
     * Unlock node id. It's thread safe method.
     *
     * @param nodeId
     *            required node id
     */
    void unlockNode(Integer nodeId);

    /**
     * Count all locked nodes. It's useful for testing purposes.
     *
     * @return number of locked threads
     */
    int countLockedThreads();

    /**
     * It's called when node is unloaded from memory. In than case lock could be
     * removed from tree.
     *
     * @param nodeId
     *            required node id
     */
    void removeLock(Integer nodeId);

}
