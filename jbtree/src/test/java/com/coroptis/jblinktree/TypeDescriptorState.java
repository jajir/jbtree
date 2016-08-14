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

import com.coroptis.jblinktree.JblinktreeException;
import com.coroptis.jblinktree.type.TypeDescriptor;

/**
 * Type descriptor that allows to create tests with more complex data types.
 *
 *
 * @author jajir
 *
 */
public class TypeDescriptorState implements TypeDescriptor<State> {

    @Override
    public int compareValues(State o1, State o2) {
        return o2.getFlags() - o1.getFlags();
    }

    @Override
    public int getMaxLength() {
        return 4;
    }

    @Override
    public void save(byte[] data, int from, State state) {
        data[from + 0] = state.getTahNo();
        data[from + 1] = state.getFlags();
    }

    @Override
    public State load(byte[] data, int from) {
        return State.valueOf(data[from + 0], data[from + 1]);
    }

    @Override
    public void verifyType(Object object) {
        if (!(object instanceof State)) {
            throw new JblinktreeException("Object of wrong type ("
                    + object.getClass().getName() + ")");
        }
    }

}
