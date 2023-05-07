package physics.verlet;

import base.vectors.points2d.Vec2df;
import shapes.Circle2df;
import shapes.Shape2df;

public class VerletCircle2df extends Circle2df implements VerletParticle {

    /**
     * Old position
     */
    private Vec2df oldPos = new Vec2df();

    /**
     * Acceleration
     */
    private Vec2df acc = new Vec2df();

    // Constructors

    public VerletCircle2df() {
        super();
    }

    public VerletCircle2df(float x, float y, float radius) {
        super(x, y, radius);
    }

    public VerletCircle2df(Vec2df pos, float radius) {
        super(pos, radius);
    }

    public VerletCircle2df(int id, float x, float y, float radius) {
        super(id, x, y, radius);
    }

    public VerletCircle2df(int id, Vec2df pos, float radius) {
        super(id, pos, radius);
    }

    public VerletCircle2df(Shape2df shape) {
        super(shape);
    }

    // Getters & Setters

    public Vec2df getOldPos() {
        return oldPos;
    }

    public void setOldPos(Vec2df oldPos) {
        this.oldPos = oldPos;
    }

    public Vec2df getAcc() {
        return acc;
    }

    public void setAcc(Vec2df acc) {
        this.acc = acc;
    }

    // Override methods

    @Override
    public void doVerletStep(float dt) {
        // Calculate velocity
        Vec2df vel = new Vec2df(pos);
        vel.sub(oldPos);

        // Save current position
        oldPos.set(pos);

        // Perform Verlet integration
        Vec2df acc = new Vec2df(this.acc);
        acc.multiply(dt * dt);
        pos.add(vel);
        pos.add(acc);

        // Reset acceleration
        this.acc.set(0);
    }

    @Override
    public void accelerate(Vec2df acc) {
        this.acc.add(acc);
    }

}
