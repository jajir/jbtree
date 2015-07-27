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

import java.util.Random;

import com.coroptis.jblinktree.TreeBuilder;
import com.coroptis.jblinktree.TreeMap;
import com.coroptis.jblinktree.type.Types;

/**
 * User JbTree as map.
 * 
 * @author jajir
 * 
 */
public class FunctionalityJbTree implements TestedTreeFunctionality {

    private TreeMap<Integer, Integer> tree;

    private Random random;

    private final int randomBaseNumber;

    public FunctionalityJbTree(final int randomBaseNumber) {
	this.randomBaseNumber = randomBaseNumber;
    }

    @Override
    public void setUp() {
	tree = TreeBuilder.builder().setL(10).setKeyType(Types.integer())
		.setValueType(Types.integer()).build();
	random = new Random();
    }

    @Override
    public Object tearDown() {
	tree.verify();
	return tree;
    }

    @Override
    public void doWork() {
	Integer integer = random.nextInt(randomBaseNumber) + 1;
	tree.put(integer, integer);
    }

}
