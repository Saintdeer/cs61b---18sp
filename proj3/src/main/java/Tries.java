import org.junit.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class Tries {
    Node root;
    String prefixCopy = "";

    public Tries() {
        root = new Node(null);
    }

    void addStr(String str) {
        if (str == null || str.isEmpty()) {
            return;
        }
        root.addRest(str);
    }

    /* 这里还有bug：不能把prefix的任意大小写版本都搜索一遍 */
    List<String> getStrWithPrefix(String prefix) {
        prefixCopy = "";
        if (prefix == null || prefix.isEmpty()) {
            return null;
        }
        char first = prefix.charAt(0),
                lower = Character.toLowerCase(first),
                upper = Character.toUpperCase(first);

        List<String> result = new ArrayList<>();
        Map<Character, Node> rootNext = root.next;
        if (rootNext.containsKey(lower)) {
            prefixCopy += lower;
            result = (rootNext.get(lower).stringWithPrefix(prefix, this));
        }

        prefixCopy = "";
        if (rootNext.containsKey(upper)) {
            prefixCopy += upper;
            List<String> result2 = rootNext.get(upper).stringWithPrefix(prefix, this);
            if (result == null || result.isEmpty()) {
                result = result2;
            } else {
                result.addAll(result2);
            }
        }

        return result;
    }

    static class Node {
        char ch;
        boolean isKey = false;
        Map<Character, Node> next = new HashMap<>();

        Node(String str) {
            if (str == null || str.isEmpty()) {
                return;
            }
            ch = str.charAt(0);
            String rest = str.substring(1);
            if (!rest.isEmpty()) {
                addRest(rest);
            } else {
                isKey = true;
            }
        }

        void addRest(String str) {
            if (str == null || str.isEmpty()) {
                return;
            }
            char first = str.charAt(0);
            if (next.containsKey(first)) {
                next.get(first).addRest(str.substring(1));
            } else {
                next.put(first, new Node(str));
            }
        }

        List<String> stringWithPrefix(String prefix, Tries t) {
            List<String> stringsOfSuffix = new ArrayList<>();

            Node prefixNode = findPrefixNode(prefix, t);
            if (prefixNode == null) {
                return new ArrayList<>();
            }

            String prefixCopy = t.prefixCopy;
            if (prefixNode.isKey) {
                stringsOfSuffix.add(prefixCopy);
            }
            for (Node suffixNode : prefixNode.next.values()) {
                suffixNode.findSuffix(prefixCopy, stringsOfSuffix);
            }
            return stringsOfSuffix;
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

        Node findPrefixNode(String prefix, Tries t) {
            if (prefix.length() > 1) {
                char secondChar = prefix.charAt(1),
                        lower = Character.toLowerCase(secondChar),
                        upper = Character.toUpperCase(secondChar);
                Node second = null;
                String searchPrefix = prefix.substring(1);
                if (next.containsKey(upper)) {
                    t.prefixCopy += upper;
                    second = next.get(upper);
                } else if (next.containsKey(lower)) {
                    t.prefixCopy += lower;
                    second = next.get(lower);
                } else if (next.containsKey(' ')) {
                    t.prefixCopy += ' ';
                    second = next.get(' ');
                    searchPrefix = prefix;
                }
                if (second == null) {
                    return null;
                }
                return second.findPrefixNode(searchPrefix, t);
            } else {
                return this;
            }
        }
    }


    @Test
    public void testT() {
        Tries t = new Tries();
        t.addStr("Top Dog");
        /*t.addStr("e Avenue");
        t.addStr("e Street");*/

        List<String> abList = new ArrayList<>(),
                actual1 = t.getStrWithPrefix("Top Dog");
        /*actual1 = t.getStrWithPrefix("ea");*/

        abList.add("Top Dog");
        //abList.add("e Avenue");
        //abList.add("Monroe Street");

        assertEquals(abList, actual1);

    }
}



