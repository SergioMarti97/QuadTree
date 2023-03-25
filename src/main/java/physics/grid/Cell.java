package physics.grid;

import physics.ball.Ball;
import base.vectors.points2d.Vec2df;
import base.vectors.points2d.Vec2di;
import panAndZoom.PanAndZoom;

import java.util.ArrayList;

public class Cell {

    private int id;

    private Vec2di pos;

    private Vec2df ori;

    private Vec2df size;

    private Vec2df end;

    private boolean isChecking = false;

    private final ArrayList<Ball> content;

    // rendering methods;

    private int zIndex = 0;

    public Cell(int id, Vec2di pos, Vec2df ori, Vec2df size) {
        this.id = id;
        this.pos = pos;
        this.ori = ori;
        this.size = size;
        this.end = new Vec2df(ori.getX() + size.getX(), ori.getY() + size.getY());

        content = new ArrayList<>();
    }

    public void draw(PanAndZoom pz) {
        pz.strokeRect(ori, size);
        pz.fillText(pos.toString() + " id " + id, ori.getX() + 5, ori.getY() + 10);
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vec2di getPos() {
        return pos;
    }

    public void setPos(Vec2di pos) {
        this.pos = pos;
    }

    public Vec2df getOri() {
        return ori;
    }

    public void setOri(Vec2df ori) {
        this.ori = ori;
        this.end = new Vec2df(ori.getX() + size.getX(), ori.getY() + size.getY());
    }

    public Vec2df getSize() {
        return size;
    }

    public void setSize(Vec2df size) {
        this.size = size;
        this.end = new Vec2df(ori.getX() + size.getX(), ori.getY() + size.getY());
    }

    // Other things

    public Vec2df getEnd() {
        return end;
    }

    public boolean isChecking() {
        return isChecking;
    }

    public void setChecking(boolean checking) {
        isChecking = checking;
    }

    public ArrayList<Ball> getContent() {
        return content;
    }

    // Rendering things

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    @Override
    public String toString() {
        return "id: " + id + " " + pos.toString() + " contenido: " + content.size();
    }
}
