package com.coroptis.jblinktree.util;

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

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * Simple implementations using field size. When field filled with values than
 * filed size is increased.
 *
 * @author jajir
 *
 */
public final class JbStackArrayList implements JbStack {

    /**
     * Initial capacity of stack.
     */
    public static final int INITIAL_FIELD_CAPACITY = 20;

    /**
     * How fast grow stack when reach it's capacity.
     */
    private static final int FIELD_GROW_SIZE = 5;

    /**
     * Stack field.
     */
    private Integer[] field;

    /**
     * index of last inserted value in field.
     */
    private int lastOne;

    /**
     * Default constructor.
     */
    public JbStackArrayList() {
        field = new Integer[INITIAL_FIELD_CAPACITY];
        lastOne = 0;
    }

    @Override
    public Integer pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        } else {
            return field[--lastOne];
        }
    }

    @Override
    public void push(final Integer item) {
        if (field.length <= lastOne) {
            field = Arrays.copyOf(field, field.length + FIELD_GROW_SIZE);
        }
        field[lastOne++] = item;
    }

    @Override
    public boolean isEmpty() {
        return lastOne == 0;
    }

}
