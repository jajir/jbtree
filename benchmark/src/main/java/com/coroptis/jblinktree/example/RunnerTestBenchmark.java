package com.coroptis.jblinktree.example;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.openjdk.jmh.runner.options.WarmupMode;

/**
 * Runner that allows start simplified micro benchmark. Class allows to verify
 * correct test parameters.
 * 
 * @author jajir
 *
 */
public class RunnerTestBenchmark {


    public static void main(String[] args) throws RunnerException {
	
	Options options = new OptionsBuilder()
		.include(ExampleBenchmark.class.getSimpleName())
		.mode(Mode.SingleShotTime)
		.warmupMode(WarmupMode.INDI)
		.warmupBatchSize(7)
		.warmupIterations(1)
		.warmupTime(TimeValue.NONE)
		.threads(1)
		.verbosity(VerboseMode.NORMAL)
		.forks(1)
		.measurementIterations(10)
		.measurementBatchSize(100)
		.measurementTime(TimeValue.NONE)
		.jvmArgsAppend("-server", "-XX:+AggressiveOpts", "-dsa", "-Xbatch", "-Xmx1024m")
		.build();
	
	new Runner(options).run();
    }

}
