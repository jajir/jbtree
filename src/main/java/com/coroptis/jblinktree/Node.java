package com.coroptis.jblinktree;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

/**
 * <p>
 * Generally in tree are inserted keys and values (K,V). There are two kind of
 * nodes:
 * <ul>
 * <li>leaf node - contains keys and values pairs</li>
 * <li>non-leaf node - contains keys and pointers to another nodes</li>
 * </ul>
 * </p>
 * In node are stored following data:
 * <table border="1" style="border-collapse: collapse">
 * <tr>
 * <td>value</td>
 * <td>P(0)</td>
 * <td>K(1)</td>
 * <td>P(1)</td>
 * <td>K(2)</td>
 * <td>P(2)</td>
 * <td>&nbsp;...&nbsp;</td>
 * <td>K(L*2)</td>
 * <td>P(L*2)</td>
 * <td>K(L*2+1)</td>
 * <td>link</td>
 * </tr>
 * <tr>
 * <td>index</td>
 * <td>0</td>
 * <td>1</td>
 * <td>2</td>
 * <td>3</td>
 * <td>4</td>
 * <td>&nbsp;...&nbsp;</td>
 * <td>L*2-1</td>
 * <td>L*2</td>
 * <td>L*2+1</td>
 * <td>L*2+2</td>
 * </tr>
 * </table>
 * Where:
 * <ul>
 * <li>P - pointer to some another node</li>
 * <li>K - Key inserted into tree, this value represents some user's data. Key
 * could be ordered and compared.</li>
 * <li>V - value inserted into tree</li>
 * <li>L - main parameter of tree, in tree could be maximally L*2+1 nodes.</li>
 * <li>link - pointer to next sibling node</li>
 * <li>K(L*2+1) - highest key value from node in case of leaf node or highest
 * key value from all referenced nodes.</li>
 * </ul>
 * First value P0 at index 0 have special meaning, when it's {@link Node#M} than
 * this node is leaf node. In all other cases is non-leaf node.
 * 
 * @author jajir
 * 
 */
public class Node {

    public static final int L = 2;

    /**
     * When this value in at P(0) position than it's leaf node.
     */
    public final static Integer M = -1;

    private final Integer id;

    public Integer field[];

    private final Lock lock;

    public Node(final Integer id, final boolean isLeafNode) {
	this.id = id;
	this.lock = new ReentrantLock();
	/**
	 * There is three position even in empty node: P0, max key and link.
	 */
	field = new Integer[3];
	if (isLeafNode) {
	    field[0] = M;
	}
    }

    public Integer getLink() {
	return field[field.length - 1];
    }

    public void setLink(final Integer link) {
	field[field.length - 1] = link;
    }

    public Integer getP0() {
	return field[0];
    }

    public void setP0(final Integer p0) {
	field[0] = p0;
    }

    public boolean isEmpty() {
	return getKeysCount() == 0;
    }

    public int getKeysCount() {
	return (field.length - 3) / 2;
    }

    public void insert(final Integer key, final Integer value) {
	Preconditions.checkNotNull(key);
	Preconditions.checkNotNull(value);
	if (field.length >= L * 2 + 2) {
	    throw new JblinktreeException("Leaf is full another value can't be inserted.");
	}
	for (int i = 1; i < field.length - 2; i = i + 2) {
	    if (field[i] > key) {
		/**
		 * given value should be inserted 1 before current index
		 */
		insertToPosition(key, value, i);
		return;
	    }
	}
	/**
	 * New key is bigger than all others so should be at the end.
	 */
	insertToPosition(key, value, field.length - 2);
	setMaxKeyValue(key);
    }

    private void insertToPosition(final Integer key, final Integer value, final int targetIndex) {
	Integer[] field2 = new Integer[field.length + 2];
	if (targetIndex > 0) {
	    System.arraycopy(field, 0, field2, 0, targetIndex);
	}
	field2[targetIndex] = key;
	field2[targetIndex + 1] = value;
	System.arraycopy(field, targetIndex, field2, targetIndex + 2, field.length - targetIndex);
	field = field2;
    }

    /**
     * About half of keys will be copied to <code>node</node>.
     * 
     * @param node
     *            required empty node
     */
    public void moveTopHalfOfDataTo(final Node node) {
	Preconditions.checkArgument(node.isEmpty());
	// copy top half to empty node
	final int startKeyNo = getKeysCount() / 2;
	final int startIndex = startKeyNo * 2 + 1;
	final int length = field.length - startIndex;
	node.field = new Integer[length + 1];
	System.arraycopy(field, startIndex, node.field, 1, length);

	// remove copied data from this node
	Integer[] field2 = new Integer[startIndex + 2];
	System.arraycopy(field, 0, field2, 0, startIndex);
	field = field2;
	setLink(node.getId());
	setMaxKeyValue(field[field.length - 4]);
	if (isLeafNode()) {
	    node.field[0] = M;
	}
    }

    /**
     * Return max key, that could be use for representing this nide.
     * 
     * @return
     */
    public Integer getMaxKey() {
	return field[field.length - 2];
    }

    @Override
    public String toString() {
	StringBuilder buff = new StringBuilder();
	buff.append("[");
	for (int i = 0; i < field.length; i++) {
	    if (i != 0) {
		buff.append(", ");
	    }
	    buff.append(field[i]);
	}
	buff.append("]");
	return MoreObjects.toStringHelper(Node.class).add("id", getId())
		.add("isLeafNode", isLeafNode()).add("field", buff.toString()).toString();
    }

    /**
     * @return the id
     */
    public Integer getId() {
	return id;
    }

    /**
     * @return the isLeafNode
     */
    public boolean isLeafNode() {
	return M.equals(field[0]);
    }

    public Integer getCorrespondingNodeId(final Integer key) {
	if (isLeafNode()) {
	    throw new JblinktreeException("Leaf node doesn't have any child nodes.");
	}
	for (int i = 1; i < field.length; i = i + 2) {
	    if (key <= field[i]) {
		return field[i - 1];
	    }
	}
	return null;
    }

    public Integer getValue(final Integer key) {
	Preconditions.checkNotNull(key);
	if (!isLeafNode()) {
	    throw new JblinktreeException("Non-leaf node doesn't have leaf value.");
	}
	for (int i = 1; i < field.length - 2; i = i + 2) {
	    if (key.equals(field[i])) {
		return field[i + 1];
	    }
	}
	return null;
    }

    public List<Integer> getNodeIds() {
	final List<Integer> out = new ArrayList<Integer>();
	for (int i = 0; i < field.length - 2; i = i + 2) {
	    out.add(field[i]);
	}
	return out;
    }

    public List<Integer> getKeys() {
	final List<Integer> out = new ArrayList<Integer>();
	for (int i = 1; i < field.length; i = i + 2) {
	    out.add(field[i]);
	}
	return out;
    }

    public void setMaxKeyValue(final Integer maxKey) {
	field[field.length - 2] = maxKey;
    }

    public Integer getMaxKeyValue() {
	return field[field.length - 2];
    }

    /**
     * @return the lock
     */
    public Lock getLock() {
	return lock;
    }

}
