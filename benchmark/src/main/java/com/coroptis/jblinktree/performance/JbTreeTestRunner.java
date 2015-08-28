package com.coroptis.jblinktree.performance;

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

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.VerboseMode;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.type.Types;

/**
 * This test starts just jbtree performance test. It's useful for real time jvm
 * monitoring.
 * 
 * @author jajir
 *
 */
public class JbTreeTestRunner {

    private static void test() {
	final Random random = new Random();
	final Map<Integer, Integer> map = TreeBuilder.builder().setL(2)
		.setKeyType(Types.integer()).setValueType(Types.integer())
		.build();
	for (int i = 0; i < 1000 * 1000 * 20; i++) {
	    final int j = random.nextInt();
	    map.put(j, -j);
	}
    }

    private static Options getOptions(final Class<?> clazz) {
	return new OptionsBuilder().include(clazz.getSimpleName()).threads(100)
		.measurementIterations(1000).verbosity(VerboseMode.NORMAL)
		.forks(0).build();
    }

    public static void main(String[] args) throws RunnerException, IOException {
	test();
//	new Runner(getOptions(MapTestJbTreeMap.class)).run();
    }

}
