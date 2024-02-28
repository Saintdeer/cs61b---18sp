package lab14;

import lab14lib.Generator;

public class StrangeBitwiseGenerator implements Generator {
    private final int period;
    private int state = 0;
    private final double coefficient;

    public StrangeBitwiseGenerator(int period) {
        this.period = period;
        coefficient = (double) 2 / period;
    }

    public double next() {
        state = state + 1;
        int weirdState = state & (state >> 3) & (state >> 8) % period;
        return coefficient * weirdState - 1;
    }
}
