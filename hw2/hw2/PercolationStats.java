package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private int[] num;

    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N < 1 || T < 1) {
            throw new IllegalArgumentException();
        }
        num = new int[T];
        int n2 = N * N;
        int t = T;
        if (T < 30) {
            t = 30;
        }
        for (int i = 0; i < t; i++) {
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
