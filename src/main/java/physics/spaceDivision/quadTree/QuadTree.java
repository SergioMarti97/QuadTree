package physics.spaceDivision.quadTree;

import base.vectors.points2d.Vec2df;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.List;

public class QuadTree<T> {

    private final int NUM_CHILDREN = 4;

    private final int MAX_DEPTH = 10;

    private Rect rect;

    private Rect[] rectChildren = new Rect[NUM_CHILDREN];

    private QuadTree<T>[] children = new QuadTree[NUM_CHILDREN];

    private final ArrayList<Pair<Rect, T>> items = new ArrayList<>();

    private int depth = 0;

    public QuadTree(Rect rect, int depth) {
        this.depth = depth;
        resize(rect);
    }

    public QuadTree(Rect rect) {
        this(rect, 0);
    }

    public QuadTree() {
        this(new Rect(0, 0, 100, 100), 0);
    }

    // Methods

    public void resize(Rect area) {
        clear();
        rect = area;

        Vec2df childSize = new Vec2df(rect.getSize());
        childSize.multiply(0.5f);

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

    public void insert(T item, Rect itemArea) {
        // Comprobar si el objeto a insertar encaja en alguna de las 4 áreas hijas
        for (int i = 0; i < NUM_CHILDREN; i++) {
            try {
                if (rectChildren[i].contains(itemArea)) {
                    if (depth + 1 < MAX_DEPTH) {

                        if (children[i] == null) {
                            children[i] = new QuadTree<T>(rectChildren[i], depth + 1);
                        }

                        children[i].insert(item, itemArea);
                        return;
                    }
                }
            } catch (NullPointerException e) {
                System.out.println(e.getMessage());
            }
        }

        items.add(new Pair<>(itemArea, item));
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

    public List<T> items() {
        ArrayList<T> list = new ArrayList<>();
        items(list);
        return list;
    }

    public boolean hasChildren() {
        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (children[i] != null) {
                return true;
            }
        }
        return false;
    }

    public boolean isEmpty() {
        if (!hasChildren()) {
            return items.isEmpty();
        } else {
            boolean isEmpty = items.isEmpty();
            if (isEmpty) {

                for (int i = 0; i < NUM_CHILDREN; i++) {
                    if (children[i] != null) {
                        if (!children[i].isEmpty()) {
                            return false;
                        }
                    }
                }
                return true;

            } else {
                return false;
            }
        }
    }

    /**
     * Para que funcione eficientemente la eliminación recursiva,
     * al algoritmo se le debe de pasar 2 cosas: el elemento y su ubicación (área).
     * Comprobar si un elemento hijo tiene o no items. Si no tiene, se debe eliminar.
     * @param item the item to remove
     * @param itemArea the location of the item
     * @return true or false if the item is removed
     */
    public boolean remove(T item, Rect itemArea) {
        boolean isHere = items.removeIf(p -> p.getValue() == item);
        if (!isHere) {
            for (int i = 0; i < NUM_CHILDREN; i++) {
                if (children[i] != null) {

                    if (rectChildren[i].contains(itemArea)) {
                        boolean isRemoved = children[i].remove(item, itemArea);

                        if (children[i].isEmpty()) {
                            children[i] = null;
                        }

                        return isRemoved;
                    }

                }
            }
            return false;
        } else {
            return isHere;
        }
    }

    // Getters & Setters

    public Rect getRect() {
        return rect;
    }

    public Rect[] getRectChildren() {
        return rectChildren;
    }

    public QuadTree<T>[] getChildren() {
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

    // DrawYourself
    public void draw(PanAndZoom pz, Rect screen) {
        if (screen.contains(rect)) {
            pz.strokeRect(rect.getPos(), rect.getSize());
        }
        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (children[i] != null) {
                if (screen.contains(rectChildren[i])) {
                    children[i].draw(pz, screen);
                } else if (screen.overlaps(rectChildren[i])) {
                    children[i].draw(pz, screen);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "QuadTree {children: " + children.length + "}";
    }
}
