package deprecated.kdTree;

import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class KDTree<T> {

    private DimensionalNode<T> root = null;

    // Constructor

    public KDTree() {

    }

    // Methods

    public void insert(T value, Rect area) {

    }

    public void remove(T value, Rect area) {

    }

    public Pair<Rect, T> nearest(Pair<Rect, T> point) {
        if (root == null) {
            return null;
        }

        return null;
    }

    public List<T> search(Rect area) {
        return new ArrayList<>();
    }

    // Getters & Setters

    public DimensionalNode<T> getRoot() {
        return root;
    }

    public void setRoot(DimensionalNode<T> root) {
        this.root = root;
    }

    // Draw Yourself

    public void draw(PanAndZoom pz, Rect screen) {
        root.draw(pz, screen);
    }

}
