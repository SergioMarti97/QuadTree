package median;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import base.vectors.points2d.Vec2df;

public class MedianTest2 {

    public static List<Vec2df> rndList(int num) {
        List<Vec2df> list = new ArrayList<>();

        Random rnd = new Random();
        rnd.setSeed(123);
        for (int i = 0; i < num; i++) {
            Vec2df vec2df = new Vec2df(rnd.nextFloat(), rnd.nextFloat());
            list.add(vec2df);
        }

        return list;
    }

    public static void main(String[] args) {

        final int NUM = 2;

        var list = rndList(NUM);

        // Parent
        System.out.println("PARENT");

        int left = 0;
        int right = list.size();
        System.out.printf("Left: %d Right: %d\n", left, right);

        Split p = new Split(true);
        p.doSplit(list);
        int m = p.getM();

        System.out.println();

        // Above
        System.out.println("ABOVE");

        int lAbove = left;
        int rAbove;
        if (list.size() % 2 == 0) {
            rAbove = left + m;
        } else {
            rAbove = left + m + 1;
        }
        System.out.printf("Left: %d Right: %d\n", lAbove, rAbove);

        var above = new ArrayList<>(list.subList(lAbove, rAbove));

        Split a = new Split(false);
        a.doSplit(above);
        int mAbove = a.getM();

        System.out.println();

        // Below
        System.out.println("BELOW");
        int lBelow = left + m;
        int rBelow = right;
        System.out.printf("Left: %d Right: %d\n", lBelow, rBelow);

        var below = new ArrayList<>(list.subList(lBelow, rBelow));

        Split b = new Split(false);
        b.doSplit(below);

        int mBelow = b.getM();
        System.out.println();


    }

}
