package median;

import base.vectors.points2d.Vec2df;

import java.util.ArrayList;
import java.util.List;

public class Split {

    private int depth = 0;

    private int m;

    private float median;

    private boolean isHorizontal;

    private float elapsedTimeSort;

    private float elapsedTimeMedian;

    private List<Vec2df> list;

    private Split above = null;

    private Split below = null;

    public static Split makeTree(List<Vec2df> list, boolean isHorizontal, int depth) {
        // Condición de salida
        if (list.size() == 1) {
            return null;
        }

        int left = 0;
        int right = list.size();

        // Parent
        Split p = new Split(isHorizontal, depth);
        p.setList(list);
        p.doSplit();
        int m = p.getM();

        // Above
        int lAbove = left;
        int rAbove;
        if (list.size() % 2 == 0) {
            rAbove = left + m;
        } else {
            rAbove = left + m + 1;
        }
        var above = new ArrayList<>(list.subList(lAbove, rAbove));
        Split a = makeTree(above, !p.isHorizontal(), p.getDepth() + 1);

        if (a != null) {
            p.setAbove(a);
        }

        // Below
        int lBelow = left + m;
        int rBelow = right;
        var below = new ArrayList<>(list.subList(lBelow, rBelow));
        Split b = makeTree(below, !p.isHorizontal(), p.getDepth() + 1);

        if (b != null) {
            p.setBelow(b);
        }

        return p;
    }

    public static void showResults(Split s) {
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

    public Split(boolean isHorizontal, int depth) {
        this.isHorizontal = isHorizontal;
        this.depth = depth;
    }

    public Split(boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
    }

    // Intrinsic Methods

    public void doSplit(List<Vec2df> list) {
        long t1, t2;
        t1 = System.nanoTime();

        if (list.size() > 1) {
            if (isHorizontal) {
                sortListByX(list);
            } else {
                sortListByY(list);
            }
        }

        t2 = System.nanoTime();
        elapsedTimeSort = (t2 - t1) / 1000000000f;

        t1 = System.nanoTime();

        int left = 0;
        int right = list.size();

        this.m = calMedianIndex(left, right);

        if (isHorizontal) {
            this.median = calMedianX(list, this.m);
        } else {
            this.median = calMedianY(list, this.m);
        }

        t2 = System.nanoTime();
        elapsedTimeMedian = (t2 - t1) / 1000000000f;
    }

    public void doSplit() {
        doSplit(list);
    }

    // Convenient intern methods

    public void sortListByX(List<Vec2df> list) {
        list.sort((v1, v2) -> Float.compare(v1.getX(), v2.getX()));
    }

    public void sortListByY(List<Vec2df> list) {
        list.sort((v1, v2) -> Float.compare(v1.getY(), v2.getY()));
    }

    public int calMedianIndex(int left, int right) {
        return (right - left) / 2;
    }

    public float calMedianX(List<Vec2df> list, int m) {
        if (list.size() % 2 == 0) {
            return (list.get(m - 1).getX() + list.get(m).getX()) / 2f;
        } else {
            return list.get(m).getX();
        }
    }

    public float calMedianY(List<Vec2df> list, int m) {
        if (list.size() % 2 == 0) {
            return (list.get(m - 1).getY() + list.get(m).getY()) / 2f;
        } else {
            return list.get(m).getY();
        }
    }

    // Show results

    public String calIdent() {
        return "  ".repeat(Math.max(0, depth));
    }

    public void showResults(List<Vec2df> list, int m, float median) {
        String space = calIdent();

        // Show results
        System.out.printf(space + "Eje: %s\n", isHorizontal ? "x" : "y");
        System.out.printf(space + "Número de elementos: %d\n", list.size());
        StringBuilder out = new StringBuilder(space + "Puntos:\n");
        for (int i = 0; i < list.size(); i++) {
            String vec = String.format("%.3fx %.3fy", list.get(i).getX(), list.get(i).getY());
            out.append(space + "Elemento ").append(i).append(": ").append(vec).append("\n");
        }
        System.out.print(out);
        System.out.printf(space + "Índice mediana: %d\n", m);
        System.out.printf(space + "Mediana: %.3f\n", median);
    }

    public void showResults(List<Vec2df> list) {
        showResults(list, m, median);

        String space = calIdent();

        System.out.printf(space + "Tiempo transcurrido ordenando: %.6f\n", elapsedTimeSort);
        System.out.printf(space + "Tiempo transcurrido mediana: %.6f\n", elapsedTimeMedian);
        System.out.printf(space + "Tiempo transcurrido total: %.6f\n", elapsedTimeSort + elapsedTimeMedian);
    }

    public void showResults() {
        showResults(list);
    }

    // Getters for intrinsic values

    public int getM() {
        return m;
    }

    public float getMedian() {
        return median;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    // Getter & Setter for depth

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    // Getter & Setter above and below

    public Split getAbove() {
        return above;
    }

    public void setAbove(Split above) {
        this.above = above;
    }

    public Split getBelow() {
        return below;
    }

    public void setBelow(Split below) {
        this.below = below;
    }

    // Getters & Setters list

    public List<Vec2df> getList() {
        return list;
    }

    public void setList(List<Vec2df> list) {
        this.list = list;
    }

}
