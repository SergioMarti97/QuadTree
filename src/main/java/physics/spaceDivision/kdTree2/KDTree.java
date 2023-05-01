package physics.spaceDivision.kdTree2;

import base.vectors.points2d.Vec2df;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class KDTree<T> {

    private DimensionalSplit2D<T> root;

    public KDTree(Rect area, List<Pair<Rect, T>> pairs) {
        if (pairs.size() == 0) {
            root = new DimensionalSplit2D<>(area);
        } else {
            root = KDFactory.generate(area, pairs, 0, pairs.size(), 0, false);
        }
    }

    public List<DimensionalSplit2D<T>> getLeaves() {
        List<DimensionalSplit2D<T>> leaves = new ArrayList<>();
        root.addLeaves(leaves);
        return leaves;
    }

    public List<DimensionalSplit2D<T>> getLeavesWithMoreOneItem() {
        var leaves = getLeaves();
        return leaves.stream().filter(ds -> ds.getItems().size() > 1).collect(Collectors.toList());
    }

    public void draw(PanAndZoom pz, Rect screen) {
        root.draw(pz, screen);
    }

    public void drawTree(GraphicsContext gc, Vec2df off) {
        root.drawTree(gc, off);
    }

}
