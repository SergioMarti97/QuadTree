package physics.spaceDivision.kdTree;

import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

public class DimensionalNode<T> {

    private int depth = 0;

    private Vec2df point = new Vec2df();

    private boolean isHorizontal = true;

    private Rect rect = new Rect();

    private DimensionalNode above = null;

    private DimensionalNode below = null;

    private Pair<Rect, T> value = null;

    // Constructor

    public DimensionalNode(int depth, boolean isHorizontal) {
        this.depth = depth;
        this.isHorizontal = isHorizontal;
    }

    public DimensionalNode(int depth, boolean isHorizontal, Vec2df point) {
        this.depth = depth;
        this.isHorizontal = isHorizontal;
        this.point.set(point);
    }

    // Kd-Tree methods

    public boolean isBelow(Vec2df point) {
        return false;
    }

    public boolean isLeaf() {
        return false;
    }

    // Space division Methods

    public void clear() {
        value = null;

        if (above != null) {
            above.clear();
            above = null;
        }

        if (below != null) {
            below.clear();
            below = null;
        }
    }

    // Getters & Setters

    public DimensionalNode getBelow() {
        return below;
    }

    public void setBelow(DimensionalNode below) {
        this.below = below;
    }

    public DimensionalNode getAbove() {
        return above;
    }

    public void setAbove(DimensionalNode above) {
        this.above = above;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = new Rect(rect);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public Vec2df getPoint() {
        return point;
    }

    public void setPoint(Vec2df point) {
        this.point = point;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public Pair<Rect, T> getValue() {
        return value;
    }

    public void setValue(Pair<Rect, T> value) {
        this.value = value;
    }

    // DrawYourSelf
    public void draw(PanAndZoom pz, Rect screen) {
        if (screen.contains(rect)) {
            rect.draw(pz);
            pz.fillText(this.toString(), rect.getPos().getX() + 2, rect.getPos().getY() + 2);
        }

        Vec2df s = new Vec2df();
        Vec2df e = new Vec2df();
        if (isHorizontal) {
            s.set(point.getX(), rect.getTop());
            e.set(point.getX(), rect.getBottom());
        } else {
            s.set(rect.getLeft(), point.getY());
            e.set(rect.getRight(), point.getY());
        }
        Paint p = pz.getGc().getStroke();
        pz.getGc().setStroke(Color.RED);
        pz.strokeLine(s, e);
        pz.getGc().setStroke(p);

        if (above != null) {
            above.draw(pz, screen);
        }

        if (below != null) {
            below.draw(pz, screen);
        }
    }

    @Override
    public String toString() {
        return "DimensionalNode {depth: " + depth + " partition p: " + point.toString() + " division: " + (isHorizontal ? "h" : "v") + " region: " + rect.toString() + "}";
    }
}
