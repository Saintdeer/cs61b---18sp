import org.junit.Test;
import java.util.*;


public class Tries {
    Node root;

    public Tries() {
        root = new Node(null, null);
    }

    void addStr(String str) {
        if (illegal(str)) {
            return;
        }
        root.addRest(str);
    }
    
    List<String> getStrWithPrefix(String prefix) {
        if (illegal(prefix)) {
            return null;
        }

        char first, lower, upper;
        first = prefix.charAt(0);
        lower = Character.toLowerCase(first);
        upper = Character.toUpperCase(first);

        List<String> result = new ArrayList<>();
        Map<Character, Node> rootNext = root.next;

        if (rootNext.containsKey(lower)) {
            result = (rootNext.get(lower).stringWithPrefix(prefix, this));
        }

        if (rootNext.containsKey(upper)) {
            List<String> result2 = rootNext.get(upper).stringWithPrefix(prefix, this);
            if (result.isEmpty()) {
                result = result2;
            } else {
                result.addAll(result2);
            }
        }

        return result;
    }

    static boolean illegal(String str) {
        return str == null || str.isEmpty();
    }

    static class Node {
        char ch;
        boolean isKey = false;
        Node preNode;
        Map<Character, Node> next = new HashMap<>();

        Node(String str, Node preNode) {
            if (illegal(str)) {
                return;
            }
            ch = str.charAt(0);
            this.preNode = preNode;

            String rest = str.substring(1);
            if (!rest.isEmpty()) {
                addRest(rest);
            } else {
                isKey = true;
            }
        }

        void addRest(String str) {
            if (illegal(str)) {
                return;
            }
            char first = str.charAt(0);
            if (next.containsKey(first)) {
                next.get(first).addRest(str.substring(1));
            } else {
                next.put(first, new Node(str, this));
            }
        }

        List<String> stringWithPrefix(String prefix, Tries t) {
            List<String> stringsOfSuffix = new ArrayList<>();

            List<Node> prefixNodes = findPrefixNode(prefix, t);
            for (Node prefixNode : prefixNodes) {
                String prefixCopy = getPrefix("", prefixNode);
                if (prefixNode.isKey) {
                    stringsOfSuffix.add(prefixCopy);
                }
                for (Node suffixNode : prefixNode.next.values()) {
                    suffixNode.findSuffix(prefixCopy, stringsOfSuffix);
                }
            }
            return stringsOfSuffix;
        }

        String getPrefix(String str, Node node) {
            if (node.preNode.preNode == null) {
                return node.ch + str;
            } else {
                return getPrefix(node.ch + str, node.preNode);
            }
        }

        void findSuffix(String prefix, List<String> result) {
            String newPrefix = prefix + ch;
            if (isKey) {
                result.add(newPrefix);
            }
            for (Node suffix : next.values()) {
                suffix.findSuffix(newPrefix, result);
            }
        }

        List<Node> findPrefixNode(String prefix, Tries t) {
            List<Node> result = new LinkedList<>();

            if (prefix.length() > 1) {
                char secondChar, lower, upper;
                secondChar = prefix.charAt(1);
                lower = Character.toLowerCase(secondChar);
                upper = Character.toUpperCase(secondChar);

                List<Node> second = new LinkedList<>();
                String searchPrefix = prefix.substring(1);
                if (next.containsKey(upper)) {
                    second.add(next.get(upper));
                }
                if (next.containsKey(lower)) {
                    second.add(next.get(lower));
                }

                // I'm not sure if it's necessary
                if (second.isEmpty() && next.containsKey(' ')) {
                    result.addAll(next.get(' ').findPrefixNode(prefix, t));
                }

                for (Node n : second) {
                    result.addAll(n.findPrefixNode(searchPrefix, t));
                }
            } else {
                result.add(this);
            }

            return result;
        }
    }

    @Test
    public void test() {
        Tries t = new Tries();
        t.addStr("Monterey Av & The Alameda");
        t.addStr("Monte Vista Food");

        List<String> strings1 = t.getStrWithPrefix("m");

    }
}



