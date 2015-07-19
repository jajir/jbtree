package com.coroptis.jblinktree.type;

import com.coroptis.jblinktree.JblinktreeException;
import com.google.common.base.Preconditions;

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
 * Integer type descriptor.
 * 
 * @author jajir
 * 
 */
public class TypeDescriptorInteger implements TypeDescriptor<Integer> {

    @Override
    public int getMaxLength() {
	return 4;
    }

    @Override
    public void save(byte[] data, int from, Integer value) {
	int v = value.intValue();
	data[from] = (byte) ((v >>> 24) & 0xFF);
	data[from + 1] = (byte) ((v >>> 16) & 0xFF);
	data[from + 2] = (byte) ((v >>> 8) & 0xFF);
	data[from + 3] = (byte) ((v >>> 0) & 0xFF);
    }

    @Override
    public Integer load(byte[] data, int from) {
	return data[from] << 24 | (data[from + 1] & 0xFF) << 16 | (data[from + 2] & 0xFF) << 8
		| (data[from + 3] & 0xFF);
    }

    @Override
    public void verifyType(Object object) {
	Preconditions.checkNotNull(object);
	if (!(object instanceof Integer)) {
	    throw new JblinktreeException("Object of wrong type (" + object.getClass().getName()
		    + ")");
	}
    }

}
