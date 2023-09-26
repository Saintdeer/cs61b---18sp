package lab9;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Implementation of interface Map61B with BST as core data structure.
 *
 * @author Your name here
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        /* (K, V) pair stored in this Node. */
        private K key;
        private V value;

        /* Children of this Node. */
        private Node left = null;
        private Node right = null;

        private Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private Node root;  /* Root node of the tree. */
    private int size; /* The number of key-value pairs in the tree */

    private Node parentOfKn = null;

    /* Creates an empty BSTMap. */
    public BSTMap() {
        this.clear();
    }

    /* Removes all of the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Returns the value mapped to by KEY in the subtree rooted in P.
     * or null if this map contains no mapping for the key.
     */
    private V getHelper(K key, Node p) {
        if (p == null) {
            return null;
        }
        int compare = key.compareTo(p.key);
        if (compare < 0) {
            return getHelper(key, p.left);
        } else if (compare == 0) {
            return p.value;
        } else {
            return getHelper(key, p.right);
        }
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return getHelper(key, root);
    }

    /**
     * Returns a BSTMap rooted in p with (KEY, VALUE) added as a key-value mapping.
     * Or if p is null, it returns a one node BSTMap containing (KEY, VALUE).
     */
    private Node putHelper(K key, V value, Node p) {
        if (p == null) {
            size += 1;
            return new Node(key, value);
        }
        int compare = key.compareTo(p.key);
        if (compare < 0) {
            p.left = putHelper(key, value, p.left);
        } else if (compare == 0) {
            p.value = value;
            return p;
        } else {
            p.right = putHelper(key, value, p.right);
        }
        return p;
    }

    /**
     * Inserts the key KEY
     * If it is already present, updates value to be VALUE.
     */
    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root);
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    //////////////// EVERYTHING BELOW THIS LINE IS OPTIONAL ////////////////

    /* Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        Set<K> s = new HashSet<>();
        keySetHelper(root, s);
        return s;
    }

    private void keySetHelper(Node p, Set<K> s) {
        if (p == null) {
            return;
        }
        s.add(p.key);
        keySetHelper(p.left, s);
        keySetHelper(p.right, s);
    }

    /**
     * Removes KEY from the tree if present
     * returns VALUE removed,
     * null on failed removal.
     */
    @Override
    public V remove(K key) {
        Node kn = findKeyNode(key, root);
        if (kn == null) {
            return null;
        }
        size -= 1;
        Node newN = findSubstituteNode(kn);
        if (newN != null && kn != root) {
            remove(newN.key);
        }
        substituteNode(newN, key);
        return kn.value;
    }

    private void substituteNode(Node newN, K key) {
        if (parentOfKn == null) {
            if (newN == null) {
                root = null;
            } else {
                root.key = newN.key;
                root.value = newN.value;
            }
        } else if (parentOfKn.left != null && parentOfKn.left.key.compareTo(key) == 0) {
            if (newN == null) {
                parentOfKn.left = null;
            } else {
                parentOfKn.left.key = newN.key;
                parentOfKn.left.value = newN.value;
            }
        } else {
            if (newN == null) {
                parentOfKn.right = null;
            } else {
                parentOfKn.right.key = newN.key;
                parentOfKn.right.value = newN.value;
            }
        }
        parentOfKn = null;
    }

    private Node findSubstituteNode(Node kn) {
        Node newN = findLeftRightChild(kn);
        if (newN == null) {
            newN = findRightLeftChild(kn);
        }
        if (newN == null) {
            newN = findLeftOrRightChild(kn);
        }
        return newN;
    }

    private Node findLeftOrRightChild(Node n) {
        Node newN;
        if (n.left == null) {
            if (n.right == null) {
                return null;
            }
            newN = n.right;
            n.right = null;
        } else {
            newN = n.left;
            n.left = null;
        }
        return newN;
    }

    private Node findLeftRightChild(Node n) {
        if (n.left != null && n.left.right != null) {
            Node newN = n.left.right;
            n.left.right = null;
            return newN;
        }
        return null;
    }

    private Node findRightLeftChild(Node n) {
        if (n.right != null && n.right.left != null) {
            Node newN = n.right.left;
            n.right.left = null;
            return newN;
        }
        return null;
    }

    private Node findKeyNode(K key, Node n) {
        if (n == null) {
            return null;
        }

        int compare = key.compareTo(n.key);
        Node kn;

        if (compare < 0) {
            kn = findKeyNode(key, n.left);
        } else if (compare == 0) {
            return n;
        } else {
            kn = findKeyNode(key, n.right);
        }

        if (parentOfKn == null && kn != null) {
            parentOfKn = n;
        }
        return kn;
    }

    /**
     * Removes the key-value entry for the specified key only if it is
     * currently mapped to the specified value.  Returns the VALUE removed,
     * null on failed removal.
     **/
    @Override
    public V remove(K key, V value) {
        if (get(key) != value) {
            return null;
        }
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
