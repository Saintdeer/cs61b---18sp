import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Set;

import edu.princeton.cs.introcs.In;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Boggle {

    // File path of dictionary file
    static String dictPath = "words.txt";
    private static int height, width;
    private static Set<String> stringSet;
    private static boolean[][] visited;
    private static char[][] chars;
    private static Trie dictTrie;

    /**
     * Solves a Boggle puzzle.
     *
     * @param k             The maximum number of words to return.
     * @param boardFilePath The file path to Boggle board file.
     * @return a list of words found in given Boggle board.
     * The Strings are sorted in descending order of length.
     * If multiple words have the same length,
     * have them in ascending alphabetical order.
     */
    public static List<String> solve(int k, String boardFilePath) {
        // YOUR CODE HERE
        checkAndInitialize(k, new In(boardFilePath));

        String[] strings = new In(boardFilePath).readAllStrings();
        int row1 = 0;
        for (String str : strings) {
            if (str.length() != width) {
                throw new IllegalArgumentException();
            }
            chars[row1] = str.toCharArray();
            row1++;
        }

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                String nextStr = String.valueOf(chars[row][column]);
                int result = dictTrie.hasPrefixOrString(nextStr);
                if (result == -1) {
                    continue;
                } else if (result == 1) {
                    stringSet.add(nextStr);
                }
                depthFirstSearch(row, column, nextStr);
            }
        }

        String[] stringsSorted = RadixSort.sort(stringSet.toArray(new String[0]));
        k = Math.min(k, stringSet.size());
        return Arrays.asList(Arrays.copyOfRange(stringsSorted, 0, k));
    }

    static void depthFirstSearch(int row, int column, String prefix) {
        visited[row][column] = true;

        List<Integer> row1s = new LinkedList<>(), column1s = new LinkedList<>();
        if (row > 0) {
            row1s.add(row - 1);
        }
        if (row < height - 1) {
            row1s.add(row + 1);
        }
        row1s.add(row);

        if (column > 0) {
            column1s.add(column - 1);
        }
        if (column < width - 1) {
            column1s.add(column + 1);
        }
        column1s.add(column);

        for (int r1 : row1s) {
            for (int c1 : column1s) {
                if (visited[r1][c1]) {
                    continue;
                }
                String nextString = prefix + chars[r1][c1];
                int result = dictTrie.hasPrefixOrString(nextString);
                if (result == -1) {
                    continue;
                } else if (result == 1) {
                    stringSet.add(nextString);
                }
                depthFirstSearch(r1, c1, nextString);
            }
        }
        visited[row][column] = false;
    }

    static void checkAndInitialize(int k, In board) {
        In dict = new In(dictPath);
        if (!dict.exists() || !board.exists() || board.isEmpty() || k <= 0) {
            throw new IllegalArgumentException();
        }

        String[] boardStr = board.readAllLines();
        height = boardStr.length;
        width = boardStr[0].length();

        stringSet = new HashSet<>();

        visited = new boolean[height][width];
        chars = new char[height][width];

        dictTrie = new Trie();
        String[] dictStrings = new In(dictPath).readAllStrings();
        for (String str : dictStrings) {
            dictTrie.addString(str);
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
        Boggle.solve(Integer.MAX_VALUE, "smallBoard.txt");
    }

    @Test
    public void test50x50() {
        Boggle.solve(Integer.MAX_VALUE, "smallBoard2.txt");
    }
}
