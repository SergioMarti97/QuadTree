package physics.spaceDivision.kdTree;

import base.vectors.points2d.Vec2df;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.List;

public class KdTree2df<T> {

    private static final int NUM_DIMENSIONS = 2;

    public static <O> Split2df<O> generateSplit(Rect area, List<Pair<Vec2df, O>> list, int d, int depth) {
        // Condición de salida
        if (list.size() <= 1) {
            // return null;
            Split2df<O> p = new Split2df<>(d, depth);
            p.setList(list);
            // p.doSplit();
            p.setArea(area);
            return p;
        } else {
            // Condición de insertar pares en colisión
        /*if (list.size() == 2) {
            collidingPairs.add(new Pair<>(list.get(0).getValue(), list.get(1).getValue()));
        }*/

            int left = 0;
            int right = list.size();

            // Parent
            Split2df<O> p = new Split2df<>(d, depth);
            p.setList(list);
            p.doSplit();
            p.setArea(area);
            int m = p.getM();

            // Change dimension
            d++;
            if (d >= NUM_DIMENSIONS) {
                d = 0;
            }

            // Above
            int lAbove = left;
            int rAbove;
            if (list.size() % 2 == 0) {
                rAbove = left + m;
            } else {
                rAbove = left + m + 1;
            }
            var above = new ArrayList<>(list.subList(lAbove, rAbove));
            Rect areaAbove = p.calAboveArea();
            Split2df<O> a = generateSplit(areaAbove, above, d, p.getDepth() + 1);

            p.setAbove(a);

            // Below
            int lBelow = left + m;
            int rBelow = right;
            var below = new ArrayList<>(list.subList(lBelow, rBelow));
            Rect areaBelow = p.calBelowArea();
            Split2df<O> b = generateSplit(areaBelow, below, d, p.getDepth() + 1);

            p.setBelow(b);

            return p;
        }
    }

    private Split2df<T> root;

    public KdTree2df(Rect arena, List<Pair<Vec2df, T>> list) {
        root = generateSplit(arena, list, 0, 0);
    }

    public void recalculate(Rect arena, List<Pair<Vec2df, T>> list) {
        root = generateSplit(arena, list, 0, 0);
    }

    @Deprecated
    public List<Pair<T, T>> getLeaves() {
        List<Pair<T, T>> leaves = new ArrayList<>();
        root.addLeaves(leaves);
        return leaves;
    }

    public List<T> search(Vec2df point) {
        List<T> list = new ArrayList<>();
        return root.search(point, list);
        // see: https://towardsdatascience.com/space-partitioning-and-kd-trees-7b0e12b368d0
    }

    public Split2df<T> getRoot() {
        return root;
    }

    private void showResults(Split2df<T> s) {
        if (s == null) {
            return;
        }

        s.showResults();
        System.out.println();

        if (s.getAbove() != null) {
            System.out.println(s.getAbove().calIdent() + "ABOVE");
        }
        showResults(s.getAbove());
        if (s.getAbove() != null) {
            System.out.println();
        }

        if (s.getBelow() != null) {
            System.out.println(s.getBelow().calIdent() + "BELOW");
        }
        showResults(s.getBelow());
        if (s.getBelow() != null) {
            System.out.println();
        }
    }

    public void showResults() {
        showResults(root);
    }

    public void draw(PanAndZoom pz, Rect screen) {
        root.draw(pz, screen);
    }

}
