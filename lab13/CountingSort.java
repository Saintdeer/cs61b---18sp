/**
 * Class with 2 ways of doing Counting sort, one naive way and one "better" way
 *
 * @author Akhil Batra, Alexander Hwang
 **/
public class CountingSort {
    /**
     * Counting sort on the given int array. Returns a sorted version of the array.
     * Does not touch original array (non-destructive method).
     * DISCLAIMER: this method does not always work, find a case where it fails
     *
     * @param arr int array that will be sorted
     * @return the sorted array
     */
    public static int[] naiveCountingSort(int[] arr) {
        // find max
        int max = Integer.MIN_VALUE;
        for (int i : arr) {
            max = max > i ? max : i;
        }

        // gather all the counts for each value
        int[] counts = new int[max + 1];
        for (int i : arr) {
            counts[i]++;
        }

        // when we're dealing with ints, we can just put each value
        // count number of times into the new array
        int[] sorted = new int[arr.length];
        int k = 0;
        for (int i = 0; i < counts.length; i += 1) {
            for (int j = 0; j < counts[i]; j += 1, k += 1) {
                sorted[k] = i;
            }
        }

        // however, below is a more proper, generalized implementation of
        // counting sort that uses start position calculation
        int[] starts = new int[max + 1];
        int pos = 0;
        for (int i = 0; i < starts.length; i += 1) {
            starts[i] = pos;
            pos += counts[i];
        }

        int[] sorted2 = new int[arr.length];
        for (int i = 0; i < arr.length; i += 1) {
            int item = arr[i];
            int place = starts[item];
            sorted2[place] = item;
            starts[item] += 1;
        }

        // return the sorted array
        return sorted;
    }

    /**
     * Counting sort on the given int array, must work even with negative numbers.
     * Note, this code does not need to work for ranges of numbers greater
     * than 2 billion.
     * Does not touch original array (non-destructive method).
     *
     * @param arr int array that will be sorted
     */
    public static int[] betterCountingSort(int[] arr) {
        // make counting sort work with arrays containing negative numbers.

        // find max
        int max = Integer.MIN_VALUE;
        for (int i : arr) {
            max = Math.max(max, i);
        }

        int[] nonNegativeCounts = null;
        if (max >= 0) {
            nonNegativeCounts = new int[max + 1];
        }

        // find min
        int min = Integer.MAX_VALUE;
        for (int i : arr) {
            min = Math.min(min, i);
        }

        int[] negativeCounts = null;
        if (min < 0) {
            negativeCounts = new int[-min + 1];
        }

        for (int i : arr) {
            if (i < 0) {
                assert negativeCounts != null;
                negativeCounts[-i]++;
            } else {
                assert nonNegativeCounts != null;
                nonNegativeCounts[i]++;
            }
        }

        int pos = 0;
        int[] negativeStarts = null;
        if (min < 0) {
            negativeStarts = new int[-min + 1];
            for (int i = negativeStarts.length - 1; i > 0; i -= 1) {
                negativeStarts[i] = pos;
                pos += negativeCounts[i];
            }
        }

        int[] nonNegativeStarts = null;
        if (max >= 0) {
            nonNegativeStarts = new int[max + 1];
            for (int i = 0; i < nonNegativeStarts.length; i += 1) {
                nonNegativeStarts[i] = pos;
                pos += nonNegativeCounts[i];
            }
        }

        int[] sorted = new int[arr.length];
        for (int item : arr) {
            int place;
            if (item < 0) {
                assert negativeStarts != null;
                place = negativeStarts[-item];
                negativeStarts[-item] += 1;
            } else {
                assert nonNegativeStarts != null;
                place = nonNegativeStarts[item];
                nonNegativeStarts[item] += 1;
            }
            sorted[place] = item;
        }

        return sorted;
    }
}
