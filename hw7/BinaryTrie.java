import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class BinaryTrie implements Serializable {
    // alphabet size of extended ASCII
    private static final int R = 256;
    private final Node root;
    public BinaryTrie(Map<Character, Integer> frequencyTable) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (int i = 0; i < R; i++) {
            char ch = (char) i;
            if (frequencyTable.containsKey(ch)) {
                pq.add(new Node(ch, frequencyTable.get(ch), null, null));
            }
        }

        while (pq.size() >= 2) {
            Node left, right;
            left = pq.remove();
            right = pq.remove();
            pq.add(new Node('\0', left.freq + right.freq, left, right));
        }

        root = pq.remove();
    }

    // Huffman trie node
    private static class Node implements Comparable<Node>, Serializable {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return (left == null) && (right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }
    public Match longestPrefixMatch(BitSequence querySequence) {
        Node nextNode = root;
        for (int i = 0; i < querySequence.length(); i++) {
            if (querySequence.bitAt(i) == 0) {
                nextNode = nextNode.left;
            } else {
                nextNode = nextNode.right;
            }

            if (nextNode.isLeaf()) {
                return new Match(querySequence.firstNBits(i + 1), nextNode.ch);
            }
        }
        return null;
    }
    public Map<Character, BitSequence> buildLookupTable() {
        Map<Character, BitSequence> table = new HashMap<>();
        buildCode(table, root, "");
        return table;
    }

    private static void buildCode(Map<Character, BitSequence> table, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(table, x.left,  s + '0');
            buildCode(table, x.right, s + '1');
        } else {
            table.put(x.ch, new BitSequence(s));
        }
    }
}
