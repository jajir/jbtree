package com.coroptis.jblinktree.junit;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

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
 * Junit test for {@link TypeDescriptorInteger}.
 * 
 * @author jan
 * 
 */
public class TypeDescriptorIntegerTest {

    TypeDescriptor<Integer> td;

    @Test
    public void test_save() throws Exception {
	byte[] b = new byte[6];
	td.save(b, 1, -1);

	assertEquals(0, b[0]);
	assertEquals(-1, b[1]);
	assertEquals(-1, b[2]);
	assertEquals(-1, b[3]);
	assertEquals(-1, b[4]);
	assertEquals(0, b[5]);
    }

    @Test
    public void test_load() throws Exception {
	byte[] b = new byte[6];
	b[1] = 1;
	b[2] = 2;
	b[3] = 3;
	b[4] = 4;

	assertEquals(Integer.valueOf(16909060), td.load(b, 1));
    }

    @Test
    public void test_verifyType_pass() throws Exception {
	td.verifyType(-4);
	td.verifyType(Integer.MAX_VALUE);
    }

    @Test(expected = JblinktreeException.class)
    public void test_verifyType_false() throws Exception {
	td.verifyType("blee");
    }

    @Before
    public void setup() {
	td = new TypeDescriptorInteger();
    }

    @After
    public void tearDown() {
	td = null;
    }

}
