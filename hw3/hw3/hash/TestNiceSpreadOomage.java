package hw3.hash;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.ArrayList;

import edu.princeton.cs.algs4.StdRandom;

public class TestNiceSpreadOomage {
    private static class NiceSpreadOomage implements Oomage {
        private int val;

        @Override
        public void draw(double x, double y, double scalingFactor) {
            return;
        }

        @Override
        public int hashCode() {
            return val;
        }

        @Override
        public boolean equals(Object o) {
            // Write this method.
            if (this == o) {
                return true;
            }
            if (o == null || o.getClass() != this.getClass()) {
                return false;
            }
            NiceSpreadOomage obj = (NiceSpreadOomage) o;
            return obj.val == this.val;
        }

        public static NiceSpreadOomage randomNiceSpreadOomage() {
            NiceSpreadOomage x = new NiceSpreadOomage();
            x.val = StdRandom.uniform(0, 1000000);
            return x;
        }

    }

    /**
     * After you've written haveNiceHashCodeSpread,
     * run this and it should pass.
     */
    @Test
    public void testRandomOomagesHashCodeSpread() {
        List<Oomage> oomages = new ArrayList<>();
        int N = 10000;

        for (int i = 0; i < N; i += 1) {
            oomages.add(NiceSpreadOomage.randomNiceSpreadOomage());
        }

        assertTrue(OomageTestUtility.haveNiceHashCodeSpread(oomages, 10));
    }
}
