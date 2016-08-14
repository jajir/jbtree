package com.coroptis.jblinktree.type;

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

import java.io.Serializable;

public abstract class AbstractTypeDescriptorMetaData<T>
        implements Serializable, TypeDescriptor<T>, MetaType {

    @Override
    public int compare(T o1, T o2) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meda tada about metadata.");
    }

    @Override
    public int getMaxLength() {
        return 0;
    }

    @Override
    public void save(byte[] data, int from, T value) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meta data about metadata.");
    }

    @Override
    public T load(byte[] data, int from) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meda tada about metadata.");
    }

    @Override
    public void verifyType(Object object) {
        throw new UnsupportedOperationException(
                "It's not alowed to store meda tada about metadata.");
    }

}
