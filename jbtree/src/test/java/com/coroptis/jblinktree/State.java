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

import com.google.common.base.MoreObjects;

/**
 * Example data type. State type is for testing purposes.
 * 
 * @author jajir
 *
 */
public class State {

    private final byte flags;

    private final byte tahNo;

    private State(final byte tahNo, final byte data) {
	this.tahNo = tahNo;
	this.flags = data;
    }

    /**
     * @return the data
     */
    public byte getFlags() {
	return flags;
    }

    public static State valueOf(final byte tahNo, final byte data) {
	return new State(tahNo, data);
    }

    /**
     * @return the tahNo
     */
    public byte getTahNo() {
	return tahNo;
    }

    @Override
    public String toString() {
	return MoreObjects.toStringHelper(State.class).add("tahNo", tahNo).add("flags", flags)
		.toString();
    }
}
