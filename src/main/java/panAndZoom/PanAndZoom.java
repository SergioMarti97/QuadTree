package panAndZoom;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.text.Font;

public class PanAndZoom {

    protected Vec2df startPan;

    protected Vec2df worldOffset;

    protected Vec2df worldScale;

    protected boolean isPanning = false;

    protected boolean isZooming = false;

    private GraphicsContext gc;

    public PanAndZoom(GraphicsContext gc) {
        startPan = new Vec2df();
        worldOffset = new Vec2df();
        worldScale = new Vec2df(1);
        this.gc = gc;
    }

    // Methods

    public void startPan(Vec2df pos) {
        isPanning = true;
        startPan.set(pos);
    }

    public void updatePan(Vec2df pos) {
        if (isPanning) {
            worldOffset.addToX(-((pos.getX() - startPan.getX()) / worldScale.getX()));
            worldOffset.addToY(-((pos.getY() - startPan.getY()) / worldScale.getY()));

            startPan.set(pos);
        }
    }

    public void endPan(Vec2df pos) {
        updatePan(pos);
        isPanning = false;
    }

    public void zoomAtScreenPos(float deltaZoom, Vec2df pos) {
        Vec2df offsetBeforeZoom = PanAndZoomUtils.screenToWorld(pos, worldOffset, worldScale);
        worldScale.multiply(deltaZoom);
        Vec2df offsetAfterZoom = PanAndZoomUtils.screenToWorld(pos, worldOffset, worldScale);
        worldOffset.addToX(offsetBeforeZoom.getX() - offsetAfterZoom.getX());
        worldOffset.addToY(offsetBeforeZoom.getY() - offsetAfterZoom.getY());
    }

    public void setZoom(float zoomX, float zoomY, Vec2df pos) {
        Vec2df offsetBeforeZoom = PanAndZoomUtils.screenToWorld(pos, worldOffset, worldScale);
        worldScale.set(zoomX, zoomY);
        Vec2df offsetAfterZoom = PanAndZoomUtils.screenToWorld(pos, worldOffset, worldScale);
        worldOffset.addToX(offsetBeforeZoom.getX() - offsetAfterZoom.getX());
        worldOffset.addToY(offsetBeforeZoom.getY() - offsetAfterZoom.getY());
    }

    public void setZoom(Vec2df zoom, Vec2df pos) {
        setZoom(zoom.getX(), zoom.getY(), pos);
    }

    public void setZoom(float zoom, Vec2df pos) {
        setZoom(zoom, zoom, pos);
    }

    public void handlePanAndZoom(GameApplication gc, MouseButton mouseButton, float zoomRate, boolean isPan, boolean isZoom) {

        Vec2df mousePos = new Vec2df(
                (float)gc.getInput().getMouseX(),
                (float)gc.getInput().getMouseY()
        );

        if (isPan) {
            if (gc.getInput().isButtonDown(mouseButton)) {
                startPan(mousePos);
            }
            if (gc.getInput().isButtonHeld(mouseButton)) {
                updatePan(mousePos);
            }
            if (gc.getInput().isButtonUp(mouseButton)) {
                endPan(mousePos);
            }
        }

        if (isZoom) {
            if (gc.getInput().getScroll() != 0) {
                zoomAtScreenPos(1.0f + (zoomRate * gc.getInput().getScroll()), mousePos);
            }
        }

    }

    // Drawing methods

    public void strokeLine(Vec2df s, Vec2df e) {
        Vec2df s2 = PanAndZoomUtils.worldToScreen(s, worldOffset, worldScale);
        Vec2df e2 = PanAndZoomUtils.worldToScreen(e, worldOffset, worldScale);
        gc.strokeLine(s2.getX(), s2.getY(), e2.getX(), e2.getY());
    }

    public void fillRect(Vec2df pos, Vec2df size) {
        Vec2df pos2 = PanAndZoomUtils.worldToScreen(pos, worldOffset, worldScale);
        Vec2df size2 = PanAndZoomUtils.scaleToScreen(size, worldScale);
        gc.fillRect(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());
    }

    public void strokeRect(Vec2df pos, Vec2df size) {
        Vec2df pos2 = PanAndZoomUtils.worldToScreen(pos, worldOffset, worldScale);
        Vec2df size2 = PanAndZoomUtils.scaleToScreen(size, worldScale);

        float copy = (float) gc.getLineWidth();
        float stroke = PanAndZoomUtils.scaleToScreen(copy, worldScale.getX());
        gc.setLineWidth(stroke);

        gc.strokeRect(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());

        gc.setLineWidth(copy);
    }

    public void fillOval(Vec2df pos, Vec2df size) {
        Vec2df pos2 = PanAndZoomUtils.worldToScreen(pos, worldOffset, worldScale);
        Vec2df size2 = PanAndZoomUtils.scaleToScreen(size, worldScale);
        gc.fillOval(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());
    }

    public void strokeOval(Vec2df pos, Vec2df size) {
        Vec2df pos2 = PanAndZoomUtils.worldToScreen(pos, worldOffset, worldScale);
        Vec2df size2 = PanAndZoomUtils.scaleToScreen(size, worldScale);

        float copy = (float) gc.getLineWidth();
        float stroke = PanAndZoomUtils.scaleToScreen(copy, worldScale.getX());
        gc.setLineWidth(stroke);

        gc.strokeOval(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());

        gc.setLineWidth(copy);
    }

    public void fillText(String text, float offX, float offY) {
        Font f = gc.getFont();
        double fontSize = f.getSize();
        double newFontSize = PanAndZoomUtils.scaleToScreen((float) fontSize, worldScale.getX());
        gc.setFont(new Font(f.getName(), newFontSize));
        float x2 = PanAndZoomUtils.worldToScreen(offX, worldOffset.getX(), worldScale.getX());
        float y2 = PanAndZoomUtils.worldToScreen(offY, worldOffset.getY(), worldScale.getY());
        gc.fillText(text, x2, y2);
        gc.setFont(f);
    }

    public void strokeText(String text, float offX, float offY) {
        float x2 = PanAndZoomUtils.worldToScreen(offX, worldOffset.getX(), worldScale.getX());
        float y2 = PanAndZoomUtils.worldToScreen(offY, worldOffset.getY(), worldScale.getY());
        gc.strokeText(text, x2, y2);
    }

    // Getters & Setters

    public GraphicsContext getGc() {
        return gc;
    }

    public void setGc(GraphicsContext gc) {
        this.gc = gc;
    }

    public Vec2df getStartPan() {
        return startPan;
    }

    public Vec2df getWorldOffset() {
        return worldOffset;
    }

    public Vec2df getWorldScale() {
        return worldScale;
    }

    public boolean isPanning() {
        return isPanning;
    }

    public boolean isZooming() {
        return isZooming;
    }

    public Vec2df getWorldTopLeft() {
        Vec2df world = new Vec2df();
        return PanAndZoomUtils.screenToWorld(world, worldOffset, worldScale);
    }

    public Vec2df getWorldBottomRight() {
        Vec2df viewArea = new Vec2df(
                (float)gc.getCanvas().getWidth(),
                (float)gc.getCanvas().getHeight()
        );
        return PanAndZoomUtils.screenToWorld(viewArea, worldOffset, worldScale);
    }

    public Vec2df getWorldVisibleArea() {
        Vec2df worldTopLeft = getWorldTopLeft();
        Vec2df worldBottomRight = getWorldBottomRight();
        worldTopLeft.multiply(-1);
        worldBottomRight.add(worldTopLeft);
        return worldBottomRight;
    }

}
