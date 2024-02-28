package lab14;

import lab14lib.Generator;

public class SawToothGenerator implements Generator {
    private final int period;
    private int state = 0;
    private final double coefficient;

    public SawToothGenerator(int period) {
        this.period = period;
        coefficient = (double) 2 / period;
    }

    public double next() {
        state = (state + 1) % period;
        return coefficient * state - 1;
    }
}
