package com.coroptis.jblinktree;

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

/**
 * Provide fluent API for creating tree.
 * 
 * @author jajir
 *
 */
public final class TreeBuilder {

    private Integer l;

    public static TreeBuilder builder() {
	return new TreeBuilder(5);
    }

    private TreeBuilder(final Integer default_l) {
	this.l = default_l;
    }

    public void setL(final Integer l) {
	this.l = l;
    }

    public Tree build() {
	NodeStoreImpl nodeStore = new NodeStoreImpl();
	Tree tree = new Tree(l, nodeStore);
	return tree;
    }

}
