package physics.spaceDivision;

import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;
import panAndZoom.PanAndZoom;

public class Rect {

    private Vec2df pos = new Vec2df();

    private Vec2df size = new Vec2df();

    private Color color = Color.WHITE;

    public Rect() {
        size.set(1, 1);
    }

    public Rect(Vec2df pos, Vec2df size) {
        this.pos = new Vec2df(pos);
        this.size = new Vec2df(size);
    }

    public Rect(float x, float y, float w, float h) {
        pos.set(x, y);
        size.set(w, h);
    }

    public Rect(Rect rect) {
        pos = new Vec2df(rect.getPos());
        size = new Vec2df(rect.getSize());
    }

    // Convenient functions

    public boolean contains(Vec2df p) {
        return !(p.getX() < pos.getX() || p.getY() < pos.getY() || p.getX() >= (pos.getX() + size.getX()) || p.getY() >= (pos.getY() + size.getY()));
    }

    public boolean contains(Rect r) {
        return (r.pos.getX() >= pos.getX()) && (r.pos.getX() + r.size.getX() < pos.getX() + size.getX()) &&
                (r.pos.getY() >= pos.getY()) && (r.pos.getY() + r.size.getY() < pos.getY() + size.getY());
    }

    public boolean overlaps(Rect r) {
        return (pos.getX() < r.pos.getX() + r.size.getX() && pos.getX() + size.getX() >= r.pos.getX() && pos.getY() < r.pos.getY() + r.size.getY() && pos.getY() + size.getY() >= r.pos.getY());
    }

    // Space Division convenient methods

    public float getLeft() {
        return pos.getX();
    }

    public float getTop() {
        return pos.getY();
    }

    public float getRight() {
        return pos.getX() + size.getX();
    }

    public float getBottom() {
        return pos.getY() + size.getY();
    }

    public void setLeft(float left) {
        float diff = left - getLeft();
        size.addToX(-diff);
        pos.setX(left);
    }

    public void setTop(float top) {
        float diff = top - getTop();
        size.addToY(-diff);
        pos.setY(top);
    }

    public void setRight(float right) {
        if (right > getLeft()) {
            float width = right - getLeft();
            size.setX(width);
        } else {
            size.setX(0);
        }
    }

    public void setBottom(float bottom) {
        if (bottom > getTop()) {
            float height = bottom - getTop();
            size.setY(height);
        } else {
            size.setY(0);
        }
    }

    // Getters & Setters

    public void set(Vec2df pos, Vec2df size) {
        this.pos = pos;
        this.size = size;
    }

    public Vec2df getPos() {
        return pos;
    }

    public void setPos(Vec2df pos) {
        this.pos = pos;
    }

    public Vec2df getSize() {
        return size;
    }

    public void setSize(Vec2df size) {
        this.size = size;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Draw Yourself method

    public void draw(PanAndZoom pz) {
        pz.strokeRect(pos, size);
    }

    @Override
    public String toString() {
        return "Rect { ori " + pos.toString() + " size " + size.toString() + "}";
    }
}
