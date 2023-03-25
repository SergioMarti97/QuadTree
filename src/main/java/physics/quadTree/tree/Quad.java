package physics.quadTree.tree;

import base.vectors.points2d.Vec2df;

public class Quad {

    private Vec2df pos = new Vec2df();

    private Vec2df size = new Vec2df();

    public void set(Vec2df pos, Vec2df size) {
        this.pos = pos;
        this.size = size;
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
}
