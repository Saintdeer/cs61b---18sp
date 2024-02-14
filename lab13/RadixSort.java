import org.junit.Test;

/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 */
public class RadixSort {
    static int length = 0;
    static int radix = 256;
    static String[] previousAsciis;

    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     * @return String[] the sorted array
     */
    public static String[] sort(String[] asciis) {
        // Implement LSD Sort

        // compute length of the longest string and initialize the copy of asciis
        previousAsciis = new String[asciis.length];
        for (int i = 0; i < asciis.length; i++) {
            previousAsciis[i] = asciis[i];
            if (asciis[i].length() > length) {
                length = asciis[i].length();
            }
        }

        // iterate asciis digit by digit
        for (int d = 0; d < length; d++) {
            sortHelperLSD(previousAsciis, d);
        }

        return previousAsciis;
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     *
     * @param asciis Input array of Strings
     * @param index  The position to sort the Strings on.
     */
    private static void sortHelperLSD(String[] asciis, int index) {
        // Optional LSD helper method for required LSD radix sort

        // compute counts array
        int[] counts = new int[radix];
        for (String ascii : asciis) {
            int offSet = length - ascii.length();
            int i = 0; // default ascii of each char
            if (offSet <= index) {
                i = ascii.charAt(ascii.length() - (index - offSet) - 1);
            }
            counts[i]++;
        }

        // compute starts array according to counts
        int[] starts = new int[radix];
        int pos = 0;
        for (int i = 0; i < starts.length; i += 1) {
            starts[i] = pos;
            pos += counts[i];
        }

        // put the asciis into the right place according to the starts
        String[] sorted = new String[asciis.length];
        for (String s : asciis) {
            int offSet = length - s.length();
            int i = 0;
            if (offSet <= index) {
                i = s.charAt(s.length() - (index - offSet) - 1);
            }
            sorted[starts[i]] = s;
            starts[i]++;
        }
        previousAsciis = sorted;
    }

    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start  int for where to start sorting in this method (includes String at start)
     * @param end    int for where to end sorting in this method (does not include String at end)
     * @param index  the index of the character the method is currently sorting on
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }

    @Test
    public void testNaiveWithNonNegative() {
        char[] a = {0, 1};
        String[] s = {"", String.valueOf(a), "", "y ", "à   "};
        String[] sorted = RadixSort.sort(s);
    }
}
