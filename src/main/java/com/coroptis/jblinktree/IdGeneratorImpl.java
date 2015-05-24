package com.coroptis.jblinktree;

import java.util.concurrent.locks.ReentrantLock;

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
 * Implementation of {@link IdGenerator}.
 * 
 * @author jajir
 * 
 */
public class IdGeneratorImpl implements IdGenerator {

    private int nextId;

    private final ReentrantLock lock = new ReentrantLock(false);

    public IdGeneratorImpl() {
	nextId = 0;
    }

    @Override
    public int getNextId() {
	lock.lock();
	int out = nextId++;
	lock.unlock();
	return out;
    }

}
