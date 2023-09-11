package byog.lab6;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.text.StringCharacterIterator;
import java.util.Random;


public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Font font = new Font("Monaco", Font.BOLD, 30);
    private Font smallFont = new Font("Monaco", Font.PLAIN, 22);
    private final Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!", "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, int seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Initialize random number generator
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        // Generate random string of letters of length n
        char[] str = new char[n];
        for (int i = 0; i < n; i++) {
            str[i] = CHARACTERS[rand.nextInt(0, CHARACTERS.length)];
        }
        return new String(str);
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        if (gameOver) {
            StdDraw.clear(Color.BLACK);
        }
        StdDraw.setFont(font);
        StdDraw.text(1.0 * width / 2, 1.0 * height / 2, s);
        StdDraw.show();
        // If game is not over, display relevant game information at the top of the screen
    }

    public void flashSequence(String letters) {
        // Display each character in letters, making sure to blank the screen between letters
        StdDraw.clear(Color.BLACK);
        int length = letters.length();
        StringCharacterIterator iterator = new StringCharacterIterator(letters);

        StdDraw.setFont(font);
        for (int i = 0; i < length; i++) {
            UI(true);
            StdDraw.text(1.0 * width / 2, 1.0 * height / 2, String.valueOf((iterator.current())));
            StdDraw.show();
            StdDraw.pause(1000);
            if (i == length - 1) {
                UI(false);
                break;
            }
            UI(true);
            StdDraw.pause(500);
            iterator.next();
        }
    }

    private void UI(boolean watch) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(smallFont);
        StdDraw.textLeft(0, height - 1, "Round: " + round);
        String s = "Watch!";
        if (!watch) {
            s = "Type!";
        }
        StdDraw.text(1.0 * width / 2, 1.0 * height - 1, s);
        String encrgmt = ENCOURAGEMENT[(rand.nextInt(0, ENCOURAGEMENT.length))];
        StdDraw.textRight(width - 1, height - 1, encrgmt);
        StdDraw.line(0, height - 2, width, height - 2);
        StdDraw.setFont(font);
        StdDraw.show();
    }

    public String solicitNCharsInput(int n) {
        // Read n letters of player input
        StdDraw.clear(Color.BLACK);
        UI(false);
        StringBuilder input = new StringBuilder();

        while (n > 0) {
            if (StdDraw.hasNextKeyTyped()) {
                input.append(StdDraw.nextKeyTyped());
                UI(false);
                drawFrame(input.toString());
                n--;
            }
        }
        return input.toString();
    }

    public void startGame() {
        // Set any relevant variables before the game starts
        round = 1;
        gameOver = false;

        // Establish Game loop
        while (!gameOver) {
            String str = generateRandomString(round);
            flashSequence(str);
            UI(false);

            String input = solicitNCharsInput(round);
            StdDraw.pause(500);
            if (!str.equals(input)) {
                gameOver = true;
                drawFrame("Game Over! You made it to round: " + round);
                return;
            }
            round++;
            UI(false);
            drawFrame("Round: " + round);
            StdDraw.pause(1000);
        }
    }
}
