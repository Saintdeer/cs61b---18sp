import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import edu.princeton.cs.introcs.In;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Boggle {
    static String dictPath = "words.txt";
    private static int width, height;
    private static Set<String> stringSet;
    private static boolean[][] visited;
    private static char[][] chars;
    private static Trie dictTrie;

    public static List<String> solve(int k, String boardFilePath) {
        stringSet = new HashSet<>();
        check(k, new In(boardFilePath));

        visited = new boolean[width][height];
        chars = new char[width][height];

        dictTrie = new Trie();
        for (String str : new In(dictPath).readAllStrings()) {
            dictTrie.addString(str);
        }

        int row1 = 0;
        for (String str : new In(boardFilePath).readAllStrings()) {
            for (int column1 = 0; column1 < width; column1++) {
                chars[column1][row1] = str.charAt(column1);
            }
            row1++;
        }

        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                String nextStr = String.valueOf(chars[column][row]);
                int result = dictTrie.hasPrefixOrString(nextStr);
                if (result == -1) {
                    continue;
                } else if (result == 1) {
                    stringSet.add(nextStr);
                }
                dfs(column, row, nextStr);
            }
        }
        String[] stringsSorted = RadixSort.sort(stringSet.toArray(new String[0]));
        k = Math.min(k, stringSet.size());
        return Arrays.asList(Arrays.copyOfRange(stringsSorted, 0, k));
    }

    static void dfs(int column, int row, String prefix) {
        visited[column][row] = true;

        List<Integer> columns = new LinkedList<>(), rows = new LinkedList<>();
        if (column > 0) {
            columns.add(column - 1);
        }
        if (column < width - 1) {
            columns.add(column + 1);
        }
        columns.add(column);

        if (row > 0) {
            rows.add(row - 1);
        }
        if (row < height - 1) {
            rows.add(row + 1);
        }
        rows.add(row);

        for (int c : columns) {
            for (int r : rows) {
                if (visited[c][r]) {
                    continue;
                }
                String nextString = prefix + chars[c][r];
                int result = dictTrie.hasPrefixOrString(nextString);
                if (result == -1) {
                    continue;
                } else if (result == 1) {
                    stringSet.add(nextString);
                }
                dfs(c, r, nextString);
            }
        }
        visited[column][row] = false;
    }

    static void check(int k, In board) {
        In dict = new In(dictPath);

        if (!dict.exists() || !board.exists() || board.isEmpty() || k <= 0) {
            throw new IllegalArgumentException();
        }

        String[] boardStr = board.readAllLines();
        height = boardStr.length;
        width = boardStr[0].length();

        for (String str : boardStr) {
            if (str.length() != width) {
                throw new IllegalArgumentException();
            }
        }
    }

    @Test
    public void simpleTest() {
        String[] expected, actual;
        expected = new String[]{"thumbtacks", "thumbtack", "setbacks",
            "setback", "ascent", "humane", "smacks"};
        actual = Boggle.solve(7, "exampleBoard.txt").toArray(new String[0]);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void test100x100() {
        Boggle.solve(7, "smallBoard.txt");
    }

    @Test
    public void test50x50() {
        Boggle.solve(7, "smallBoard2.txt");
    }
}
