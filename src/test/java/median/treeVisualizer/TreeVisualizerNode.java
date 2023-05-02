package median.treeVisualizer;

import base.vectors.points2d.Vec2df;
import panAndZoom.PanAndZoom;

import java.util.ArrayList;
import java.util.List;

public class TreeVisualizerNode<T> {

    public interface Drawable<T> {
        void drawYourSelf(TreeVisualizerNode<T> node, PanAndZoom pz);
    }

    private Vec2df pos;

    private T item;

    private Drawable<T> drawMethod;

    private final List<TreeVisualizerNode<T>> children = new ArrayList<>();

    public TreeVisualizerNode(Vec2df pos, T item) {
        this.pos = new Vec2df(pos);
        this.item = item;
    }

    public void draw(PanAndZoom pz) {
        this.drawMethod.drawYourSelf(this, pz);
    }

    // Methods

    public int numChildren() {
        return children.size();
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public TreeVisualizerNode<T> getNode(Vec2df p, float minDist) {
        if (pos.dist(p) < minDist) {
            return this;
        } else {
            for (var child : children) {
                var selected = child.getNode(p, minDist);
                if (selected != null) {
                    return selected;
                }
            }
        }
        return null;
    }

    // Getters & Setters

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public Vec2df getPos() {
        return pos;
    }

    public void setPos(Vec2df pos) {
        this.pos.set(pos);
    }

    public Drawable<T> getDrawMethod() {
        return drawMethod;
    }

    public void setDrawMethod(Drawable<T> drawMethod) {
        this.drawMethod = drawMethod;
    }

    public List<TreeVisualizerNode<T>> getChildren() {
        return children;
    }

}
