package quadTree.bugs;

import base.vectors.points2d.Vec2df;
import javafx.scene.image.Image;

public class Bush {

    private Vec2df pos;

    private Vec2df scale;

    private int imgId;

    public Bush() {
        this.pos = new Vec2df();
        this.scale = new Vec2df();
        imgId = -1;
    }

    public Bush(Vec2df pos, Vec2df scale, int imgId) {
        this.pos = pos;
        this.scale = scale;
        this.imgId = imgId;
    }

    public Vec2df getPos() {
        return pos;
    }

    public void setPos(Vec2df pos) {
        this.pos = pos;
    }

    public Vec2df getScale() {
        return scale;
    }

    public void setScale(Vec2df scale) {
        this.scale = scale;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
