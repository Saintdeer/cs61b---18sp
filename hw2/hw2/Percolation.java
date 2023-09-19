package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF nxn;
    private final WeightedQuickUnionUF shadow;
    private final int[] sites;
    private final int length;
    private int size;

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException();
        }
        nxn = new WeightedQuickUnionUF(N * N);
        shadow = new WeightedQuickUnionUF(N * N);
        for (int i = 0; i < N - 1; i++) {
            nxn.union(i, i + 1);
            shadow.union(i, i + 1);
        }
        for (int i = N * (N - 1); i < N * N - 1; i++) {
            nxn.union(i, i + 1);
        }
        length = N;
        sites = new int[N * N];
        size = 0;
    }                // create N-by-N grid, with all sites initially blocked

    private int computeIndex(int row, int col) {
        return row * length + col;
    }

    private boolean indexOut(int row, int col) {
        return row < 0 || col < 0 || row >= length || col >= length;
    }

    private void exception(int row, int col) {
        if (indexOut(row, col)) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void open(int row, int col) {
        exception(row, col);

        if (isOpen(row, col)) {
            return;
        }
        sites[computeIndex(row, col)] = 1;
        size += 1;
        connectAround(row, col);
    }      // open the site (row, col) if it is not open already

    private void checkAround(int index, int row, int col) {
        if (indexOut(row, col) || !isOpen(row, col)) {
            return;
        }
        int otherIndex = computeIndex(row, col);
        nxn.union(index, otherIndex);
        shadow.union(index, otherIndex);
    }

    private void connectAround(int row, int col) {
        int index = computeIndex(row, col);
        checkAround(index, row - 1, col);
        checkAround(index, row + 1, col);
        checkAround(index, row, col - 1);
        checkAround(index, row, col + 1);
    }

    public boolean isOpen(int row, int col) {
        exception(row, col);
        return sites[computeIndex(row, col)] == 1;
    }  // is the site (row, col) open?

    public boolean isFull(int row, int col) {
        exception(row, col);
        int index = computeIndex(row, col);
        return isOpen(row, col) && nxn.connected(0, index) && shadow.connected(0, index);
    }  // is the site (row, col) full?

    public int numberOfOpenSites() {
        return size;
    }           // number of open sites

    public boolean percolates() {
        return nxn.connected(0, length * length - 1);
    }              // does the system percolate?

    public static void main(String[] args) {
    }   // use for unit testing (not required)
}
