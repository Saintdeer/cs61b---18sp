import java.util.HashMap;
import java.util.Map;

public class Trie {
    private Node root;

    private class Node {
        private Map<Character, Node> next;
        private boolean isKey;

        private Node() {
            next = new HashMap<>();
            isKey = false;
        }
    }

    public Trie() {
        root = new Node();
    }

    public void addString(String str) {
        Node nextNode = root;
        for (char ch : str.toCharArray()) {
            nextNode.next.putIfAbsent(ch, new Node());
            nextNode = nextNode.next.get(ch);
        }
        nextNode.isKey = true;
    }

    public int hasPrefixOrString(String prefix) {
        Node node = root;
        for (char ch : prefix.toCharArray()) {
            node = node.next.get(ch);
            if (node == null) {
                break;
            }
        }

        if (node == null) {
            return -1; // no such prefix
        } else if (!node.isKey) {
            return 0; // has prefix
        } else {
            return 1; // has full string
        }
    }
}
