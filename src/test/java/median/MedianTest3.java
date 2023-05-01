package median;

import base.vectors.points2d.Vec2df;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MedianTest3 {

    public static final int NUM = 2000;

    public static Random rnd;

    public static List<Vec2df> list;

    public static Split s;

    public static List<Vec2df> rndList(int num) {
        List<Vec2df> list = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            Vec2df vec2df = new Vec2df(rnd.nextFloat(), rnd.nextFloat());
            list.add(vec2df);
        }

        return list;
    }

    public static float repeatSimulation(int repeats) {
        long t1, t2;
        float elapsedTime = 0;
        for (int i = 0; i < repeats; i++) {
            System.out.println("Simulation number: " + i);
            t1 = System.nanoTime();
            s = Split.makeTree(list, true, 0);
            t2 = System.nanoTime();
            elapsedTime += (t2 - t1) / 1000000000f;
        }
        return elapsedTime / (float)repeats;
    }

    public static void main(String[] args) {
        rnd = new Random();
        rnd.setSeed(123);

        System.out.println("Points number: " + NUM);

        list = rndList(NUM);

        /*long t1, t2;
        t1 = System.nanoTime();
        Split s = Split.makeTree(list, true, 0);
        t2 = System.nanoTime();
        float elapsedTime = (t2 - t1) / 1000000000f;
        System.out.printf("Time required to build the kd-tree: %.6f\n", elapsedTime);

        Split.showResults(s);*/

        float elapsedTime = repeatSimulation(100);
        System.out.printf("Average elapsed time to build kd-tree: %.6f\n", elapsedTime);

        // Split.showResults(s);
    }

}
