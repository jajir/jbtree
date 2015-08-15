package com.coroptis.jblinktree;

import java.util.Stack;

import com.coroptis.jblinktree.type.TypeDescriptor;
import com.google.common.base.Preconditions;

public class JbTreeHelperImpl<K, V> implements JbTreeHelper<K, V> {

    /**
     * Main node parameter, it's number of nodes.
     */
    private final int l;

    private final NodeStore<K> nodeStore;

    private final JbTreeTool<K, V> treeTool;

    private final JbTreeService<K> treeService;

    private final TypeDescriptor<V> valueTypeDescriptor;

    private final TypeDescriptor<Integer> linkTypeDescriptor;

    private final JbTreeData<K, V> treeData;

    JbTreeHelperImpl(final int l, final NodeStore<K> nodeStore, final JbTreeTool<K, V> treeTool,
	    final JbTreeService<K> treeService, final JbTreeData<K, V> treeData,
	    final TypeDescriptor<V> valueTypeDescriptor,
	    final TypeDescriptor<Integer> linkTypeDescriptor) {
	this.l = l;
	this.nodeStore = Preconditions.checkNotNull(nodeStore);
	this.treeTool = Preconditions.checkNotNull(treeTool);
	this.treeService = Preconditions.checkNotNull(treeService);
	this.treeData = Preconditions.checkNotNull(treeData);
	this.valueTypeDescriptor = Preconditions.checkNotNull(valueTypeDescriptor,
		"value TypeDescriptor is null, use .setValueType in builder");
	this.linkTypeDescriptor = Preconditions.checkNotNull(linkTypeDescriptor,
		"link TypeDescriptor is null");
    }

    @Override
    public Node<K, V> findAppropriateLeafNode(final K key) {
	Preconditions.checkNotNull(key);
	Integer idNode = treeTool.findLeafNodeId(key, new Stack<Integer>(),
		treeData.getRootNodeId());
	Node<K, V> node = nodeStore.get(idNode);
	return treeTool.moveRightLeafNodeWithoutLocking(node, key);
    }

    @Override
    public V insertToLeafNode(Node<K, V> currentNode, final K key, final V value,
	    final Stack<Integer> stack) {
	if (currentNode.getKeysCount() >= l) {
	    /**
	     * There is no free space for key and value
	     */
	    final Node<K, V> newNode = treeTool.split(currentNode, key, value, valueTypeDescriptor);
	    nodeStore.writeNode(newNode);
	    nodeStore.writeNode(currentNode);
	    if (stack.empty()) {
		/**
		 * There is no previous node, it's root node.
		 */
		treeData.splitRootNode(currentNode, newNode);
		return null;
	    } else {
		/**
		 * There is a previous node, so move there.
		 */
		Integer tmpValue = newNode.getId();
		K tmpKey = newNode.getMaxKey();
		final Integer previousCurrentNodeId = currentNode.getId();
		Node<K, Integer> previousNode = treeService.loadParentNode(currentNode, tmpKey,
			stack.pop());
		nodeStore.unlockNode(previousCurrentNodeId);
		return insertNonLeaf(previousNode, tmpKey, tmpValue, stack);
	    }
	} else {
	    /**
	     * There is a free space for new key and value.
	     */
	    storeValueIntoLeafNode(currentNode, key, value);
	    return null;
	}
    }

    private V insertNonLeaf(Node<K, Integer> currentNode, final K key, final Integer value,
	    final Stack<Integer> stack) {
	/**
	 * Key and value have to be inserted
	 */
	Integer tmpValue = value;
	K tmpKey = key;
	while (true) {
	    if (currentNode.getKeysCount() >= l) {
		/**
		 * There is no free space for key and value
		 */
		final Node<K, Integer> newNode = treeTool.split(currentNode, tmpKey, tmpValue,
			linkTypeDescriptor);
		nodeStore.writeNode(newNode);
		nodeStore.writeNode(currentNode);
		if (stack.empty()) {
		    /**
		     * There is no previous node, it's root node.
		     */
		    treeData.splitRootNode(currentNode, newNode);
		    return null;
		} else {
		    /**
		     * There is a previous node, so move there.
		     */
		    tmpValue = newNode.getId();
		    tmpKey = newNode.getMaxKey();
		    final Integer previousCurrentNodeId = currentNode.getId();
		    currentNode = treeService.loadParentNode(currentNode, tmpKey, stack.pop());
		    nodeStore.unlockNode(previousCurrentNodeId);
		}
	    } else {
		/**
		 * There is a free space for new key and value.
		 */
		storeValueIntoNonLeafNode(currentNode, tmpKey, tmpValue);
		return null;
	    }
	}
    }

    @Override
    public void storeValueIntoLeafNode(final Node<K, V> currentNode, final K key, final V value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }

    private void storeValueIntoNonLeafNode(final Node<K, Integer> currentNode, final K key,
	    final Integer value) {
	currentNode.insert(key, value);
	nodeStore.writeNode(currentNode);
	nodeStore.unlockNode(currentNode.getId());
    }

}
