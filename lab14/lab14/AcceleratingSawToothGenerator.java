package lab14;

import lab14lib.Generator;

public class AcceleratingSawToothGenerator implements Generator {
    private int period;
    private final double factor;
    private int state = 0;
    private double coefficient;

    public AcceleratingSawToothGenerator(int period, double factor) {
        this.period = period;
        this.factor = factor;
        coefficient = (double) 2 / period;
    }

    public double next() {
        state = (state + 1) % period;
        if (state == 0) {
            period = (int) Math.floor(period * factor);
            coefficient = (double) 2 / period;
            // System.out.println(period);
        }
        return coefficient * state - 1;
    }
}
