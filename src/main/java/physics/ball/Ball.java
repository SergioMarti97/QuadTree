package physics.ball;

import base.vectors.points2d.Vec2df;
import physics.grid.Cell;
import panAndZoom.PanAndZoom;
import javafx.scene.paint.Color;

public class Ball {

    private int id;

    private Vec2df pos;

    private Vec2df size;

    private Vec2df ori;

    private Vec2df vel;

    private Color color;

    private Cell cell = null;

    public Ball() {
        this.id = -1;
        this.pos = new Vec2df();
        this.size = new Vec2df();
        this.ori = new Vec2df();
        this.vel = new Vec2df();
        this.color = null;
        this.cell = null;
    }

    public Ball(int id, Vec2df pos, Vec2df vel, Vec2df size, Color color) {
        this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.size = size;
        this.color = color;
        this.ori = new Vec2df();
        calOri();
    }

    public void draw(PanAndZoom pz, float dt) {
        Vec2df oriSize = new Vec2df(4f);

        pz.getGc().setFill(color);

        pz.fillOval(pos, size);
        pz.strokeOval(pos, size);
        pz.strokeRect(pos, size);

        /*Vec2df end = new Vec2df(pos);
        Vec2df space = new Vec2df(vel);
        space.multiply(dt);
        end.add(space);

        pz.fillOval(end, size);
        // pz.strokeOval(end, size);

        end.addToX(size.getX() / 2);
        end.addToY(size.getY() / 2);
        oriSize.multiply(-0.5f);
        end.add(oriSize);
        oriSize.multiply(-2f);
        // pz.strokeOval(end, oriSize);

        Vec2df ori = new Vec2df(pos);
        ori.addToX(size.getX() / 2);
        ori.addToY(size.getY() / 2);

        Vec2df velEnd = new Vec2df(vel);
        velEnd.multiply(100);
        velEnd.add(ori);

        // pz.strokeLine(ori, velEnd);

        oriSize.multiply(-0.5f);
        ori.add(oriSize);
        oriSize.multiply(-2f);
        // pz.strokeOval(ori, oriSize);*/

        pz.getGc().setFill(Color.BLACK);
        pz.fillText(String.format("%d", id), ori.getX(), ori.getY());

        if (cell != null) {
            pz.fillText(cell.toString(), ori.getX(), ori.getY() + 10);
        }
    }

    private void calOri() {
        ori.set(
                pos.getX() + size.getX() / 2f,
                pos.getY() + size.getY() / 2f
        );
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

    public void setPos(Vec2df pos) {
        this.pos = pos;
    }

    public Vec2df getSize() {
        return size;
    }

    public void setSize(Vec2df size) {
        this.size = size;
    }

    public Vec2df getVel() {
        return vel;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    // Other stuff

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    @Override
    public String toString() {
        return "id: " + id + " " + cell.toString();
    }
}
