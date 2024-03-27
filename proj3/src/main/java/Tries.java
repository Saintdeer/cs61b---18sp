import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tries {
    Node root;

    public Tries() {
        root = new Node();
    }

    private static class Node {
        Map<Character, Node> next;
        List<String> names;

        Node() {
            next = new HashMap<>();
            names = new ArrayList<>();
        }
    }

    void addStr(String originalStr) {
        String cleanStr = GraphDB.cleanString(originalStr);
        Node currentNode = root;
        for (char ch : cleanStr.toCharArray()) {
            currentNode.next.putIfAbsent(ch, new Node());
            currentNode = currentNode.next.get(ch);
        }
        if (!currentNode.names.contains(originalStr)) {
            currentNode.names.add(originalStr);
        }
    }

    List<String> getStrWithPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        if (prefix == null || prefix.isEmpty()) {
            return result;
        }

        String cleanStr = GraphDB.cleanString(prefix);
        Node prefixNode = root;
        for (char ch : cleanStr.toCharArray()) {
            prefixNode = prefixNode.next.get(ch);
            if (prefixNode == null) {
                return result;
            }
        }

        List<Node> nodeList = new LinkedList<>();
        nodeList.add(prefixNode);
        while (!nodeList.isEmpty()) {
            Node node = nodeList.remove(0);
            result.addAll(node.names);
            nodeList.addAll(node.next.values());
        }
        return result;
    }
}
