package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private final int[] num;

    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N < 30) {
            throw new IllegalArgumentException("N is at least 30.");
        }
        num = new int[T];
        int n2 = N * N;
        for (int i = 0; i < T; i++) {
            Percolation grid = pf.make(N);
            while (!grid.percolates()) {
                grid.open(StdRandom.uniform(0, n2), StdRandom.uniform(0, n2));
            }
            num[i] = grid.numberOfOpenSites();
        }
    }   // perform T independent experiments on an N-by-N grid

    public double mean() {
        return StdStats.mean(num);
    }                                           // sample mean of percolation threshold

    public double stddev() {
        return StdStats.stddev(num);
    }                                         // sample standard deviation of percolation threshold

    private double computeRest() {
        return 1.96 * Math.sqrt(stddev()) / Math.sqrt(num.length);
    }

    public double confidenceLow() {
        return mean() - computeRest();
    }                                  // low endpoint of 95% confidence interval

    public double confidenceHigh() {
        return mean() + computeRest();
    }                                 // high endpoint of 95% confidence interval

}
