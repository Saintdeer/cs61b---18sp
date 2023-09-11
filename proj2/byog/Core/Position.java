package byog.Core;

import java.io.Serial;
import java.io.Serializable;

public class Position implements Serializable {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private int rationalize(int number) {
        return Math.max(number, 0);
    }

    public void changeToTopLeft(Position newP, int offset) {
        this.x = rationalize(this.x - offset);
        this.y = rationalize(newP.y - 2);
    }

    public void changeToTopRight(Position newP) {
        this.x = newP.x - 1;
        this.y = newP.y - 2;
    }

    public void changeToLowLeft(Position newP, int offset) {
        this.x = rationalize(this.x - offset);
        this.y = rationalize(newP.y + 2);
    }

    public void changeToLowRight(Position newP) {
        this.x = rationalize(newP.x - 1);
        this.y = rationalize(newP.y + 2);
    }
}
