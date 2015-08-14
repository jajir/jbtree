package com.coroptis.jblinktree.performance.tool;

/**
 * Class generate list of randomly ordered numbers. Data will be used for
 * benchmarks.
 * 
 * @author jajir
 * 
 */
public class GenerateRandomUniqueNumbers {

    public static void main(final String[] args) throws Exception {
	final GenerateNumbers gn = new GenerateNumbers(1000, 1000 * 1000 * 100);
	gn.writeTofile("src/data/numbers1.txt");
    }

}
