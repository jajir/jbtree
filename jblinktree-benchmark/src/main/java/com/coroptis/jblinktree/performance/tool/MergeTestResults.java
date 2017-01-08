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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

import com.google.common.base.Preconditions;

public class MergeTestResults {

    private final File parentDir;
    private final BufferedWriter writer;

    public MergeTestResults(final String parentDirName) throws IOException {
	this.parentDir = new File(parentDirName);
	Preconditions.checkArgument(parentDir.isDirectory());
	Preconditions.checkArgument(parentDir.exists());
	writer = new BufferedWriter(new FileWriter(parentDir + File.separator
		+ "merged-test-results.csv", false));
	writer.write("\"Benchmark\",\"Mode\",\"Threads\",\"Samples\",\"Score\",\"Score Error (99,9%)\",\"Unit\"");
	writer.newLine();
    }

    public void merge() throws IOException {
	for (final File particularCvs : parentDir
		.listFiles(new FilenameFilter() {

		    @Override
		    public boolean accept(final File dir, final String name) {
			return name.startsWith("result-");
		    }
		})) {
	    merge(particularCvs);
	    System.out.println(particularCvs);
	}
	writer.close();
    }

    private void merge(final File fileToMerge) throws IOException {
	BufferedReader reader = new BufferedReader(new FileReader(fileToMerge));
	String line = null;
	reader.readLine();
	while ((line = reader.readLine()) != null) {
	    writer.write(line);
	    writer.newLine();
	}
	reader.close();
    }

    public static void main(String[] args) throws Exception {
	new MergeTestResults("./target/").merge();
    }

}
