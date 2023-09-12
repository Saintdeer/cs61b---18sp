package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.Random;

import static byog.Core.RandomUtils.uniform;

public class WorldGenerator implements Serializable {
    private int usage;
    private final double usageRate = 0.8;
    private int WIDTH;
    private int HEIGHT;
    private static Random RANDOM;
    private final Random random;
    private int direction;

    TETile[][] map;
    Position self;
    Position ghost;
    boolean hasGhost = false;
    boolean hasWater = false;

    public WorldGenerator(long seed) {
        usage = 0;
        random = new Random(seed);
        RANDOM = random;
        direction = 1;
    }

    public void initialize(TETile[][] world, int width, int height) {
        WIDTH = width;
        HEIGHT = height;

        Position initPos = new Position(uniform(random, 10), uniform(random, 10));
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        self = new Position(initPos.x + 1, initPos.y + 1);

        boolean play = true;
        int i = 1;
        while ((usage * 1.0 / (HEIGHT * WIDTH) < usageRate) && play) {
            int construction = uniform(random, 3);
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
        if (!hasWater) {
            world[initPos.x][initPos.y] = Tileset.WATER;
            hasWater = true;
        }
        world[self.x][self.y] = Tileset.PLAYER;

        map = world;
    }

    private boolean addHallWay(TETile[][] world, Position p) {
        int length = uniform(random, 10, 18);
        int width = 3;
        return addSpace(world, p, width, length, uniform(random, 3));
    }

    private boolean addRoom(TETile[][] world, Position p) {
        int width = uniform(random, 6, 11);
        int height = 15 - width;
        return addSpace(world, p, width, height, 0);
    }

    private boolean addSpace(TETile[][] world, Position p, int width,
                             int height, int intersection) {
        if (intersection > 0) {
            int length = uniform(random, 10, 18);
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
                if ((usage * 1.0 / (HEIGHT * WIDTH) > usageRate * 0.9) && !hasWater) {
                    world[x][y] = Tileset.WATER;
                    hasWater = true;
                } else {
                    continue;
                }
            }
            if (wall || i == 0 || i == (length - 1) || outOfIndex(x + 1, y)) {
                if (world[x][y].equals(Tileset.WATER)) {
                    hasWater = false;
                }
                world[x][y] = Tileset.WALL;
            } else {
                if ((usage * 1.0 / (HEIGHT * WIDTH) > usageRate * 0.9) && !hasWater) {
                    world[x][y] = Tileset.WATER;
                    hasWater = true;
                } else if ((usage * 1.0 / (HEIGHT * WIDTH) > usageRate * 0.8) && !hasGhost) {
                    world[x][y] = Tileset.GHOST;
                    ghost = new Position(x, y);
                    hasGhost = true;
                } else {
                    world[x][y] = Tileset.FLOOR;
                }
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
                origin.changeToTopLeft(newP, uniform(random, 2));
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
                origin.changeToLowLeft(newP, uniform(random, 2));
            } else {
                origin.changeToLowRight(newP);
            }
        }
        return true;
    }

    public boolean interact(char key) {
        TETile character = Tileset.PLAYER;
        switch (Character.toUpperCase(key)) {
            case 'W':
                move(0, 1, character);
                break;
            case 'A':
                move(-1, 0, character);
                break;
            case 'S':
                move(0, -1, character);
                break;
            case 'D':
                move(1, 0, character);
                break;
            case ':':
                return true;
            default:
                break;
        }
        if (hasGhost && uniform(random, 0, 2) == 0) {
            move(uniform(random, -4, 5), uniform(random, -4, 5), Tileset.GHOST);
        }

        checkGameOver();
        return false;
    }

    private void checkGameOver() {
        int xLength = Math.abs(self.x - ghost.x);
        int yLength = Math.abs(self.y - ghost.y);
        if (Math.sqrt(Math.pow(xLength, 2) + Math.pow(yLength, 2)) < 5) {
            endUI(false);
        }

        int x = self.x;
        int y = self.y;
        TETile w = Tileset.WATER;
        boolean sign = map[x - 1][y].equals(w) || map[x + 1][y].equals(w)
                || map[x][y + 1].equals(w) || map[x][y - 1].equals(w);
        if (sign) {
            endUI(true);
        }
    }

    private void endUI(boolean win) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));

        String text;
        if (win) {
            text = "Congratulations! you find water and survived!";
        } else {
            text = "good bye ~ ~ the ghost finds you ~ ~";
        }
        StdDraw.text((double) WIDTH / 2, (double) HEIGHT / 2, text);
        StdDraw.show();
        StdDraw.pause(3 * 1000);
        Game game = new Game();
        game.playWithKeyboard();
    }

    public void aHUD(int x, int y, TERenderer ter) {
        StdDraw.enableDoubleBuffering();
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont();

        if (outOfIndex(x, y)) {
            return;
        }

        StdDraw.text(1, HEIGHT - 1, map[x][y].description());
        StdDraw.show();
    }

    private void move(int x, int y, TETile character) {
        Position p;
        if (character.equals(Tileset.PLAYER)) {
            p = self;
        } else {
            p = ghost;
        }
        int xPos = p.x + x, yPos = p.y + y;
        if (outOfIndex(xPos, yPos)) {
            return;
        }
        TETile destination = map[xPos][yPos];
        if (!destination.equals(Tileset.FLOOR)) {
            return;
        }

        map[p.x][p.y] = Tileset.FLOOR;
        p.x = xPos;
        p.y = yPos;
        map[xPos][yPos] = character;
    }

    private boolean outOfIndex(int x, int y) {
        return x >= WIDTH || y >= HEIGHT || x < 0 || y < 0;
    }
}
