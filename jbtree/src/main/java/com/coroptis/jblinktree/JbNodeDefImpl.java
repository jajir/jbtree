package com.coroptis.jblinktree;

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

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.google.common.base.Preconditions;

/**
 * Contain information about node. It's data types of key and value.
 * 
 * @author jajir
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 * 
 */
public class JbNodeDefImpl<K, V> implements JbNodeDef<K, V> {

    private final int l;

    private final TypeDescriptor<K> keyTypeDescriptor;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    public JbNodeDefImpl(final int l, final TypeDescriptor<K> keyTypeDescriptor,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.l = l;
	this.keyTypeDescriptor = Preconditions.checkNotNull(keyTypeDescriptor);
	this.valueTypeDescriptor = Preconditions
		.checkNotNull(valueTypeDescriptor);
	this.linkTypeDescriptor = Preconditions
		.checkNotNull(linkTypeDescriptor);
    }

    @Override
    public int getL() {
	return l;
    }

    @Override
    public TypeDescriptor<K> getKeyTypeDescriptor() {
	return keyTypeDescriptor;
    }

    @Override
    public TypeDescriptor<V> getValueTypeDescriptor() {
	return valueTypeDescriptor;
    }

    @Override
    public TypeDescriptor<Integer> getLinkTypeDescriptor() {
	return linkTypeDescriptor;
    }

    //TODO consider renaming record to field
    @Override
    public int getRecordMaxLength() {
	return getRecordActualLength(getL());
    }

    //TODO consider renaming record to field
    @Override
    public int getRecordActualLength(final int numberOfKeys) {
	return FLAGS_LENGTH + numberOfKeys * getKeyAndValueSize()
		+ getLinkTypeDescriptor().getMaxLength();
    }

    @Override
    public int getKeyAndValueSize() {
	return getKeyTypeDescriptor().getMaxLength()
		+ getValueTypeDescriptor().getMaxLength();
    }

    /**
     * Override {@link System#toString()} method.
     */
    @Override
    public String toString() {
	StringBuilder buff = new StringBuilder();
	buff.append("JbNodeDef{L=");
	buff.append(l);
	buff.append(", keyTypeDescriptor=");
	buff.append(getKeyTypeDescriptor());
	buff.append(", valueTypeDescriptor=");
	buff.append(getValueTypeDescriptor());
	buff.append(", linkTypeDescriptor=");
	buff.append(getLinkTypeDescriptor());
	buff.append("}");
	return buff.toString();
    }

}
