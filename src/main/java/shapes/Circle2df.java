package shapes;

import base.vectors.points2d.Vec2df;
import panAndZoom.PanAndZoom;

public class Circle2df extends Shape2df {

    protected float radius;

    // Constructors

    public Circle2df() {
        super();
        radius = 0;
    }

    public Circle2df(float x, float y, float radius) {
        super(x, y);
        this.radius = radius;
    }

    public Circle2df(Vec2df pos, float radius) {
        super(pos);
        this.radius = radius;
    }

    public Circle2df(int id, float x, float y, float radius) {
        super(id, x, y);
        this.radius = radius;
    }

    public Circle2df(int id, Vec2df pos, float radius) {
        super(id, pos);
        this.radius = radius;
    }

    public Circle2df(Shape2df shape) {
        super(shape);
        if (shape instanceof Circle2df) {
            Circle2df c = (Circle2df) shape;
            this.radius = c.getRadius();
        }
    }

    // Methods

    public Vec2df getSize() {
        return new Vec2df(2 * radius);
    }

    public float dist(Circle2df c) {
        return ShapeOverlapUtils.distCircleToCircle(this, c);
    }

    // Getters & Setters

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    @Override
    public void drawYourself(PanAndZoom g) {
        float x = pos.getX() - radius;
        float y = pos.getY() - radius;
        g.fillOval(new Vec2df(x, y), getSize());
    }

    @Override
    public boolean overlaps(Shape2df shape) {
        if (shape instanceof Circle2df) {
            Circle2df c = (Circle2df) shape;
            return ShapeOverlapUtils.circleVsCircle(this, c);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Circle2df{" +
                "id=" + id +
                ", pos=" + pos +
                ", radius=" + radius +
                '}';
    }

}
