package com.coroptis.jblinktree.performance.tool;

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
