package byog.lab5;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final Random RANDOM = new Random(147);

    public static void addBigHexagon(TETile[][] world, Position p, int size) {
        if (size < 2) {
            throw new IllegalArgumentException("size can't be less than 2.");
        }
        int length = size;
        for (int i = 0; true; i++, length++, p.getTopRight(size)) {
            Position pos = Position.copy(p);
            drawDiagonalLine(world, pos, size, length);
            if (i == (size - 1)) {
                break;
            }
        }
        for (int i = 0; i <= (size - 2); i++) {
            length--;
            p.getTop(size);
            Position pos = Position.copy(p);
            drawDiagonalLine(world, pos, size, length);
        }
    }

    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(3);
        return switch (tileNum) {
            case 0 -> Tileset.WALL;
            case 1 -> Tileset.FLOWER;
            case 2 -> Tileset.TREE;
            default -> Tileset.SAND;
        };
    }

    private static void drawDiagonalLine(TETile[][] world, Position p, int size, int length) {
        for (int i = 0; i < length; i++, p.getTopLeft(size)) {
            addHexagon(world, p, size, randomTile());
        }
    }

    // p specifies the lower left corner of the hexagon.
    public static void addHexagon(TETile[][] world, Position p, int s, TETile t) {
        if (s < 2) {
            throw new IllegalArgumentException("size can't be less than 2.");
        }
        int x = p.x;
        int y = p.y;
        int length = s;
        int sign = 1;
        for (int i = 0; i < 2; i++) {
            for (int n = 1; true; x -= sign, y++, length += 2 * sign, n++) {
                drawRow(world, x, y, x + length, t);
                if (n == s) {
                    y++;
                    break;
                }
            }
            sign = -1;
        }
    }

    private static void drawRow(TETile[][] world, int xPosition, int y, int boundary, TETile t) {
        if (boundary <= 0) {
            return;
        }
        for (int x = xPosition; x < boundary; x++) {
            if (x < 0) {
                continue;
            }
            world[x][y] = t;
        }
    }
}
