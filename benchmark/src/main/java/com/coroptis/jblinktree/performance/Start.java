package com.coroptis.jblinktree.performance;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class Start {
    
    public static void main(String... args) throws RunnerException {
	  Options opts = new OptionsBuilder()
	    .include(".*.GoodBench.*")
	    .warmupIterations(20)
	    .measurementIterations(5)
	    .measurementTime(TimeValue.milliseconds(3000))
	    .jvmArgsPrepend("-server")
	    .forks(3)
	    .build();
	  new Runner(opts).run();}
    
}
