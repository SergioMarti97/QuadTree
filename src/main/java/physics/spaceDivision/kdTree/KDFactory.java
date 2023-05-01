package physics.spaceDivision.kdTree;

import base.vectors.points2d.Vec2df;
import javafx.util.Pair;
import physics.spaceDivision.Rect;

import java.util.List;

public class KDFactory {

    public static <T> void calDimensionalNodeRect(DimensionalNode<T> dm, boolean isHorizontal, boolean isAbove) {
        Vec2df point = dm.getPoint();
        if (isHorizontal) {
            if (isAbove) {
                // H & a
                dm.getRect().setLeft(point.getX());
            } else {
                dm.getRect().setRight(point.getX());
            }
        } else {
            if (isAbove) {
                // V & a
                dm.getRect().setTop(point.getY());
            } else {
                dm.getRect().setBottom(point.getY());
            }
        }
    }

    public static <T> DimensionalNode<T> generate(int depth, boolean isHorizontal, List<Pair<Rect, T>> points, int left, int right, Rect area) {
        if (right < left) {
            return null;
        }
        if (right == left) {
            Pair<Rect, T> value = points.get(left);
            DimensionalNode<T> dm = new DimensionalNode<T>(depth, isHorizontal, value.getKey().getPos());
            dm.setValue(value);
            dm.setRect(area);
            return dm;
        }

        int m = 1 + (right - left) / 2;
        // Selecion by QuickSort
        if (isHorizontal) {
            points.subList(left, right).sort((p1, p2) -> Float.compare(p1.getKey().getPos().getX(), p2.getKey().getPos().getX()));
        } else {
            points.subList(left, right).sort((p1, p2) -> Float.compare(p1.getKey().getPos().getY(), p2.getKey().getPos().getY()));
        }

        // Create the dimensional node
        Pair<Rect, T> value = points.get(left + m - 1);
        DimensionalNode<T> dm = new DimensionalNode<>(depth, isHorizontal, value.getKey().getPos());
        dm.setValue(value);
        dm.setRect(area);

        // Update the split dimensions
        isHorizontal = !isHorizontal;

        // Increase depth
        depth++;

        dm.setBelow(generate(depth, isHorizontal, points, left, left + m - 2, dm.getRect()));
        dm.setAbove(generate(depth, isHorizontal, points, left + m, right, dm.getRect()));

        if (dm.getBelow() != null) {
            calDimensionalNodeRect(dm.getBelow(), isHorizontal, false);
        }
        if (dm.getAbove() != null) {
            calDimensionalNodeRect(dm.getAbove(), isHorizontal, true);
        }
        return dm;
    }

    public static <T> KDTree<T> generate(Rect area, List<Pair<Rect, T>> points) {
        if (points.size() == 0) {
            return null;
        }

        KDTree<T> tree = new KDTree<>();

        tree.setRoot(generate(1, true, points, 0, points.size() - 1, area));
        return tree;
    }

}
