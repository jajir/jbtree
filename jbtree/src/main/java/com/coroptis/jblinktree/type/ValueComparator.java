package com.coroptis.jblinktree.type;

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
 * Define method for comparing values.
 *
 * @author jajir
 *
 * @param <T>
 *            type which should be compared
 */
public interface ValueComparator<T> {

    /**
     * Compare values. Same definition as {@link java.util.Comparator}.
     *
     * @param o1
     *            required first value
     * @param o2
     *            required second value
     * @return comparing result
     */
    int compareValues(T o1, T o2);
}
