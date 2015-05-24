package com.coroptis.jblinktree.junit;

import junit.framework.TestCase;

import org.junit.Test;

import com.coroptis.jblinktree.IdGenerator;
import com.coroptis.jblinktree.IdGeneratorImpl;

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
 * Junit test of {@link IdGenerator}.
 * 
 * @author jajir
 * 
 */
public class IdGeneratorTest extends TestCase {

    @Test
    public void test_basic() throws Exception {
	IdGenerator idGenerator = new IdGeneratorImpl();

	assertEquals(0, idGenerator.getNextId());
    }

}
