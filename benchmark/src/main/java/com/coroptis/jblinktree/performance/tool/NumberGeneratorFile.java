package com.coroptis.jblinktree.performance.tool;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

import com.coroptis.jblinktree.JblinktreeException;
import com.google.common.base.Preconditions;

/**
 * Read numbers from given file. It allows to generate random numbers outside of
 * test.
 * 
 * @author jajir
 * 
 */
public class NumberGeneratorFile {

    private final BufferedReader bufferedReader;

    private final ReentrantLock lock = new ReentrantLock(false);

    public NumberGeneratorFile(final String fileName) {
	try {
	    File file = new File(Preconditions.checkNotNull(fileName));
	    if (!file.exists()) {
		throw new JblinktreeException(
			"File must exists. Current file is: "
				+ file.getAbsolutePath());
	    }
	    bufferedReader = new BufferedReader(new FileReader(file),
		    1000 * 1000);
	} catch (IOException e) {
	    throw new JblinktreeException(e.getMessage(), e);
	}
    }

    public Integer nextInt() {
	try {
	    lock.lock();
	    final String line = bufferedReader.readLine();
	    if (line == null) {
		throw new JblinktreeException("file is already closed.");
	    } else {
		return Integer.valueOf(line);
	    }
	} catch (IOException e) {
	    throw new JblinktreeException(e.getMessage(), e);
	} finally {
	    lock.unlock();
	}
    }

    void close() {
	try {
	    bufferedReader.close();
	} catch (IOException e) {
	    throw new JblinktreeException(e.getMessage(), e);
	}
    }

}
