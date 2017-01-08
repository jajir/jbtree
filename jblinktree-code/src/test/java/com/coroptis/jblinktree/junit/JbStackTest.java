package com.coroptis.jblinktree.junit;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.util.JbStack;
import com.coroptis.jblinktree.util.JbStackArrayList;

public class JbStackTest {

    private JbStack stack;

    @Test
    public void test_operations() throws Exception {
        assertTrue(stack.isEmpty());

        stack.push(8);
        stack.push(4);
        stack.push(-90);

        assertEquals(Integer.valueOf(-90), stack.pop());
        assertEquals(Integer.valueOf(4), stack.pop());
        assertEquals(Integer.valueOf(8), stack.pop());

        assertTrue(stack.isEmpty());
    }

    @Test
    public void test_moreElements() throws Exception {
        final int maxSize = JbStackArrayList.INITIAL_FIELD_CAPACITY + 10;
        assertTrue(stack.isEmpty());

        for (int i = 0; i < maxSize; i++) {
            stack.push(i);
        }

        for (int i = maxSize - 1; i >= 0; i--) {
            assertEquals(Integer.valueOf(i), stack.pop());
        }

        assertTrue(stack.isEmpty());
    }

    @Before
    public void setUp() throws Exception {
        stack = new JbStackArrayList();
    }

    @After
    public void tearDown() throws Exception {
        stack = null;
    }
}
