package byog.Core;

// import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
// import byog.TileEngine.Tileset;

/**
 * This is the main entry point for the program. This class simply parses
 * the command line inputs, and lets the byog.Core.Game class take over
 * in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            Game game = new Game();
            TETile[][] worldState = game.playWithInputString(args[0]);
            System.out.println(TETile.toString(worldState));
        } else {
            Game game = new Game();
            game.playWithKeyboard();
        }

        /*int WIDTH = 80;
        int HEIGHT = 30;

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        WorldGenerator wg = new WorldGenerator(963);
        wg.initialize(world, WIDTH, HEIGHT);


        ter.renderFrame(world);*/
    }
}
