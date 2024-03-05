import edu.princeton.cs.introcs.In;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class TrieDB {
    private final Node root;

    /* for dict */
    public TrieDB(In dict) {
        root = new Node(null, null);
        for (String str : dict.readAllStrings()) {
            addStr(str);
        }
    }

    /* for board */
    public TrieDB(TrieDB dict) {
        root = new Node(-1, ' ', null, dict);
    }

    /* add a full string to dict */
    void addStr(String str) {
        if (illegal(str)) {
            return;
        }
        root.addRestString(str);
    }

    /* for board */
    Node getRoot() {
        return root;
    }

    /* for board */
    void addPosition(int position, char ch, Node parentNode, TrieDB dict) {
        parentNode.addNextPositionAndCheck(position, ch, dict);
    }

    /* check whether the trie has the prefix */
    boolean hasPrefix(String prefix) {
        return getPrefixNode(prefix) != null;
    }

    private Node getPrefixNode(String prefix) {
        if (illegal(prefix)) {
            return null;
        }

        char first = prefix.charAt(0);

        Map<Character, Node> rootNext = root.charNext;
        if (rootNext.containsKey(first)) {
            return rootNext.get(first).findPrefixNode(prefix);
        } else {
            return null;
        }
    }

    /* check whether dict trie has the full str */
    private boolean hasString(String str) {
        Node stringNode = getPrefixNode(str);
        return stringNode != null && stringNode.isKey;
    }

    List<String> getAllStrings(int length) {
        List<Node> nodes, keys;
        nodes = new LinkedList<>(root.positionNext.values());
        keys = new LinkedList<>();

        while (!nodes.isEmpty()) {
            Node nd = nodes.remove(0);
            if (nd.isKey) {
                keys.add(nd);
            }
            nodes.addAll(nd.positionNext.values());
        }

        Set<String> strSet = new HashSet<>();
        for (Node keyNode : keys) {
            strSet.add(keyNode.getStringUpToCurrentNode());
        }

        String[] stringsSorted = RadixSort.sort(strSet.toArray(new String[0]));
        length = Math.min(length, strSet.size());
        return Arrays.asList(Arrays.copyOfRange(stringsSorted, 0, length));
    }

    /* check whether the str is legal to add to dict */
    private static boolean illegal(String str) {
        return str == null || str.isEmpty();
    }

    /* for board */
    static List<Node> getAllNextNodes(Node node) {
        List<Node> nodes = new LinkedList<>();
        Set<Integer> keys = node.positionNext.keySet();
        Map<Integer, Node> kv = node.positionNext;
        for (int key : keys) {
            nodes.add(kv.get(key));
        }
        return nodes;
    }

    static class Node {
        // character
        private char ch;

        // if it's true, this ch is the last character of a full string
        private boolean isKey = false;

        // parent node
        private Node preNode;

        // position index in board
        private int position;

        // map ch to children node
        private final Map<Character, Node> charNext = new HashMap<>();

        // map position to children node
        private final Map<Integer, Node> positionNext = new HashMap<>();

        // all the used positions
        private final Set<Integer> usedPositions = new HashSet<>();

        /* for str in dict, no need position */
        private Node(String str, Node preNode) {
            if (illegal(str)) {
                return;
            }
            ch = str.charAt(0);
            this.preNode = preNode;

            String rest = str.substring(1);
            if (!rest.isEmpty()) {
                addRestString(rest);
            } else {
                isKey = true;
            }
        }

        /* for str in board, need position */
        private Node(int position, char ch, Node preNode, TrieDB dict) {
            this.position = position;
            this.preNode = preNode;
            this.ch = ch;
            usedPositions.add(position);

            if (preNode == null || preNode.preNode == null) {
                return;
            }
            usedPositions.addAll(preNode.usedPositions);

            String strUpToNow = getStringUpToCurrentNode();
            if (dict.hasString(strUpToNow)) {
                isKey = true;
            }
        }

        /* helper function of dict Node constructor to add rest part of full string */
        private void addRestString(String str) {
            if (illegal(str)) {
                return;
            }
            char first = str.charAt(0);
            if (charNext.containsKey(first)) {
                charNext.get(first).addRestString(str.substring(1));
            } else {
                charNext.put(first, new Node(str, this));
            }
        }

        /* for board Node to add next position node */
        void addNextPositionAndCheck(int nextPosition, char character, TrieDB dict) {
            if (!usedPositions.contains(nextPosition) && !positionNext.containsKey(nextPosition)) {
                Node neighbor = new Node(nextPosition, character, this, dict);
                if (neighbor.neighborPrefixCheck(dict)) {
                    positionNext.put(nextPosition, neighbor);
                }
            }
        }

        int getPosition() {
            return position;
        }

        /* helper function of getStrWithPrefix */
        private List<String> stringWithPrefixNode(Node prefixNode, String prefix) {
            List<String> stringsOfSuffix = new ArrayList<>();

            if (prefixNode == null) {
                return stringsOfSuffix;
            }

            if (prefixNode.isKey) {
                stringsOfSuffix.add(prefix);
            }
            for (Node suffixNode : prefixNode.charNext.values()) {
                suffixNode.findSuffixAndAddToPrefix(prefix, stringsOfSuffix);
            }

            return stringsOfSuffix;
        }

        /* get the string consisted of the chars from beginning to this node */
        private String getStringUpToCurrentNode() {
            StringBuilder str = new StringBuilder(String.valueOf(ch));
            Node preNd = preNode;
            while (preNd.preNode != null) {
                str.insert(0, preNd.ch);
                preNd = preNd.preNode;
            }
            return str.toString();
        }

        /* according to given prefix to get all full strings with this prefix */
        private void findSuffixAndAddToPrefix(String prefix, List<String> result) {
            String newPrefix = prefix + ch;
            if (isKey) {
                result.add(newPrefix);
            }
            for (Node suffix : charNext.values()) {
                suffix.findSuffixAndAddToPrefix(newPrefix, result);
            }
        }

        /* according to given prefix to find the node which ch is the last character of prefix */
        private Node findPrefixNode(String prefix) {
            if (prefix.length() > 1) {
                char secondChar = prefix.charAt(1);
                String searchPrefix = prefix.substring(1);
                if (charNext.containsKey(secondChar)) {
                    return charNext.get(secondChar).findPrefixNode(searchPrefix);
                } else {
                    return null;
                }
            }
            return this;
        }

        /* delete parent node which can't consist of a valid string after prefixCheckForNeighbor */
        public void parentCheckAndPrune() {
            if (preNode == null) {
                return;
            }

            boolean hasPrefixButNotAFullString = !isKey && positionNext.isEmpty();
            if (hasPrefixButNotAFullString) {
                preNode.positionNext.remove(position);
                if (preNode.preNode == null) {
                    return;
                }
                preNode.parentCheckAndPrune();
            }
        }

        private boolean neighborPrefixCheck(TrieDB dict) {
            return dict.hasPrefix(getStringUpToCurrentNode());
        }
    }
}



