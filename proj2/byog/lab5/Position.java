package byog.lab5;

public class Position {
    int x;
    int y;

    public static Position copy(Position p) {
        Position pos = new Position();
        pos.x = p.x;
        pos.y = p.y;
        return pos;
    }

    private void changePosition(int size, int sign) {
        x += (2 * size - 1) * sign;
        y += size;
    }

    public void getTopLeft(int size) {
        this.changePosition(size, -1);
    }

    public void getTopRight(int size) {
        this.changePosition(size, 1);
    }

    public void getTop(int size) {
        y += 2 * size;
    }
}
