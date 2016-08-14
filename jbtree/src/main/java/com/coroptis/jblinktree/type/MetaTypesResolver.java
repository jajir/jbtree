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

import java.util.ArrayList;
import java.util.List;

import com.coroptis.jblinktree.JblinktreeException;

public class MetaTypesResolver {

    static {
        MetaTypesResolver metaTypesResolver = MetaTypesResolver.getInstance();
        metaTypesResolver.addMetaType(new MetaTypeByte());
        metaTypesResolver.addMetaType(new MetaTypeString());
        metaTypesResolver.addMetaType(new MetaTypeInteger());
    }

    private static MetaTypesResolver instance;

    private final List<MetaType> metaTypes;

    private MetaTypesResolver() {
        metaTypes = new ArrayList<MetaType>();
    }

    public static MetaTypesResolver getInstance() {
        if (instance == null) {
            instance = new MetaTypesResolver();
        }
        return instance;
    }

    public void addMetaType(final MetaType metaType) {
        metaTypes.add(metaType);
    }

    public <S> AbstractTypeDescriptorMetaData<TypeDescriptor<S>> resolve(
            byte code) {
        for (final MetaType metaType : metaTypes) {
            if (code == metaType.getCode()) {
                return (AbstractTypeDescriptorMetaData<TypeDescriptor<S>>) metaType;
            }
        }
        throw new JblinktreeException(
                "Unable to find type for code '" + code + "'");
    }

    public <S> AbstractTypeDescriptorMetaData<TypeDescriptor<S>> resolve(
            Class<?> clazz) {
        for (final MetaType metaType : metaTypes) {
            if (clazz.equals(metaType.getMetaTypeClass())) {
                return (AbstractTypeDescriptorMetaData<TypeDescriptor<S>>) metaType;
            }
        }
        throw new JblinktreeException(
                "Unable to find type for class '" + clazz.getName() + "'");
    }

}
