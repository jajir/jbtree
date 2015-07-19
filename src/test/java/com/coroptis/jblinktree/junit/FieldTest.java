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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Field;
import com.coroptis.jblinktree.FieldImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;

/**
 * Junit test for {@link FieldImpl}.
 * 
 * @author jajir
 * 
 */
public class FieldTest {

    private final Logger logger = LoggerFactory.getLogger(FieldTest.class);

    private Field<Integer, Integer> field;

    private TypeDescriptor intDescriptor;

    @Test
    public void test_default_field_value_0() throws Exception {
	assertEquals(Integer.valueOf(0), field.getValue(0));
    }

    @Test(expected = NullPointerException.class)
    public void test_save_null() throws Exception {
	field.set(2, null);
    }

    @Test
    public void test_save() throws Exception {
	field.set(2, -40);
	assertEquals(Integer.valueOf(-40), field.get(2));
    }

    @Test
    public void test_length_2() throws Exception {
	FieldImpl<Integer, Integer> f = new FieldImpl<Integer, Integer>(
		new Integer[] { 10, 1, 30 }, intDescriptor, intDescriptor);

	assertEquals(3, f.getLength());
    }

    @Test
    public void test_length_3() throws Exception {
	FieldImpl<Integer, Integer> f = new FieldImpl<Integer, Integer>(new Integer[] { 10, 1, 20,
		2, 30 }, intDescriptor, intDescriptor);

	assertEquals(5, f.getLength());
    }

    @Test
    public void test_getKey() throws Exception {
	FieldImpl<Integer, Integer> f = new FieldImpl<Integer, Integer>(new Integer[] { 10, 1, 20,
		2, 30 }, intDescriptor, intDescriptor);

	assertEquals(Integer.valueOf(2), f.getKey(3));
    }

    @Test
    public void test_get() throws Exception {
	FieldImpl<Integer, Integer> f = new FieldImpl<Integer, Integer>(new Integer[] { 10, 1, 20,
		2, 30 }, intDescriptor, intDescriptor);

	assertEquals(Integer.valueOf(10), f.get(0));
	assertEquals(Integer.valueOf(1), f.get(1));
	assertEquals(Integer.valueOf(20), f.get(2));
	assertEquals(Integer.valueOf(2), f.get(3));
    }

    @Test
    public void test_setKey() throws Exception {
	field.setKey(1, -3);

	assertEquals(Integer.valueOf(-3), field.getKey(1));
    }

    @Test(expected = NullPointerException.class)
    public void test_setKey_nullPointerException() {
	field.setKey(1, null);
    }

    @Test(expected = NullPointerException.class)
    public void test_setValue_nullPointerException() {
	field.setKey(2, null);
    }

    @Test
    public void test_toString() throws Exception {
	logger.debug(field.toString());
	assertEquals("Field{field=[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}", field.toString());
    }

    @Before
    public void setUp() throws Exception {
	intDescriptor = new TypeDescriptorInteger();
	field = new FieldImpl<Integer, Integer>(3, intDescriptor, intDescriptor);
    }

    @After
    public void tearDown() throws Exception {
	intDescriptor = null;
	field = null;
    }

}
