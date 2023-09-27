package hw3.hash;

import java.util.ArrayList;
import java.util.List;

public class HashTableVisualizer {

    public static void main(String[] args) {
        /* scale: StdDraw scale
           N:     number of items
           M:     number of buckets */

        /* After getting your simpleOomages to spread out
           nicely, be sure to try
           scale = 0.5, N = 2000, M = 100. */

        double scale = 1;
        int N = 2000;
        int M = 100;

        HashTableDrawingUtility.setScale(scale);
        /*List<Oomage> oomies = new ArrayList<>();
        for (int i = 0; i < N; i += 1) {
           oomies.add(SimpleOomage.randomSimpleOomage());
        }*/

        List<Oomage> deadlyList = new ArrayList<>();

        // Your code here.
        for (int i = 1; i < 10; i++) {
            List<Integer> lst = new ArrayList<>();
            for (int n = 0; n < i; n++) {
                lst.add(255);
            }
            deadlyList.add(new ComplexOomage(lst));
        }

        visualize(deadlyList, 10, scale);
    }

    public static void visualize(List<Oomage> oomages, int M, double scale) {
        HashTableDrawingUtility.drawLabels(M);
        int[] numInBucket = new int[M];
        for (Oomage s : oomages) {
            int bucketNumber = (s.hashCode() & 0x7FFFFFFF) % M;
            double x = HashTableDrawingUtility.xCoord(numInBucket[bucketNumber]);
            numInBucket[bucketNumber] += 1;
            double y = HashTableDrawingUtility.yCoord(bucketNumber, M);
            s.draw(x, y, scale);
        }
    }
} 
