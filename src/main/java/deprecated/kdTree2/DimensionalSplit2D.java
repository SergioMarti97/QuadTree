package deprecated.kdTree2;

import base.vectors.points2d.Vec2df;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Deprecated
public class DimensionalSplit2D<T> {

    private boolean isHorizontal = false;

    private int depth = 0;

    private float split = 0;

    private Rect rect = new Rect();

    private DimensionalSplit2D<T> above = null;

    private DimensionalSplit2D<T> below = null;

    private List<Pair<Rect, T>> items = new ArrayList<>();

    // Constructor

    public DimensionalSplit2D(int depth, float split, boolean isHorizontal, Rect rect) {
        this.depth = depth;
        this.split = split;
        this.isHorizontal = isHorizontal;
        this.rect = new Rect(rect);
        Random rnd = new Random();
        this.rect.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
    }

    public DimensionalSplit2D(float split, boolean isHorizontal, Rect rect) {
        this.split = split;
        this.isHorizontal = isHorizontal;
        this.rect = new Rect(rect);
        Random rnd = new Random();
        this.rect.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
    }

    public DimensionalSplit2D(boolean isHorizontal, Rect rect) {
        this.isHorizontal = isHorizontal;
        this.rect = new Rect(rect);
        Random rnd = new Random();
        this.rect.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
    }

    public DimensionalSplit2D(Rect rect) {
        this.rect = new Rect(rect);
        Random rnd = new Random();
        this.rect.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));
    }

    // Methods

    public boolean isLeaf() {
        return above == null && below == null;
    }

    public void addLeaves(List<DimensionalSplit2D<T>> leaves) {
        if (this.isLeaf()) {
            leaves.add(this);
        } else {
            if (below != null) {
                if (below.isLeaf()) {
                    leaves.add(below);
                } else {
                    below.addLeaves(leaves);
                }
            }

            if (above != null) {
                if (above.isLeaf()) {
                    leaves.add(above);
                } else {
                    above.addLeaves(leaves);
                }
            }
        }
    }

    // Getters & Setters

    public float getSplit() {
        return split;
    }

    public List<Pair<Rect, T>> getItems() {
        return items;
    }

    public Rect getRect() {
        return rect;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    // Getters & Setters for Children

    public DimensionalSplit2D<T> getAbove() {
        return above;
    }

    public void setAbove(DimensionalSplit2D<T> above) {
        this.above = above;
    }

    public DimensionalSplit2D<T> getBelow() {
        return below;
    }

    public void setBelow(DimensionalSplit2D<T> below) {
        this.below = below;
    }


    // Draw Yourself

    public void draw(PanAndZoom pz, Rect screen) {
        if (screen.contains(rect)) {
            pz.getGc().setStroke(rect.getColor());
            rect.draw(pz);
        }

        Vec2df s = new Vec2df();
        Vec2df e = new Vec2df();
        if (isHorizontal) {
            s.set(split, rect.getTop());
            e.set(split, rect.getBottom());
        } else {
            s.set(rect.getLeft(), split);
            e.set(rect.getRight(), split);
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

    public void drawTree(GraphicsContext gc, Vec2df off) {
        final int SMALL_LEADING = 2;
        final int LEADING = 50;

        gc.fillText(this.toString(), off.getX(), off.getY());

        Vec2df s = new Vec2df(off);
        s.addToY(SMALL_LEADING);

        if (below != null) {
            Vec2df e = new Vec2df(off);
            e.addToY(LEADING);
            e.addToX(-LEADING);

            gc.strokeLine(s.getX(), s.getY(), e.getX(), e.getY());

            below.drawTree(gc, e);
        }

        if (above != null) {
            Vec2df e = new Vec2df(off);
            e.addToY(LEADING * 1.5f);
            e.addToX(LEADING);

            gc.strokeLine(s.getX(), s.getY(), e.getX(), e.getY());

            above.drawTree(gc, e);
        }
    }

    @Override
    public String toString() {
        return String.format("DimensionalSplit2D{depth: %d split: %.3f axis: %s items: %d rect: %s}", depth, split, (isHorizontal? "h" : "v"), items.size(), rect.toString());
    }
}
