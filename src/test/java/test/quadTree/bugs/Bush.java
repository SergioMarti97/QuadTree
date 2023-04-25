package test.quadTree.bugs;

import base.vectors.points2d.Vec2df;
import javafx.scene.image.Image;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomDrawable;
import physics.spaceDivision.Rect;

public class Bush implements PanAndZoomDrawable {

    private int id;

    private Vec2df pos;

    private Vec2df scale;

    private Image img;

    public Bush() {
        this.pos = new Vec2df();
        this.scale = new Vec2df();
        img = null;
    }

    public Bush(int id, Vec2df pos, Vec2df scale, Image imgId) {
        this.id = id;
        this.pos = pos;
        this.scale = scale;
        this.img = imgId;
    }

    // Methods

    public Rect getArea() {
        return new Rect(pos, scale);
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

    public Vec2df getScale() {
        return scale;
    }

    public void setScale(Vec2df scale) {
        this.scale = scale;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    @Override
    public void drawYourSelf(PanAndZoom pz) {
        pz.drawImage(img, pos, scale);
    }

}
