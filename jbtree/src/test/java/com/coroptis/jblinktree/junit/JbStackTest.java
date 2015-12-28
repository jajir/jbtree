package com.coroptis.jblinktree.junit;

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

	for (int i = maxSize-1; i >= 0; i--) {
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
