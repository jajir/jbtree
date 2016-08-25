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

/**
 * Helps convert byte code to specific typeDescriptor.
 *
 * @author jajir
 *
 */
public final class MetaTypesResolver {

    static {
        MetaTypesResolver metaTypesResolver = MetaTypesResolver.getInstance();
        metaTypesResolver.addMetaType(new MetaTypeByte());
        metaTypesResolver.addMetaType(new MetaTypeString());
        metaTypesResolver.addMetaType(new MetaTypeInteger());
    }

    /**
     * Singleton instance.
     */
    private static MetaTypesResolver instance;

    /**
     * List with meta types.
     */
    private final List<MetaType<?>> metaTypes;

    /**
     * Private constructor.
     */
    private MetaTypesResolver() {
        metaTypes = new ArrayList<MetaType<?>>();
    }

    /**
     * Return singleton instance.
     *
     * @return return MetaTypesResolver instance
     */
    public static MetaTypesResolver getInstance() {
        if (instance == null) {
            instance = new MetaTypesResolver();
        }
        return instance;
    }

    /**
     * Allows to add meta data type.
     *
     * @param metaType
     *            required meta type
     */
    public void addMetaType(final MetaType<?> metaType) {
        metaTypes.add(metaType);
    }

    /**
     * for data type identification code find meta type.
     *
     * @param code
     *            data type identification code
     * @return meta type descriptor
     * @param <T>
     *            type described by {@link TypeDescriptor}.
     */
    @SuppressWarnings("unchecked")
    public <T> AbstractMetaType<TypeDescriptor<T>> resolve(final byte code) {
        for (final MetaType<?> metaType : metaTypes) {
            if (code == metaType.getCode()) {
                return (AbstractMetaType<TypeDescriptor<T>>) metaType;
            }
        }
        throw new JblinktreeException(
                "Unable to find type for code '" + code + "'");
    }

    /**
     * For given {@link TypeDescriptor} find meta descriptor.
     *
     * @param clazz
     *            dataTypeDescriptor class
     * @return meta type descriptor
     * @param <T>
     *            type described by {@link TypeDescriptor}.
     */
    @SuppressWarnings("unchecked")
    public <T> AbstractMetaType<TypeDescriptor<T>> resolve(
            final Class<?> clazz) {
        for (final MetaType<?> metaType : metaTypes) {
            if (clazz.equals(metaType.getMetaTypeClass())) {
                return (AbstractMetaType<TypeDescriptor<T>>) metaType;
            }
        }
        throw new JblinktreeException(
                "Unable to find type for class '" + clazz.getName() + "'");
    }

}
