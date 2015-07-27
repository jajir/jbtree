package com.coroptis.jblinktree.junit;

import static org.junit.Assert.assertEquals;

import java.nio.charset.Charset;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coroptis.jblinktree.type.TypeDescriptorString;

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
 * Junit test for {@link TypeDescriptorString}.
 * 
 * @author jan
 * 
 */
public class TypeDescriptorStringTest {

    private final Logger logger = LoggerFactory.getLogger(TypeDescriptorStringTest.class);

    @Test
    public void test_store_short() throws Exception {
	TypeDescriptorString td = new TypeDescriptorString(10, Charset.forName("UTF-8"));
	byte b[] = new byte[100];
	td.save(b, 10, "Ahoj");
	String word = td.load(b, 10);
	logger.debug(toString(b));

	logger.debug("-->" + word + "<--");

	assertEquals("Ahoj", word);
    }

    @Test
    public void test_store() throws Exception {
	TypeDescriptorString td = new TypeDescriptorString(10, Charset.forName("UTF-8"));
	byte b[] = new byte[100];
	td.save(b, 10, "Ahoj lidi!");
	logger.debug(toString(b));

	assertEquals("Ahoj lidi!", td.load(b, 10));
    }

    @Test
    public void test_store_too_long() throws Exception {
	TypeDescriptorString td = new TypeDescriptorString(10, Charset.forName("UTF-8"));
	byte b[] = new byte[100];
	td.save(b, 10, "Ahoj lidi! Ahoj lidi! brekeke");
	logger.debug(toString(b));

	assertEquals("Ahoj lidi!", td.load(b, 10));
    }

    private String toString(byte b[]) {
	StringBuilder buff = new StringBuilder();
	buff.append("[");
	for (int i = 0; i < b.length; i++) {
	    if (i != 0) {
		buff.append(", ");
	    }
	    buff.append(b[i]);
	}
	buff.append("]");
	return buff.toString();
    }

}
