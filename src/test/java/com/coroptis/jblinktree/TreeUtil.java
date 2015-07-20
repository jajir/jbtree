package com.coroptis.jblinktree;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

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
 * Provide printing of dot files.
 * 
 * @author jajir
 * 
 */
public class TreeUtil {

    private final String intendation = "    ";

    private final JbTree<Integer, Integer> jbTree;

    public TreeUtil(final JbTree<Integer, Integer> jbTree) {
	this.jbTree = jbTree;
    }

    public String toDotFile(final File file) {
	final StringBuilder buff = new StringBuilder();

	buff.append("digraph graphname {\n");
	buff.append(intendation);
	buff.append("edge [label=0];\n");
	buff.append(intendation);
	buff.append("graph [ranksep=1];\n");
	buff.append(intendation);
	buff.append("node [shape=record]\n");

	jbTree.visit(new JbTreeVisitor<Integer, Integer>() {

	    @Override
	    public boolean visitedLeaf(final Node<Integer, Integer> node) {
		node.writeTo(buff, intendation);
		return true;
	    }

	    @Override
	    public boolean visitedNonLeaf(final Node<Integer, Integer> node) {
		node.writeTo(buff, intendation);
		return true;
	    }
	});

	jbTree.visit(new JbTreeVisitor<Integer, Integer>() {

	    private void addLink(final Node<Integer, Integer> node) {
		if (node.getLink() != null) {
		    buff.append(intendation);
		    buff.append("\"node");
		    buff.append(node.getId());
		    buff.append("\":L");
		    buff.append(node.getLink());
		    buff.append(" -> ");
		    buff.append("\"node");
		    buff.append(node.getLink());
		    buff.append("\" [constraint=false, label=\"");
		    buff.append(node.getLink());
		    buff.append("\"]");
		    buff.append(";\n");
		}
	    }

	    @Override
	    public boolean visitedLeaf(final Node<Integer, Integer> node) {
		addLink(node);
		return true;
	    }

	    @Override
	    public boolean visitedNonLeaf(final Node<Integer, Integer> node) {
		addLink(node);
		for (final Object o : node.getNodeIds()) {
		    Integer i = (Integer) o;
		    buff.append(intendation);
		    buff.append("\"node");
		    buff.append(node.getId());
		    buff.append("\":F");
		    buff.append(i);
		    buff.append(" -> ");
		    buff.append("\"node");
		    buff.append(i);
		    buff.append("\" [label=\"");
		    buff.append(i);
		    buff.append("\"];");
		    buff.append("\n");
		}
		return true;
	    }
	});

	buff.append("");
	buff.append("}");

	try {
	    Files.write(buff, file, Charsets.ISO_8859_1);
	} catch (IOException e) {
	    throw new JblinktreeException(e.getMessage());
	}
	return buff.toString();
    }

}
