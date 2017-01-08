package com.coroptis.jblinktree.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.charset.Charset;

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

import org.junit.Test;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.TypeDescriptorString;
import com.coroptis.jblinktree.type.Wrapper;

/**
 * {@link Wrapper} test.
 *
 * @author jajir
 *
 */
public class WrapperTest {

	@Test
	public void test_toString_integer_23() {
		TypeDescriptor<Integer> tdi = new TypeDescriptorInteger();
		Wrapper<Integer> w = Wrapper.make(23, tdi);

		assertNotNull(w);
		assertEquals("Wrapper{value=23}", w.toString());
	}

	@Test
	public void test_toString_string_ahoj() {
		TypeDescriptorString td = new TypeDescriptorString(10, Charset.forName("UTF-8"));
		Wrapper<String> w = Wrapper.make("ahoj", td);

		assertNotNull(w);
		assertEquals("Wrapper{value=ahoj}", w.toString());
	}

}
