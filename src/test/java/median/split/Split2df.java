package median.split;

import base.vectors.points2d.Vec2df;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

import java.util.Comparator;
import java.util.List;

/**
 * This class represents a split on a determinate dimension
 *
 * It needs the comparator to sort elements on its dimension and calculate the median
 *
 * The median value is cal
 *
 * @param <T>
 */
public class Split2df<T> {

    // todo: not finished yet! copy same logic Split class & apply to Rects as is done to Vec2df

    /**
     * The dimensional comparator to sort the elements
     *
     * 0 = X; 1 = Y
     */
    private final Comparator<Pair<Vec2df, T>>[] DIMENSIONAL_COMPARATOR = new Comparator[] {
            (Comparator<Pair<Vec2df, T>>) (o1, o2) -> Float.compare(o1.getKey().getX(), o2.getKey().getX()),
            (Comparator<Pair<Vec2df, T>>) (o1, o2) -> Float.compare(o1.getKey().getY(), o2.getKey().getY())
    };

    // Properties of the Split

    /**
     * The depth od this split on the tree
     */
    private int depth = 0;

    /**
     * The dimension which is going to split the space
     *
     * ¿Which dimension? Maybe axis X, Y, Z or W, the "Dimensional Split" doesn't matter,
     * only needs two things: the id of the dimension and the comparator to sort the objets on this dimension
     *
     * Note: dimension and axis are synonyms on this context
     */
    private int dimension; // old: isHorizontal

    // Properties to find the median

    /**
     * The median index
     */
    private int m;

    /**
     * The median value
     */
    private float median;

    // Children

    private Split2df<T> above = null;

    private Split2df<T> below = null;

    // List of objects

    private List<Pair<Vec2df, T>> list;

    // Area

    private Rect area;

    // Constructor

    public Split2df(int dimension, int depth) {
        this.dimension = dimension;
        this.depth = depth;
    }

    public Split2df(int dimension) {
        this.dimension = dimension;
    }

    // Abstract methods

    /**
     * This method is needed to calculate the median value (not the index, the value) to split the space/axis
     *
     * Other way to implement this would be as an another property which in fact is a lambda method returns a float value
     *
     * @param list the list of undefined type "T" objets
     * @param index the index which is going access
     * @return the value of that element
     */
    private float getValue(List<Pair<Vec2df, T>> list, int index) {
        var p = list.get(index);
        return (dimension == 0 ? p.getKey().getX() : p.getKey().getY());
    }

    // Intrinsic methods

    public void doSplit() {
        if (this.list.size() > 1) {
            this.list.sort(this.DIMENSIONAL_COMPARATOR[this.dimension]);
        }

        int left = 0;
        int right = this.list.size();

        this.m = calMedianIndex(left, right);

        this.median = calMedian(this.list, this.m);
    }

    // Convenient intern methods

    public int calMedianIndex(int left, int right) {
        return (right - left) / 2;
    }

    public float calMedian(List<Pair<Vec2df, T>> list, int m) {
        if (list.size() % 2 == 0) {
            return (getValue(list, m - 1) + getValue(list, m)) / 2f;
        } else {
            return getValue(list, m);
        }
    }

    // Getters for intrinsic values: median index (m) and median

    public int getM() {
        return m;
    }

    public float getMedian() {
        return median;
    }

    // Getter & Setter for depth

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    // Getter for comparator

    public Comparator<Pair<Vec2df, T>> getDimensionalComparator() {
        return DIMENSIONAL_COMPARATOR[this.dimension];
    }

    // Getter & Setter above and below

    public Split2df<T> getAbove() {
        return above;
    }

    public void setAbove(Split2df<T> above) {
        this.above = above;
    }

    public Split2df<T> getBelow() {
        return below;
    }

    public void setBelow(Split2df<T> below) {
        this.below = below;
    }

    // Getters & Setters for list

    public List<Pair<Vec2df, T>> getList() {
        return list;
    }

    public void setList(List<Pair<Vec2df, T>> list) {
        this.list = list;
    }

    // Getter & Setter for area

    public Rect getArea() {
        return area;
    }

    public void setArea(Rect area) {
        this.area = area;
    }

    public Rect calAboveArea() {
        Rect a = new Rect(area);
        if (this.dimension == 0) { // X axis
            a.setRight(this.median);
        } else { // Y axis
            a.setTop(this.median);
        }
        return a;
    }

    public Rect calBelowArea() {
        Rect b = new Rect(area);
        if (this.dimension == 0) {
            b.setLeft(this.median);
        } else {
            b.setBottom(this.median);
        }
        return b;
    }

    // Show results

    public String calIdent() {
        return "  ".repeat(Math.max(0, depth));
    }

    private void showResults(List<Pair<Vec2df, T>> list) {
        String space = calIdent();

        // Show results
        System.out.printf(space + "Axis: %s\n", this.dimension == 0 ? "x" : "y");
        System.out.printf(space + "Number of elements: %d\n", list.size());
        StringBuilder out = new StringBuilder(space + "Points:\n");
        for (int i = 0; i < list.size(); i++) {
            var p = list.get(i).getKey();
            var o = list.get(i).getValue();
            String vec = String.format("%.3fx %.3fy", p.getX(), p.getY());
            out.append(space).append("Element ").append(i).append(": ").append(vec).append(" {").append(o.toString()).append("}\n");
        }
        System.out.print(out);
        System.out.printf(space + "Median index: %d\n", this.m);
        System.out.printf(space + "Median: %.3f\n", this.median);
    }

    public void showResults() {
        showResults(this.list);
    }

    // Draw yourself method

    public void draw(PanAndZoom pz, Rect screen) {
        if (screen.contains(area)) {
            pz.strokeRect(area.getPos(), area.getSize());
        }

        if (above != null) {
            above.draw(pz, screen);
        }

        if (below != null) {
            below.draw(pz, screen);
        }
    }


}
