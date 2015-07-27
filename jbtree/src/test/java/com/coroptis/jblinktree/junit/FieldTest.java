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
import com.coroptis.jblinktree.TreeUtil;
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

    private TypeDescriptor<Integer> intDescriptor;

    @Test
    public void test_default_field_value_0() throws Exception {
	assertEquals(Integer.valueOf(0), field.getValue(0));
    }

    @Test
    public void test_length_2() throws Exception {
	Field<Integer, Integer> f = TreeUtil.makeFromIntegerField(new Integer[] { 10, 1, 30 });

	assertEquals(3, f.getLength());
    }

    @Test
    public void test_length_3() throws Exception {
	Field<Integer, Integer> f = TreeUtil
		.makeFromIntegerField(new Integer[] { 10, 1, 20, 2, 30 });

	assertEquals(5, f.getLength());
    }

    @Test
    public void test_getKey() throws Exception {
	Field<Integer, Integer> f = TreeUtil
		.makeFromIntegerField(new Integer[] { 10, 1, 20, 2, 30 });

	assertEquals(Integer.valueOf(2), f.getKey(3));
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
	assertEquals("Field{field=[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}",
		field.toString());
    }

    @Test
    public void test_setLink_getLink() throws Exception {
	field.setLink(98);
	logger.debug(field.toString());

	assertEquals(Integer.valueOf(98), field.getLink());
    }

    @Test(expected = NullPointerException.class)
    public void test_setLink_null() throws Exception {
	field.setLink(null);
    }

    @Before
    public void setUp() throws Exception {
	intDescriptor = new TypeDescriptorInteger();
	field = new FieldImpl<Integer, Integer>(3, intDescriptor, intDescriptor, intDescriptor);
    }

    @After
    public void tearDown() throws Exception {
	intDescriptor = null;
	field = null;
    }

}
