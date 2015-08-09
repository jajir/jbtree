package com.coroptis.jblinktree.performance;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

public class MapTestRunner {

    private static Options getOptions(final Class<?> clazz) {
	return new OptionsBuilder().include(clazz.getSimpleName()).warmupIterations(0).threads(100)
		.measurementIterations(10).verbosity(VerboseMode.NORMAL).forks(1).build();
    }

    public static void main(String[] args) throws RunnerException {
	// new Runner(getOptions(MapTestJbTreeMap.class)).run();
	new Runner(getOptions(MapTestConcurrentHashMap.class)).run();
	new Runner(getOptions(MapTestSynchronizedHashMap.class)).run();
	new Runner(getOptions(MapTestSynchronizedTreeMap.class)).run();
    }

}
