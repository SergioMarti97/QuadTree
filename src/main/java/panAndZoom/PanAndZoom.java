package panAndZoom;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
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
        Vec2df s2 = worldToScreen(s);
        Vec2df e2 = worldToScreen(e);
        gc.strokeLine(s2.getX(), s2.getY(), e2.getX(), e2.getY());
    }

    public void fillRect(Vec2df pos, Vec2df size) {
        Vec2df pos2 = worldToScreen(pos);
        Vec2df size2 = scaleToScreen(size);
        gc.fillRect(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());
    }

    public void strokeRect(Vec2df pos, Vec2df size) {
        Vec2df pos2 = worldToScreen(pos);
        Vec2df size2 = scaleToScreen(size);

        float copy = (float) gc.getLineWidth();
        float stroke = scaleToScreenX(copy);
        gc.setLineWidth(stroke);

        gc.strokeRect(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());

        gc.setLineWidth(copy);
    }

    public void fillOval(Vec2df pos, Vec2df size) {
        Vec2df pos2 = worldToScreen(pos);
        Vec2df size2 = scaleToScreen(size);
        gc.fillOval(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());
    }

    public void strokeOval(Vec2df pos, Vec2df size) {
        Vec2df pos2 = worldToScreen(pos);
        Vec2df size2 = scaleToScreen(size);

        float copy = (float) gc.getLineWidth();
        float stroke = scaleToScreenX(copy);
        gc.setLineWidth(stroke);

        gc.strokeOval(pos2.getX(), pos2.getY(), size2.getX(), size2.getY());

        gc.setLineWidth(copy);
    }

    public void fillText(String text, float offX, float offY) {
        Font f = gc.getFont();
        double fontSize = f.getSize();
        double newFontSize = scaleToScreenX((float) fontSize);
        gc.setFont(new Font(f.getName(), newFontSize));
        float x2 = worldToScreenX(offX);
        float y2 = worldToScreenY(offY);
        gc.fillText(text, x2, y2);
        gc.setFont(f);
    }

    public void strokeText(String text, float offX, float offY) {
        float x2 = worldToScreenX(offX);
        float y2 = worldToScreenY(offY);
        gc.strokeText(text, x2, y2);
    }

    // --- Draw Images

    public void drawImage(Image img, float offX, float offY) {
        float x2 = worldToScreenX(offX);
        float y2 = worldToScreenY(offY);
        gc.drawImage(img, x2, y2);
    }

    public void drawImage(Image img, Vec2df offset) {
        Vec2df o = worldToScreen(offset);
        gc.drawImage(img, o.getX(), o.getY());
    }

    public void drawImage(Image img, float offX, float offY, float width, float height) {
        float x2 = worldToScreenX(offX);
        float y2 = worldToScreenY(offY);
        float w2 = scaleToScreenX(width);
        float h2 = scaleToScreenY(height);
        gc.drawImage(img, x2, y2, w2, h2);
    }

    public void drawImage(Image img, Vec2df offset, Vec2df size) {
        Vec2df o = worldToScreen(offset);
        Vec2df s = scaleToScreen(size);
        gc.drawImage(img, o.getX(), o.getY(), s.getX(), s.getY());
    }

    public void drawImage(Image img,
                          float sx,
                          float sy,
                          float sw,
                          float sh,
                          float dx,
                          float dy,
                          float dw,
                          float dh) {
        float dx2 = worldToScreenX(dx);
        float dy2 = worldToScreenY(dy);
        float dw2 = scaleToScreenX(dw);
        float dh2 = scaleToScreenY(dh);

        gc.drawImage(img, sx, sy, sw, sh, dx2, dy2, dw2, dh2);
    }

    public void drawImage(Image img,
                          float sx,
                          float sy,
                          float sw,
                          float sh,
                          Vec2df pos,
                          Vec2df scale) {
        Vec2df p = worldToScreen(pos);
        Vec2df s = scaleToScreen(scale);

        gc.drawImage(img,
                sx, sy,
                sw, sh,
                p.getX(), p.getY(),
                s.getX(), s.getY());
    }

    public void drawImage(Image img,
                          Vec2df sourceOri,
                          Vec2df sourceDim,
                          Vec2df pos,
                          Vec2df scale) {
        Vec2df p = worldToScreen(pos);
        Vec2df s = scaleToScreen(scale);

        gc.drawImage(img,
                sourceOri.getX(), sourceOri.getY(),
                sourceDim.getX(), sourceDim.getY(),
                p.getX(), p.getY(),
                s.getX(), s.getY());
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

    // Utilidades

    public float worldToScreenX(float x) {
        return PanAndZoomUtils.worldToScreen(x, worldOffset.getX(), worldScale.getX());
    }

    public float worldToScreenY(float y) {
        return PanAndZoomUtils.worldToScreen(y, worldOffset.getY(), worldScale.getY());
    }

    public Vec2df worldToScreen(Vec2df v) {
        return PanAndZoomUtils.worldToScreen(v, worldOffset, worldScale);
    }

    // ---

    public float screenToWorldX(float x) {
        return PanAndZoomUtils.screenToWorld(x, worldOffset.getX(), worldScale.getX());
    }

    public float screenToWorldY(float y) {
        return PanAndZoomUtils.screenToWorld(y, worldOffset.getY(), worldScale.getY());
    }

    public Vec2df screenToWorld(Vec2df v) {
        return PanAndZoomUtils.screenToWorld(v, worldOffset, worldScale);
    }

    // ---

    public float scaleToScreenX(float x) {
        return PanAndZoomUtils.scaleToScreen(x, worldScale.getX());
    }

    public float scaleToScreenY(float y) {
        return PanAndZoomUtils.scaleToScreen(y, worldScale.getY());
    }

    public Vec2df scaleToScreen(Vec2df v) {
        return PanAndZoomUtils.scaleToScreen(v, worldScale);
    }

    // ---

    public float scaleToWorldX(float x) {
        return PanAndZoomUtils.scaleToWorld(x, worldScale.getX());
    }

    public float scaleToWorldY(float y) {
        return PanAndZoomUtils.scaleToWorld(y, worldScale.getY());
    }

    public Vec2df scaleToWorld(Vec2df v) {
        return PanAndZoomUtils.scaleToWorld(v, worldScale);
    }

}
