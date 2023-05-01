package physics.spaceDivision.kdTree2;

import base.vectors.points2d.Vec2df;
import javafx.util.Pair;
import physics.spaceDivision.Rect;

import java.util.List;

public class KDFactory {

    public static <T> void calDimensionalNodeRect(DimensionalSplit2D<T> ds, float splitParent, boolean parentIsHorizontal, boolean isAbove) {
        float point = splitParent;
        if (parentIsHorizontal) {
            if (isAbove) {
                // H & a
                ds.getRect().setLeft(point);
            } else {
                ds.getRect().setRight(point);
            }
        } else {
            if (isAbove) {
                // V & a
                ds.getRect().setBottom(point);
            } else {
                ds.getRect().setTop(point);
            }
        }
    }

    public static <T> DimensionalSplit2D<T> generate(
            Rect area,
            List<Pair<Rect, T>> pairs,
            int left,
            int right,
            int depth,
            boolean isHorizontal) {

        // Two easy cases

        if (right < left) {
            return null;
        }

        if (right == left) {
            Pair<Rect, T> value = pairs.get(left);
            DimensionalSplit2D<T> ds = new DimensionalSplit2D<>(depth, isHorizontal, area);
            ds.getItems().add(value);
            return ds;
        }

        // Sort the pairs by X axis (horizontal) or Y axis (vertical)
        var list = pairs.subList(left, right);
        if (isHorizontal) {
            list.sort((p1, p2) -> Float.compare(p1.getKey().getPos().getX(), p2.getKey().getPos().getX()));
        } else {
            list.sort((p1, p2) -> Float.compare(p1.getKey().getPos().getY(), p2.getKey().getPos().getY()));
        }

        // Calcule the median split
        int m = (right - left) / 2;
        float median;
        if (list.size() % 2 == 0) {
            Rect r1 = list.get(m - 1).getKey();
            Rect r2 = list.get(m).getKey();
            float p1, p2;
            if (isHorizontal) {
                p1 = r1.getPos().getX() + r1.getSize().getX() / 2;
                p2 = r2.getPos().getX() + r2.getSize().getX() / 2;
            } else {
                p1 = r1.getPos().getY() + r1.getSize().getY() / 2;
                p2 = r2.getPos().getY() + r2.getSize().getY() / 2;
            }
            median = (p1 + p2) / 2f;
        } else {
            Rect r = list.get(m).getKey();
            if (isHorizontal) {
                median = r.getPos().getX() + r.getSize().getX() / 2;
            } else {
                median = r.getPos().getY() + r.getSize().getY() / 2;
            }
        }

        // Create the dimensional split
        DimensionalSplit2D<T> ds = new DimensionalSplit2D<>(depth, median, isHorizontal, area);

        // Add all the elements
        ds.getItems().addAll(list);

        // Increase the depth & update the split direction (dimensions > 2 = update split dimension)
        depth++;
        isHorizontal = !isHorizontal;

        // Create Children
        DimensionalSplit2D<T> below = generate(ds.getRect(), pairs, left, left + m, depth, isHorizontal); // left + m - 1
        DimensionalSplit2D<T> above = generate(ds.getRect(), pairs, left + m, right, depth, isHorizontal); // left + m + 1

        // Calcule rectangles & set children
        if (below != null) {
            calDimensionalNodeRect(below, ds.getSplit(), ds.isHorizontal(), false);
            ds.setBelow(below);
        }

        if (above != null) {
            calDimensionalNodeRect(above, ds.getSplit(), ds.isHorizontal(), true);
            ds.setAbove(above);
        }

        return ds;
    }

}
