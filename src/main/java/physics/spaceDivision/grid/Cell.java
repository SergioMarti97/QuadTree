package physics.spaceDivision.grid;

import base.vectors.points2d.Vec2di;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.Set;

public class Cell<T> {

    private int id = -1;

    /**
     * The position on the grid: num col (x) and num row (y)
     */
    private Vec2di pos;

    /**
     * The area of this cell
     */
    private Rect rect;

    /**
     * The items of this cell
     */
    private final ArrayList<Pair<Rect, T>> items = new ArrayList<>();

    // Constructors

    public Cell(int id, Vec2di pos, Rect rect) {
        this.id = id;
        this.pos = pos;
        this.rect = rect;
    }

    public Cell(Vec2di pos, Rect rect) {
        this.pos = pos;
        this.rect = rect;
    }

    // Methods

    public void clear() {
        items.clear();
    }

    public int size() {
        return items.size();
    }

    public void items(Set<T> list) {
        for (var item : this.items) {
            list.add(item.getValue());
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Draw yourself method

    public void draw(PanAndZoom pz) {
        pz.strokeRect(rect.getPos(), rect.getSize());
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Vec2di getPos() {
        return pos;
    }

    public Rect getRect() {
        return rect;
    }

    public ArrayList<Pair<Rect, T>> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Cell {id: " + id + " pos: " + pos + " rect: " + rect + " items: " + size() + "}";
    }
}
