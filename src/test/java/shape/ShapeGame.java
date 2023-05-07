package shape;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomUtils;
import physics.spaceDivision.Rect;
import shapes.Segment2df;
import shapes.Shape2df;

import java.util.ArrayList;
import java.util.List;

public class ShapeGame extends AbstractGame {

    private final Vec2df CIRCLE_SIZE = new Vec2df(5);

    private final Vec2df HALF_CIRCLE_SIZE = new Vec2df();

    private List<Shape2df> shapes;

    private PanAndZoom pz;

    private Rect screen;

    private Vec2df mouse;

    private Vec2df s = null;

    private boolean isDrawBoundingBox = false;

    @Override
    public void initialize(GameApplication gc) {
        pz = new PanAndZoom(gc.getGraphicsContext());
        shapes = new ArrayList<>();
        mouse = new Vec2df();
        screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        HALF_CIRCLE_SIZE.set(CIRCLE_SIZE);
        HALF_CIRCLE_SIZE.division(2);
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        pz.handlePanAndZoom(gc, MouseButton.MIDDLE, 0.001f, true, true);

        Vec2df mouse = new Vec2df((float)gc.getInput().getMouseX(), (float)gc.getInput().getMouseY());
        this.mouse.set(PanAndZoomUtils.screenToWorld(mouse, pz.getWorldOffset(), pz.getWorldScale()));

        if (gc.getInput().isButtonDown(MouseButton.PRIMARY)) {
            if (s == null) {
                s = new Vec2df(this.mouse);
            } else {
                Segment2df segment = new Segment2df(s, this.mouse);
                segment.setId(shapes.size());
                shapes.add(segment);
                s = null;
            }
        }

        if (gc.getInput().isKeyDown(KeyCode.TAB)) {
            isDrawBoundingBox = !isDrawBoundingBox;
        }

    }

    @Override
    public void render(GameApplication gc) {
        screen.set(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Calcule offset starting point and mouse
        Vec2df s = null;
        if (this.s != null) {
            s = new Vec2df(this.s);
            s.sub(HALF_CIRCLE_SIZE);
        }
        Vec2df mouse = new Vec2df(this.mouse);
        mouse.sub(HALF_CIRCLE_SIZE);

        // Draw the background
        pz.setFill(Color.DARKBLUE);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Draw all the shapes
        pz.setStroke(Color.WHITE);
        for (var shape : shapes) {
            if (isDrawBoundingBox) {
                if (shape instanceof Segment2df) {
                    Segment2df segment = (Segment2df) shape;

                    Vec2df pos = segment.getPos();
                    Vec2df end = segment.getEnd();
                    float ox, oy;
                    float dx, dy;
                    if (end.getX() > pos.getX()) {
                        ox = pos.getX();
                        dx = end.getX() - pos.getX();
                    } else if (end.getX() < pos.getX()) {
                        ox = end.getX();
                        dx = pos.getX() - end.getX();
                    } else {
                        ox = pos.getX();
                        dx = 1;
                    }

                    if (end.getY() > pos.getY()) {
                        oy = pos.getY();
                        dy = end.getY() - pos.getY();
                    } else if (end.getY() < pos.getY()) {
                        oy = end.getY();
                        dy = pos.getY() - end.getY();
                    } else {
                        oy = pos.getY();
                        dy = 1;
                    }

                    pz.setStroke(Color.RED);
                    pz.strokeRect(new Vec2df(ox, oy), new Vec2df(dx, dy));
                }
            }
            pz.setStroke(Color.WHITE);
            shape.drawYourself(pz);
        }

        // Draw the intersection points
        if (shapes.size() > 1) {
            List<Vec2df> intersectionPoints = new ArrayList<>();
            for (var s1 : shapes) {
                for (var s2 : shapes) {
                    if (!s1.equals(s2)) {
                        if (s1.overlaps(s2)) {
                            Segment2df seg1 = (Segment2df) s1;
                            Segment2df seg2 = (Segment2df) s2;

                            intersectionPoints.add(seg1.getIntersectionPoint(seg2));
                        }
                    }
                }
            }

            for (var p : intersectionPoints) {
                pz.setStroke(Color.YELLOW);
                Vec2df pos = new Vec2df(p);
                pos.sub(HALF_CIRCLE_SIZE);
                pz.strokeOval(pos, CIRCLE_SIZE);
            }
        }

        // Draw the building segment
        if (s != null) {
            pz.setStroke(Color.RED);
            Vec2df pos = new Vec2df(s);
            pos.sub(HALF_CIRCLE_SIZE);
            pz.strokeOval(pos, CIRCLE_SIZE);

            pz.setStroke(Color.WHITE);
            pz.strokeLine(pos, mouse);
        }

        // Draw mouse
        pz.setStroke(Color.GREEN);
        pz.strokeOval(mouse, CIRCLE_SIZE);

        // Draw text
        final float textLeading = 17;
        Vec2df textPos = new Vec2df(10, 20);
        gc.getGraphicsContext().setFill(Color.WHITE);
        gc.getGraphicsContext().fillText(String.format("Mouse: %.3f %.3f", this.mouse.getX(), this.mouse.getY()), textPos.getX(), textPos.getY());
        textPos.addToY(textLeading);
        gc.getGraphicsContext().fillText(String.format("Number of shapes: %d", shapes.size()),textPos.getX(), textPos.getY());
        textPos.addToY(textLeading);
        gc.getGraphicsContext().fillText(String.format("Drawing bounding box: %b", isDrawBoundingBox),textPos.getX(), textPos.getY());
        textPos.addToY(textLeading);
        gc.getGraphicsContext().fillText(String.format("Pan: %.3f %.3f Zoom: %.3f %.3f", pz.getWorldOffset().getX(), pz.getWorldOffset().getY(), pz.getWorldScale().getX(), pz.getWorldScale().getY()),textPos.getX(), textPos.getY());

    }
}
