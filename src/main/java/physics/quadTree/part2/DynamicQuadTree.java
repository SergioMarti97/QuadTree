package physics.quadTree.part2;

import panAndZoom.PanAndZoom;

import base.vectors.points2d.Vec2df;
import javafx.util.Pair;
import physics.quadTree.Rect;

import java.util.ArrayList;
import java.util.List;

public class DynamicQuadTree<T> {

    private final int NUM_CHILDREN = 4;

    private final int MAX_DEPTH = 8;

    private Rect rect;

    private Rect[] rectChildren = new Rect[NUM_CHILDREN];

    private final DynamicQuadTree<T>[] children = new DynamicQuadTree[NUM_CHILDREN];

    private final ArrayList<Pair<Rect, T>> items = new ArrayList<>();

    private int depth = 0;

    public DynamicQuadTree(Rect rect, int depth) {
        this.depth = depth;
        resize(rect);
    }

    public DynamicQuadTree(Rect rect) {
        this(rect, 0);
    }

    public DynamicQuadTree() {
        this(new Rect(0, 0, 100, 100), 0);
    }

    // Methods

    public void resize(Rect area) {
        clear();
        rect = area;

        Vec2df childSize = new Vec2df(rect.getSize());
        childSize.multiply(0.5f);

        if (rectChildren.length > 0) { // TODO no se si asignalo nada mas instanciar el objeto o aquí
            rectChildren = new Rect[NUM_CHILDREN];
        }

        rectChildren[0] = new Rect(rect.getPos().getX(), rect.getPos().getY(), childSize.getX(), childSize.getY());
        rectChildren[1] = new Rect(rect.getPos().getX() + childSize.getX(), rect.getPos().getY(), childSize.getX(), childSize.getY());
        rectChildren[2] = new Rect(rect.getPos().getX(), rect.getPos().getY() + childSize.getY(), childSize.getX(), childSize.getY());
        rectChildren[3] = new Rect(rect.getPos().getX() + childSize.getX(), rect.getPos().getY() + childSize.getY(), childSize.getX(), childSize.getY());
    }

    public void clear() {
        items.clear();

        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (children[i] != null) {
                children[i].clear();
            }
            children[i] = null;
        }
    }

    public int size() {
        int count = items.size();
        for (var child : children) {
            if (child != null) {
                count += child.size();
            }
        }
        return count;
    }

    public void insert(T item, Rect itemSize) {
        // Comprobar si el objeto a insertar encaja en alguna de las 4 áreas hijas
        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (rectChildren[i].contains(itemSize)) {
                if (depth + 1 < MAX_DEPTH) {

                    if (children[i] == null) {
                        children[i] = new DynamicQuadTree<T>(rectChildren[i], depth + 1);
                    }

                    children[i].insert(item, itemSize);
                    return;
                }
            }
        }

        items.add(new Pair<>(itemSize, item));
    }

    public List<T> search(Rect area) {
        ArrayList<T> listItems = new ArrayList<>();
        search(area, listItems);
        return listItems;
    }

    private void search(Rect area, List<T> listItems) {
        for (var item : items) {
            if (area.overlaps(item.getKey())) {
                listItems.add(item.getValue());
            }
        }

        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (children[i] != null) {
                if (area.contains(rectChildren[i])) {
                    children[i].items(listItems);
                } else if (rectChildren[i].overlaps(area)) {
                    children[i].search(area, listItems);
                }
            }
        }
    }

    private void items(List<T> list) {
        for (var item : items) {
            list.add(item.getValue());
        }

        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (children[i] != null) {
                children[i].items(list);
            }
        }
    }

    public boolean remove(T itemToRemove) {
        boolean isHere = items.removeIf(p -> p.getValue() == itemToRemove);

        if (!isHere) {
            for (int i = 0; i < NUM_CHILDREN; i++) {
                if (children[i] != null) {
                    if (children[i].remove(itemToRemove)) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            return true;
        }
    }

    // Getters & Setters

    public Rect getRect() {
        return rect;
    }

    public Rect[] getRectChildren() {
        return rectChildren;
    }

    public DynamicQuadTree<T>[] getChildren() {
        return children;
    }

    public ArrayList<Pair<Rect, T>> getItems() {
        return items;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

}
