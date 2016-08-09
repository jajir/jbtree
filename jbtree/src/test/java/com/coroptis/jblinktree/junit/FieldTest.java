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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Field;
import com.coroptis.jblinktree.FieldImpl;
import com.coroptis.jblinktree.NodeRule;

/**
 * Junit test for {@link FieldImpl}.
 * 
 * @author jajir
 * 
 */
public class FieldTest {

    private final Logger logger = LoggerFactory.getLogger(FieldTest.class);

    @Rule
    public NodeRule nr = new NodeRule(3);

    private Field<Integer, Integer> field;

    @Test
    public void test_default_field_value_0() throws Exception {
	assertEquals(Integer.valueOf(0), field.getValue(0));
    }

    @Test
    public void test_length_2() throws Exception {
	Field<Integer, Integer> f = nr
		.makeFieldFromArray(new Integer[] { 10, 1, 30 });

	assertEquals(1, f.getKeyCount());
    }

    @Test
    public void test_length_3() throws Exception {
	Field<Integer, Integer> f = nr
		.makeFieldFromArray(new Integer[] { 10, 1, 20, 2, 30 });

	assertEquals(2, f.getKeyCount());
    }

    @Test
    public void test_getKey() throws Exception {
	Field<Integer, Integer> f = nr
		.makeFieldFromArray(new Integer[] { 10, 1, 20, 2, 30 });

	assertEquals(Integer.valueOf(2), f.getKey(1));
	assertEquals(Integer.valueOf(20), f.getValue(1));
	assertEquals(Integer.valueOf(1), f.getKey(0));
	assertEquals(Integer.valueOf(10), f.getValue(0));
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
	assertEquals(
		"Field{field=[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]}",
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

    @Test
    public void test_equals_null() throws Exception {
	assertFalse(field.equals(null));
    }

    @Test
    public void test_equals_different() throws Exception {
	Field<Integer, Integer> f = new FieldImpl<Integer, Integer>(nr.getL(),
		nr.getTreeData().getLeafNodeDescriptor());
	f.setKey(1, -1);
	assertFalse(field.equals(f));
    }

    @Test
    public void test_equals_equals() throws Exception {
	final Field<Integer, Integer> f1 = new FieldImpl<Integer, Integer>(
		nr.getL(), nr.getTreeData().getLeafNodeDescriptor());
	f1.setKey(1, -1);

	final Field<Integer, Integer> f2 = new FieldImpl<Integer, Integer>(
		nr.getL(), nr.getTreeData().getLeafNodeDescriptor());
	f2.setKey(1, -1);

	assertTrue(f1.equals(f2));
    }

    @Test
    public void test_equals_same() throws Exception {
	assertTrue(field.equals(field));
    }

    @Test
    public void test_constructor_field_defensive_copy() throws Exception {
	byte[] field = new byte[] { 10, 1, 20, 2, 30 };

	Field<Integer, Integer> f = new FieldImpl<Integer, Integer>(field,
		nr.getTreeData().getLeafNodeDescriptor());

	field[0] = -1;
	field[1] = -1;
	field[2] = -1;
	field[3] = -1;
	field[4] = -1;

	assertEquals(10, f.getBytes()[0]);
	assertEquals(1, f.getBytes()[1]);
	assertEquals(20, f.getBytes()[2]);
	assertEquals(2, f.getBytes()[3]);
	assertEquals(30, f.getBytes()[4]);
    }

    @Before
    public void setUp() throws Exception {
	field = new FieldImpl<Integer, Integer>(nr.getL(), nr.getTreeData().getLeafNodeDescriptor());
    }

    @After
    public void tearDown() throws Exception {
	field = null;
    }

}
