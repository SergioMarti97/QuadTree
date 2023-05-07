package shapes;

import base.vectors.points2d.Vec2df;
import panAndZoom.PanAndZoom;

public class Segment2df extends Shape2df {

    protected Vec2df end;

    // Constructors

    public Segment2df() {
        super();
        end = new Vec2df();
    }

    public Segment2df(float sX, float sY, float eX, float eY) {
        super(sX, sY);
        setEnd(eX, eY);
    }

    public Segment2df(Vec2df pos, Vec2df end) {
        super(pos);
        setEnd(end);
    }

    public Segment2df(int id, float sX, float sY, float eX, float eY) {
        super(id, sX, sY);
        setEnd(eX, eY);
    }

    public Segment2df(int id, Vec2df pos, Vec2df end) {
        super(id, pos);
        setEnd(end);
    }

    public Segment2df(Segment2df segment2df) {
        super(segment2df);
        setEnd(segment2df.getEnd());
    }

    // Methods

    public Vec2df getVector() {
        Vec2df v = new Vec2df(end);
        v.sub(pos);
        v.normalize();
        return v;
    }

    public float getSlope() {
        float deltaX = end.getX() - pos.getX();
        float deltaY = end.getY() - pos.getY();
        return deltaY / deltaX;
    }

    public float getIntersectYAxis() {
        float slope = getSlope();
        return slope * pos.getX() - pos.getY();
    }

    public boolean isParallel(Segment2df s) {
        return this.getSlope() == s.getSlope();
    }

    @Override
    public boolean overlaps(Shape2df shape2df) {
        if (shape2df instanceof Segment2df) {
            Segment2df s = (Segment2df) shape2df;
            return ShapeOverlapUtils.segmentVsSegment(this, s);
        }
        return false;
    }

    public Vec2df getIntersectionPoint(Segment2df s) {
        if (!overlaps(s)) {
            return null;
        }

        float m1 = getSlope();
        float n1 = getIntersectYAxis();
        float m2 = s.getSlope();
        float n2 = s.getIntersectYAxis();

        float x = (n2 - n1) / (m1 - m2);
        float y = m1 * x + n1;

        return new Vec2df(-x, -y);
    }

    // Getters & Setters

    public Vec2df getEnd() {
        return end;
    }

    public void setEnd(float x, float y) {
        if (this.end == null) {
            this.end = new Vec2df();
        }
        this.end.set(x, y);
    }

    public void setEnd(Vec2df end) {
        if (this.end == null) {
            this.end = new Vec2df();
        }
        this.end.set(end);
    }

    @Override
    public void drawYourself(PanAndZoom g) {
        g.strokeLine(pos, end);
    }

}
