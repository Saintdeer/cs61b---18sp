import org.junit.Test;

/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 */
public class RadixSort {
    static int length = 0;
    static int radix = 256;
    static String[] lastAsciis;

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
        lastAsciis = asciis;
        for (String ascii : asciis) {
            if (ascii.length() > length) {
                length = ascii.length();
            }
        }

        for (int d = 0; d < length; d++) {
            sortHelperLSD(asciis, d);
        }
        return lastAsciis;
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
        int[] counts = new int[radix];
        for (String ascii : lastAsciis) {
            int offSet = length - ascii.length();
            int i = ' ';
            if (offSet <= index) {
                i = ascii.charAt(ascii.length() - (index - offSet) - 1);
            }
            counts[i]++;
        }

        int[] starts = new int[radix];
        int pos = 0;
        for (int i = 0; i < starts.length; i += 1) {
            starts[i] = pos;
            pos += counts[i];
        }

        String[] sorted = new String[asciis.length];
        for (String s : lastAsciis) {
            int offSet = length - s.length();
            int i = ' ';
            if (offSet <= index) {
                i = s.charAt(s.length() - (index - offSet) - 1);
            }
            sorted[starts[i]] = s;
            starts[i]++;
        }
        lastAsciis = sorted;
        return;
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
        String[] s = {"bcf", "bcd", "a", "_"};
        String[] sorted = RadixSort.sort(s);
    }
}
