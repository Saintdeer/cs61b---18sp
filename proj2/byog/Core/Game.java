package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;

import java.math.BigInteger;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private final long upperBound = 9223372036854775807L;

    //private static int[] seeds =

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        //  Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().

        int length = input.length();
        char first = input.charAt(0);
        char last = input.charAt(input.length() - 1);
        if (length < 3 || length > 21 || !Character.isLetter(first) || !Character.isLetter(last)
                || Character.toUpperCase(first) != 'N' || Character.toUpperCase(last) != 'S') {
            System.exit(0);
        }

        StringBuilder s = new StringBuilder();
        for (int i = 1; i < input.length() - 1; i++) {
            if (!Character.isDigit(input.charAt(i))) {
                System.exit(0);
            }
        }

        String number = input.substring(1, input.length() - 1);
        BigInteger bigInteger = new BigInteger(number);

        int comparisonResult = bigInteger.compareTo(BigInteger.valueOf(upperBound));
        if (comparisonResult > 0) {
            System.exit(0);
        }
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        WorldGenerator wg = new WorldGenerator(bigInteger.longValue());
        TETile[][] finalWorldFrame = wg.initialize(world, WIDTH, HEIGHT);

        return finalWorldFrame;
    }
}
