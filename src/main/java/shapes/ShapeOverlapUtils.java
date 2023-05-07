package shapes;

import base.vectors.points2d.Vec2df;

public class ShapeOverlapUtils {

    public static boolean doIntervalsOverlap(float x1, float x2, float y1, float y2) {
        return Math.max(x1, y1) <= Math.min(x2, y2);
    }

    /**
     * Two segments can be:
     *
     * Parallel
     * Blotters
     * Coincident
     */
    public static boolean segmentVsSegment(Segment2df s1, Segment2df s2) {
        // Check the start and end points such as two rectangles
        boolean spaceOverlaps = (s1.pos.getX() < s2.pos.getX() + s2.end.getX() &&
                        s1.pos.getX() + s1.end.getX() >= s2.pos.getX() &&
                        s1.pos.getY() < s2.pos.getY() + s2.end.getY() &&
                        s1.pos.getY() + s1.end.getY() >= s2.pos.getY());
        // If the space overlaps
        if (spaceOverlaps) {
            if (!s1.isParallel(s2)) { //
                return true;
            } else { // Are parallel, but can be coincident
                Vec2df v = s1.getVector(); // Vector of segment 1 and 2 are equals
                Vec2df a = s1.getPos();
                Vec2df b = s2.getPos();
                return (b.getX() - a.getX()) / v.getX() != (b.getY() - a.getY()) / v.getY();
            }
        } else {
            // Maybe the lines are parallel or cut, but the segments don't overlap
            return false;
        }
        /*boolean overlapX = doIntervalsOverlap(s1.getPos().getX(), s1.getEnd().getX(), s2.getPos().getX(), s2.getEnd().getX());
        boolean overlapY = doIntervalsOverlap(s1.getPos().getY(), s1.getEnd().getY(), s2.getPos().getY(), s2.getEnd().getY());
        return overlapX && overlapY;*/
    }

    // Circle vs Circle

    public static float distCircleToCircle(Circle2df c1, Circle2df c2) {
        return c1.getPos().dist(c2.getPos());
    }

    public static boolean circleVsCircle(Circle2df c1, Circle2df c2) {
        float dist2 = c1.getPos().dist2(c2.getPos());
        float r = c1.radius + c2.radius;
        return dist2 <= r * r;
    }

}
