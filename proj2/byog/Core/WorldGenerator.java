package byog.Core;

// import byog.TileEngine.TERenderer;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.io.Serializable;
import java.util.Random;

import static byog.Core.RandomUtils.uniform;


public class WorldGenerator implements Serializable {
    private int usage;
    private final double usageRate = 0.7;
    private int WIDTH;
    private int HEIGHT;
    private static Random RANDOM;
    private int direction;

    TETile[][] map;
    Position self;

    public WorldGenerator(long seed) {
        usage = 0;
        System.out.println(seed);
        RANDOM = new Random(seed);
        direction = 1;
    }

    public TETile[][] initialize(TETile[][] world, int width, int height) {
        WIDTH = width;
        HEIGHT = height;

        Position initPos = new Position(uniform(RANDOM, 10), uniform(RANDOM, 10));
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        self = new Position(initPos.x + 1, initPos.y + 1);

        boolean play = true;
        int i = 1;
        while ((usage * 1.0 / (HEIGHT * WIDTH) < usageRate) && play) {
            int construction = uniform(RANDOM, 3);
            switch (construction) {
                case 0:
                    play = addRoom(world, initPos);
                    continue;
                case 1:
                    play = addHallWay(world, initPos);
                    continue;
                default:
                    play = addHallWay(world, initPos);
            }
        }
        world[self.x][self.y] = Tileset.PLAYER;

        map = world;
        return world;
    }

    private boolean addHallWay(TETile[][] world, Position p) {
        int length = uniform(RANDOM, 10, 18);
        int width = 3;
        return addSpace(world, p, width, length, uniform(RANDOM, 3));
    }

    private boolean addRoom(TETile[][] world, Position p) {
        int width = uniform(RANDOM, 6, 11);
        int height = 15 - width;
        return addSpace(world, p, width, height, 0);
    }

    private boolean addSpace(TETile[][] world, Position p, int width,
                             int height, int intersection) {
        if (intersection > 0) {
            int length = uniform(RANDOM, 10, 18);
            int xPos = p.x + 1 - (int) Math.round(length * 0.5);
            int yPos = p.y + (int) Math.round((height - 4) * 0.5) * direction;
            Position point = new Position(Math.max(0, xPos), yPos);
            int originalDirection = direction;
            addSpace(world, point, length, 3, 0);
            direction = originalDirection;
        }
        addLine(world, p, width, true);
        int i = 1;
        int x = p.x, y;
        for (; i < (height - 1); i++) {
            y = p.y + i * direction;
            if (outOfIndex(x, y + direction)) {
                break;
            }
            addLine(world, new Position(x, y), width, false);
        }
        Position newP = addLine(world, new Position(x, p.y + i * direction), width, true);
        return rightPosition(p, newP);
    }

    private Position addLine(TETile[][] world, Position p, int length, boolean wall) {
        int i = 0;
        for (int y = p.y; i < length; i++) {
            int x = p.x + i;
            if (outOfIndex(x, y)) {
                break;
            }
            if (world[x][y] == Tileset.FLOOR) {
                continue;
            }
            if (wall || i == 0 || i == (length - 1) || outOfIndex(x + 1, y)) {
                world[x][y] = Tileset.WALL;
            } else {
                world[x][y] = Tileset.FLOOR;
            }
            usage += 1;
        }
        return new Position(p.x + i - 1, p.y);
    }

    private boolean rightPosition(Position origin, Position newP) {
        double leftUsageRate = usage * 1.0 / (newP.x * HEIGHT);
        boolean lowUsage = leftUsageRate < usageRate;

        boolean xCloseToUpperBorder = (WIDTH - newP.x) < 5;
        boolean yCloseToUpperBorder = (HEIGHT - newP.y) < 5;
        boolean xCloseToLowerBorder = newP.x < 5;
        boolean yCloseToLowerBorder = newP.y < 5;

        if (newP.x < 0 || newP.y < 0) {
            return false;
        }

        if (direction == 1) {
            if (xCloseToUpperBorder && yCloseToUpperBorder) {
                return false;
            }

            if (yCloseToUpperBorder) {
                direction = -1;
            }

            if ((!xCloseToLowerBorder && lowUsage) || xCloseToUpperBorder) {
                origin.changeToTopLeft(newP, uniform(RANDOM, 2));
            } else {
                origin.changeToTopRight(newP);
            }
        } else {
            if ((xCloseToLowerBorder && yCloseToLowerBorder)) {
                return false;
            }

            if (yCloseToLowerBorder) {
                direction = 1;
            }

            if ((!xCloseToLowerBorder && lowUsage) || xCloseToUpperBorder) {
                origin.changeToLowLeft(newP, uniform(RANDOM, 2));
            } else {
                origin.changeToLowRight(newP);
            }
        }
        return true;
    }

    public boolean interact(char key) {
        switch (Character.toUpperCase(key)) {
            case 'W':
                move(0, 1);
                break;
            case 'A':
                move(-1, 0);
                break;
            case 'S':
                move(0, -1);
                break;
            case 'D':
                move(1, 0);
                break;
            case ':':
                return true;
            default:
                break;
        }
        return false;
    }

    public void aHUD(int x, int y, TERenderer ter) {
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont();

        if (outOfIndex(x, y)) {
            return;
        }
        TETile tile = map[x][y];
        if (tile.equals(Tileset.WALL)) {
            StdDraw.text(1, HEIGHT - 1, "Wall");
        } else if (tile.equals(Tileset.FLOOR)) {
            StdDraw.text(1, HEIGHT - 1, "Floor");
        }
        StdDraw.show();
    }

    private void move(int x, int y) {
        int xPos = self.x + x, yPos = self.y + y;
        if (outOfIndex(xPos, yPos)) {
            return;
        }
        TETile destination = map[xPos][yPos];
        if (destination.equals(Tileset.WALL)) {
            return;
        }

        map[self.x][self.y] = Tileset.FLOOR;
        self.x = xPos;
        self.y = yPos;
        map[xPos][yPos] = Tileset.PLAYER;
    }

    private boolean outOfIndex(int x, int y) {
        return x >= WIDTH || y >= HEIGHT || x < 0 || y < 0;
    }
}
