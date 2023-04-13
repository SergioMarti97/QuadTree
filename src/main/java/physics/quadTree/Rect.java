package physics.quadTree;

import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;

public class Rect {

    private Vec2df pos = new Vec2df();

    private Vec2df size = new Vec2df();

    private Color color = Color.WHITE;

    public Rect() {
        size.set(1, 1);
    }

    public Rect(Vec2df pos, Vec2df size) {
        this.pos = pos;
        this.size = size;
    }

    public Rect(float x, float y, float w, float h) {
        pos.set(x, y);
        size.set(w, h);
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

    @Override
    public String toString() {
        return "Rect { ori " + pos.toString() + " size " + size.toString() + "}";
    }
}
