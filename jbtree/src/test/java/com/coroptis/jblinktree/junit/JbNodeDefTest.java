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

import static org.junit.Assert.*;

import java.nio.charset.Charset;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.coroptis.jblinktree.JbNodeDef;
import com.coroptis.jblinktree.JbNodeDefImpl;
import com.coroptis.jblinktree.type.TypeDescriptor;
import com.coroptis.jblinktree.type.TypeDescriptorInteger;
import com.coroptis.jblinktree.type.TypeDescriptorString;

public class JbNodeDefTest {

    JbNodeDef<String, String> nd;
    
    @Test
    public void test_typeDescriptor_length() throws Exception {
	assertEquals(21, nd.getKeyTypeDescriptor().getMaxLength());
	assertEquals(15, nd.getValueTypeDescriptor().getMaxLength());
	assertEquals(4, nd.getLinkTypeDescriptor().getMaxLength());
	assertEquals(5, nd.getL());
    }
    
    @Test
    public void test_getFieldMaxLength() throws Exception {
	assertEquals(185, nd.getFieldMaxLength());	
    }
    
    @Test
    public void test_getFieldActualLength() throws Exception {
	assertEquals(113, nd.getFieldActualLength(3));		
    }
    
    @Test
    public void test_getKeyPosition() throws Exception {
	assertEquals(124, nd.getKeyPosition(3));		
    }
    
    @Test
    public void test_getValuePosition() throws Exception {
	assertEquals(109, nd.getValuePosition(3));		
    }
    
    @Test
    public void test_getKeyAndValueSize() throws Exception {
	assertEquals(36, nd.getKeyAndValueSize());		
    }
    
    @Before
    public void setup(){
	TypeDescriptor<String> keyTypeDescriptor = new TypeDescriptorString(17,Charset.forName("ISO-8859-1"));
	TypeDescriptor<String> valueTypeDescriptor = new TypeDescriptorString(11,Charset.forName("ISO-8859-1"));
	TypeDescriptor<Integer> linkTypeDescriptor = new TypeDescriptorInteger();
	nd = new JbNodeDefImpl<String, String>(5, keyTypeDescriptor, valueTypeDescriptor, linkTypeDescriptor);
    }

    @After
    public void tearDown(){
	nd = null;
    }
    
}
