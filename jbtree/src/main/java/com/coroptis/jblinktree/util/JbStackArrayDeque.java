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

import java.util.ArrayDeque;

/**
 * This is simplest {@link JbStack} implementation based on {@link ArrayDeque}
 * class.
 * <p>
 * It's not thread safe.
 * </p>
 * 
 * TODO JH - piece of shit - slow, field, linked list
 * 
 * @author jajir
 * 
 */
public class JbStackArrayDeque implements JbStack {

    private final ArrayDeque<Integer> deque;

    public JbStackArrayDeque() {
	deque = new ArrayDeque<Integer>();
    }

    @Override
    public Integer pop() {
	return deque.pop();
    }

    @Override
    public void push(final Integer item) {
	deque.push(item);
    }

    @Override
    public boolean isEmpty() {
	return deque.isEmpty();
    }

}
