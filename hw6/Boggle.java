import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.princeton.cs.introcs.In;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class Boggle {

    // File path of dictionary file
    static String dictPath = "words.txt";

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
        check(k, new In(dictPath), new In(boardFilePath));

        TrieDB dictTrie, boardTrie;
        dictTrie = new TrieDB(new In(dictPath));
        boardTrie = new TrieDB(dictTrie);

        TrieDB.Node currentNode = boardTrie.getRoot();

        int width, height;
        width = new In(boardFilePath).readLine().length();
        height = new In(boardFilePath).readAllLines().length;
        char[] chars = new char[width * height];

        int index1 = 0;
        In board = new In(boardFilePath);
        for (String str : board.readAllStrings()) {
            for (int index2 = 0; index2 < width; index2++) {
                chars[index1] = str.charAt(index2);
                boardTrie.addPosition(index1, chars[index1], currentNode, dictTrie);
                index1++;
            }
        }

        List<TrieDB.Node> nodes = TrieDB.getAllNextNodes(currentNode);

        while (!nodes.isEmpty()) {
            TrieDB.Node node = nodes.remove(0);

            Set<Integer> neighbors = getNeighbors(node.getPosition(), width, height);
            for (int neighbor : neighbors) {
                node.addNextPositionAndCheck(neighbor, chars[neighbor], dictTrie);
            }

            List<TrieDB.Node> nodeList = TrieDB.getAllNextNodes(node);
            for (TrieDB.Node nd : nodeList) {
                nodes.add(0, nd);
            }

            node.parentCheckAndPrune();
        }

        return boardTrie.getAllStrings(k);
    }

    static Set<Integer> getNeighbors(int position, int width, int height) {
        int row, column;
        row = position / width;
        column = position - row * width;

        int maxColumn, maxRow, leftColumn, rightColumn, upRow, downRow;
        maxColumn = width - 1;
        maxRow = height - 1;

        leftColumn = column > 0 ? column - 1 : 0;
        rightColumn = column < maxColumn ? column + 1 : maxColumn;
        upRow = row > 0 ? row - 1 : 0;
        downRow = row < maxRow ? row + 1 : maxRow;

        Set<Integer> neighbors = new HashSet<>();
        neighbors.add(upRow * width + leftColumn);
        neighbors.add(upRow * width + column);
        neighbors.add(upRow * width + rightColumn);
        neighbors.add(row * width + leftColumn);
        neighbors.add(row * width + rightColumn);
        neighbors.add(downRow * width + leftColumn);
        neighbors.add(downRow * width + column);
        neighbors.add(downRow * width + rightColumn);

        neighbors.remove(position);

        return neighbors;
    }

    static void check(int k, In dict, In board) {
        if (!dict.exists() || !board.exists() || board.isEmpty() || k <= 0) {
            throw new IllegalArgumentException();
        }

        int width = board.readLine().length();

        for (String str : board.readAllLines()) {
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
