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

    public TreeBuilder setL(final Integer l) {
	this.l = l;
	return this;
    }

    public JbTree build() {
	final IdGenerator idGenerator = new IdGeneratorImpl();
	final NodeStoreImpl nodeStore = new NodeStoreImpl(idGenerator);
	final JbTreeTool jbTreeTool = new JbTreeToolImpl(nodeStore);
	final JbTreeService treeService = new JbTreeServiceImpl(nodeStore, jbTreeTool);
	final JbTree tree = new JbTreeImpl(l, nodeStore, jbTreeTool, treeService);
	return tree;
    }

}
