package shapes;

import base.graphics.Drawable;
import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;
import panAndZoom.PanAndZoom;

import java.util.Objects;

public abstract class Shape2df implements Drawable<PanAndZoom> {

    protected int id = 0;

    protected Vec2df pos;

    protected Color color;

    // Constructor

    public Shape2df() {
        pos = new Vec2df();
    }

    public Shape2df(float x, float y) {
        setPos(x, y);
    }

    public Shape2df( Vec2df pos) {
        setPos(pos);
    }

    public Shape2df(int id, float x, float y) {
        this.id = id;
        setPos(x, y);
    }

    public Shape2df(int id, Vec2df pos) {
        this.id = id;
        setPos(pos);
    }

    public Shape2df(Shape2df shape) {
        this.id = shape.getId();
        setPos(shape.getPos());
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vec2df getPos() {
        return pos;
    }

    public void setPos(float x, float y) {
        if (this.pos == null) {
            this.pos = new Vec2df();
        }
        this.pos.set(x, y);
    }

    public void setPos(Vec2df pos) {
        if (this.pos == null) {
            this.pos = new Vec2df();
        }
        this.pos.set(pos);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Methods

    public float dist(Shape2df shape) {
        return this.getPos().dist(shape.getPos());
    }

    public abstract boolean overlaps(Shape2df shape);

    // To String

    @Override
    public String toString() {
        return "id: " + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Shape2df)) return false;
        Shape2df shape2df = (Shape2df) o;
        return getId() == shape2df.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
