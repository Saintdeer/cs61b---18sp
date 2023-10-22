package hw4.puzzle;

import edu.princeton.cs.algs4.Queue;

public class Board implements WorldState {
    private final int BLANK = 0;
    private final int[][] board;
    private final int size;

    public Board(int[][] tiles) {
        size = tiles.length;
        board = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(tiles[i], 0, board[i], 0, size);
        }
    }

    public int tileAt(int i, int j) {
        if (i < 0 || j < 0 || i >= size || j >= size) {
            throw new IndexOutOfBoundsException();
        }
        return board[i][j];
    }

    public int size() {
        return size;
    }

    public int hamming() {
        int num = 0;
        int n = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++, n++) {
                if (board[i][j] == BLANK) {
                    continue;
                }
                if (board[i][j] != n) {
                    num += 1;
                }
            }
        }
        return num;
    }

    public int manhattan() {
        int total = 0;
        int correctNum = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++, correctNum++) {
                if (board[i][j] == 0) {
                    continue;
                }
                if (board[i][j] != correctNum) {
                    int number = board[i][j] - 1;
                    int x = number / size;
                    int y = number % size;
                    total += Math.abs(x - i) + Math.abs(y - j);
                }
            }
        }
        return total;
    }

    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    @Override
    public boolean equals(Object y) {
        if (this == y) {
            return true;
        }
        if (y == null || getClass() != y.getClass()) {
            return false;
        }

        Board y1 = (Board) y;
        if (y1.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] != y1.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    // Source: http://joshh.ug/neighbors.html
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == BLANK) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = BLANK;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = BLANK;
                }
            }
        }
        return neighbors;
    }

    /**
     * Returns the string representation of the board.
     * Uncomment this method.
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N).append("\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
