public class ArrayDequeTest {
    /* Utility method for printing out empty checks. */
    public static boolean checkEmpty(boolean expected, boolean actual) {
        if (expected != actual) {
            System.out.println("isEmpty() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Utility method for printing out empty checks. */
    public static boolean checkSize(int expected, int actual) {
        if (expected != actual) {
            System.out.println("size() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Utility method for printing out getItem checks. */
    public static boolean checkItem(int expected, int actual) {
        if (expected != actual) {
            System.out.println("get() returned " + actual + ", but expected: " + expected);
            return false;
        }
        return true;
    }

    /* Prints a nice message based on whether a test passed.
     * The \n means newline. */
    public static void printTestStatus(boolean passed) {
        if (passed) {
            System.out.println("Test passed!\n");
        } else {
            System.out.println("Test failed!\n");
        }
    }

    /**
     * Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     * <p>
     * && is the "and" operation.
     */
    public static void addIsEmptySizeTest() {
        System.out.println("Running add/isEmpty/Size test.");

        ArrayDeque<String> ad1 = new ArrayDeque<>();

        boolean passed = checkEmpty(true, ad1.isEmpty());

        ad1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        passed = checkSize(1, ad1.size()) && passed;
        passed = checkEmpty(false, ad1.isEmpty()) && passed;

        ad1.addLast("middle");
        passed = checkSize(2, ad1.size()) && passed;

        ad1.addLast("back");
        passed = checkSize(3, ad1.size()) && passed;

        System.out.println("Printing out deque: ");
        ad1.printDeque();

        printTestStatus(passed);

    }

    /**
     * Adds an item, then removes an item, and ensures that dll is empty afterward.
     */
    public static void addRemoveTest() {

        System.out.println("Running add/remove test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        // should be empty
        boolean passed = checkEmpty(true, ad1.isEmpty());

        ad1.addFirst(10);
        // should not be empty
        passed = checkEmpty(false, ad1.isEmpty()) && passed;

        ad1.removeFirst();
        // should be empty
        passed = checkEmpty(true, ad1.isEmpty()) && passed;

        printTestStatus(passed);

    }

    public static void addAndGetTest() {

        System.out.println("Running add/get test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        ad1.addFirst(10);
        boolean passed = checkItem(10, ad1.get(7));

        ad1.removeFirst();
        // should be empty
        passed = checkEmpty(true, ad1.isEmpty()) && passed;

        printTestStatus(passed);

    }

    public static void resizeAndAddAndRemoveTest() {

        System.out.println("Running resize/add/remove/downsize test.");

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        int n = 17;
        for (int i = 0; i < n; i++) {
            ad1.addFirst(10);
        }

        for (int i = 0; i < 9; i++) {
            ad1.removeLast();
        }
        ad1.removeLast();

        System.out.println("resizing test passed.");

    }

    public static void main(String[] args) {
        System.out.println("Running tests.\n");
        addIsEmptySizeTest();
        addRemoveTest();
        addAndGetTest();
        resizeAndAddAndRemoveTest();
    }
}
