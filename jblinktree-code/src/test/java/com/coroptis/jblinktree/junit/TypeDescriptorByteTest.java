package com.coroptis.jblinktree.junit;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.type.TypeDescriptorByte;

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
 * Verify byte test.
 * 
 * @author jajir
 *
 */
public class TypeDescriptorByteTest {

	private TypeDescriptorByte tdb;

	@Test
	public void test_toString() throws Exception {
		assertEquals("TypeDescriptorByte{maxLength=1}", tdb.toString());
	}

	@Before
	public void setup() {
		tdb = new TypeDescriptorByte();
	}

	@After
	public void tearDown() {
		tdb = null;
	}

}
