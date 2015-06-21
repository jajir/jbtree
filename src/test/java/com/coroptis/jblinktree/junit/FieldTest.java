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

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.Field;

/**
 * Junit test for {@link Field}.
 * 
 * @author jajir
 * 
 */
public class FieldTest extends TestCase {

    private final Logger logger = LoggerFactory.getLogger(FieldTest.class);

    private Field field;

    @Test
    public void test_default_field_value_null() throws Exception {
	assertNull(field.get(0));
    }

    @Test
    public void test_save_null() throws Exception {
	field.set(2, -40);
	assertEquals(Integer.valueOf(-40), field.get(2));
	field.set(2, null);
	assertNull(field.get(2));
    }

    @Test
    public void test_toString() throws Exception {
	logger.debug(field.toString());
	assertEquals("Field{field=[-128, 0, 0, 0, -128, 0, 0, 0, -128, 0, 0, 0]}", field.toString());
    }

    @Override
    protected void setUp() throws Exception {
	super.setUp();
	field = new Field(3);
    }

    @Override
    protected void tearDown() throws Exception {
	field = null;
	super.tearDown();
    }

}
