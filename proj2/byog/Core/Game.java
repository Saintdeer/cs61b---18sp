package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private final long upperBound = 9223372036854775807L;
    private final Font font = new Font("Monaco", Font.BOLD, 30);

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        mainMenu(1);

        WorldGenerator wg = null;
        char input;
        boolean startMenu = true;
        while (startMenu) {
            if(StdDraw.hasNextKeyTyped()) {
                input = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (input == 'N') {
                    mainMenu(2);
                    wg = getWorldGenerator();
                    startMenu = false;
                }else if(input == 'L'){
                    wg = load();
                    startMenu = false;
                }
            }
        }

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        assert wg != null;
        ter.renderFrame(wg.map);

        boolean attention = false;
        while (true) {
            wg.HUD((int)Math.round(StdDraw.mouseX()), (int)Math.round(StdDraw.mouseY()), ter);
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if(attention && Character.toUpperCase(key) == 'Q'){
                    save(wg);
                    System.exit(0);
                }
                if (!wg.interact(key)) {
                    attention = true;
                }
            }
            ter.renderFrame(wg.map);
        }
    }

    public WorldGenerator getWorldGenerator(){
        StringBuilder seed = new StringBuilder();
        while(true) {
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                if (Character.toUpperCase(key) == 'S') {
                    String seeds = seed.toString();
                    if (seeds.isEmpty()) {
                        System.exit(0);
                    }
                    return worldMap(new BigInteger(seed.toString()));
                    }
                seed.append(key);
            }
        }
    }

    private void mainMenu(int choice) {
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(font);
        StdDraw.text((double) WIDTH / 2, 3.0* HEIGHT / 4, "A GAME");
        StdDraw.setFont();

        String text;
        if (choice == 1) {
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2+1, "New Game (N)");
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, "Load Game (L)");
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2-1, "Quit (Q)");
        }else{
            text = "Please enter a seed, and end up with 's'.";
            StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, text);
        }
        StdDraw.show();
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
        char first = Character.toUpperCase(input.charAt(0));

        if (length < 3 || (first != 'N' && first != 'L')) {
            System.exit(0);
        }

        int digitEnd = 0, i = 1;
        WorldGenerator wg = null;

        if (first == 'L') {
            wg = load();
        } else {
            for (; i < length - 1; i++) {
                if (Character.isDigit(input.charAt(i))) {
                    digitEnd++;
                } else if (Character.toUpperCase(input.charAt(i)) == 'S') {
                    break;
                } else {
                    System.exit(0);
                }
            }

            String number = input.substring(1, digitEnd);
            BigInteger bigInteger = new BigInteger(number);

            wg = worldMap(bigInteger);
        }

        if (i <= length - 1) {
            String instructions = input.substring(i + 1, length - 1);
            int strLength = instructions.length();
            for (int n = 0; n < strLength - 1; n++) {
                assert wg != null;
                if (!wg.interact(instructions.charAt(n))) {
                    if (Character.toUpperCase(instructions.charAt(n + 1)) == 'Q') {
                        save(wg);
                        System.exit(0);
                    }
                }
            }
        }
        assert wg != null;
        return wg.map;
    }

    private void save(WorldGenerator worldGenerator) {
        try {
            // Create an ObjectOutputStream
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    new FileOutputStream("worldGenerator.ser"));

            // Write the object to the stream
            outputStream.writeObject(worldGenerator);

            // Close the stream
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private WorldGenerator load() {
        WorldGenerator worldGenerator = null;
        try {
            // Create an ObjectInputStream
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream("worldGenerator.ser"));

            // Read the object from the stream
            worldGenerator = (WorldGenerator) inputStream.readObject();

            // Close the stream
            inputStream.close();

            return worldGenerator;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return worldGenerator;
    }

    private WorldGenerator worldMap(BigInteger seed) {
        int comparisonResult = seed.compareTo(BigInteger.valueOf(upperBound));
        if (comparisonResult > 0) {
            System.exit(0);
        }
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        WorldGenerator wg = new WorldGenerator(seed.longValue());
        wg.initialize(world, WIDTH, HEIGHT);
        return wg;
    }
}
