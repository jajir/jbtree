package com.coroptis.jblinktree.performance.type;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.openjdk.jmh.runner.options.VerboseMode;
import org.openjdk.jmh.runner.options.WarmupMode;

import com.coroptis.jblinktree.store.CacheLru;

/**
 * Benchmark helps to optimize {@link CacheLru} class.
 * 
 * @author jajir
 *
 */
public class ComparingBytesAndValuesBenchmarkRunner {

    public static void main(String[] args) throws RunnerException {

        Options options = new OptionsBuilder()
                .include(ComparingBytesAndValuesBenchmark.class.getSimpleName())
                .mode(Mode.SingleShotTime).warmupMode(WarmupMode.INDI)
                .warmupBatchSize(1000 * 100).warmupIterations(1)
                .warmupTime(TimeValue.NONE).threads(1)
                .verbosity(VerboseMode.NORMAL).forks(1)
                .measurementIterations(10).measurementBatchSize(10 * 1000)
                .measurementTime(TimeValue.NONE).jvmArgsAppend("-server",
                        "-XX:+AggressiveOpts", "-dsa", "-Xbatch", "-Xmx1024m")
                .build();

        new Runner(options).run();
    }

}
