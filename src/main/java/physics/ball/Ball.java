package physics.ball;

import base.vectors.points2d.Vec2df;
import panAndZoom.PanAndZoom;
import javafx.scene.paint.Color;
import physics.spaceDivision.Rect;

import java.util.Objects;

public class Ball {

    private int id;

    private Vec2df pos;

    private Vec2df ori;

    private Vec2df size;

    private float radius;

    private Vec2df vel;

    private Color color;

    public Ball() {
        this.id = -1;
        this.pos = new Vec2df();
        this.size = new Vec2df();
        this.ori = new Vec2df();
        this.vel = new Vec2df();
        this.radius = 0;
        this.color = null;
    }

    public Ball(int id, Vec2df pos, Vec2df vel, Vec2df size, Color color) {
        this.id = id;
        this.pos = pos;
        this.vel = vel;
        this.size = size;
        this.color = color;
        this.ori = new Vec2df();
        calOri();
        calRadius();
    }

    public void draw(PanAndZoom pz) {
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
    }

    public void calOri() {
        ori.set(
                pos.getX() + size.getX() / 2f,
                pos.getY() + size.getY() / 2f
        );
    }

    public void calRadius() {
        radius = size.getX() / 2f;
    }

    // Functionalities

    public void calPos() {
        pos.set(
                ori.getX() - size.getX() / 2f,
                ori.getY() - size.getY() / 2f
        );
    }

    public float dist(Ball b) {
        return ori.dist(b.ori);
    }

    public boolean overlaps(Ball b) {
        return (pos.getX() < b.pos.getX() + b.size.getX() && pos.getX() + size.getX() >= b.pos.getX() && pos.getY() < b.pos.getY() + b.size.getY() && pos.getY() + size.getY() >= b.pos.getY());
    }
    
    public boolean doCirclesOverlap(Ball b) {
        return (this.ori.getX() - b.getOri().getX()) * (this.ori.getX() - b.getOri().getX()) +
                (this.ori.getY() - b.getOri().getY()) * (this.ori.getY() - b.getOri().getY()) <=
                (radius + b.getRadius()) * (radius + b.getRadius());
    }

    /**
     * This method returns the sear area based on the position and the velocity
     * @return a rect to search other colliding balls
     */
    public Rect getSearchRect(float dt) {
        Rect ballArea = new Rect();

        // Si no se va a modificar la posición de este Rect, se puede pasar sin instanciar un nuevo vector
        ballArea.getPos().set(pos);

        if (Math.abs(vel.getX()) + size.getX() / 2 < size.getX()) { // vel.getX() == 0
            ballArea.getSize().setX(size.getX());
        } else {
            float velX = vel.getX() * dt;
            ballArea.getSize().setX(velX);
            if (velX < 0) {
                velX = -velX;
                ballArea.getSize().setX(velX);

                ballArea.getPos().addToX(-velX);
                ballArea.getSize().addToX(size.getX());
            }
        }

        if (Math.abs(vel.getY()) + size.getY() / 2 < size.getY()) { // vel.getY() == 0
            ballArea.getSize().setY(size.getY());
        } else {
            float velY = vel.getY() * dt;
            ballArea.getSize().setY(velY);
            if (velY < 0) {
                velY = -velY;
                ballArea.getSize().setY(velY);

                ballArea.getPos().addToY(-velY);
                ballArea.getSize().addToY(size.getY());
            }
        }

        return ballArea;
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

    public float getRadius() {
        return radius;
    }

    public Vec2df getOri() {
        return ori;
    }

    @Override
    public String toString() {
        return "id: " + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ball)) return false;
        Ball ball = (Ball) o;
        return id == ball.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pos, ori, size, radius, vel, color);
    }

}
