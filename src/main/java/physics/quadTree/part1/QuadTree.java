package physics.quadTree.part1;

import base.vectors.points2d.Vec2df;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.quadTree.Rect;

import java.util.ArrayList;
import java.util.List;

public class QuadTree<T> {

    private final int NUM_CHILDREN = 4;

    private final int MAX_DEPTH = 10;

    private Rect rect;

    private Rect[] rectChildren = new Rect[NUM_CHILDREN];

    private final QuadTree<T>[] children = new QuadTree[NUM_CHILDREN];

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

        /*if (rectChildren.length > 0) { // TODO no se si asignalo nada mas instanciar el objeto o aquí
            rectChildren = new Rect[NUM_CHILDREN];
        }*/

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

    //public QuadTreeItemLocation<T> insert(T item, Rect itemSize) {
    // public void insert(T item, Rect itemSize) {
    public Rect insert(T item, Rect itemSize) {
        // Comprobar si el objeto a insertar encaja en alguna de las 4 áreas hijas
        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (rectChildren[i].contains(itemSize)) {
                if (depth + 1 < MAX_DEPTH) {

                    if (children[i] == null) {
                        children[i] = new QuadTree<T>(rectChildren[i], depth + 1);
                    }

                    return children[i].insert(item, itemSize);
                    // children[i].insert(item, itemSize);
                    // return;
                }
            }
        }

        // QuadTreeItemLocation itemAndLoc = new QuadTreeItemLocation<>(this, item);
        // items.add(itemAndLoc);
        items.add(new Pair<>(itemSize, item));
        return itemSize;
        // return itemAndLoc;
        //return;
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
        //list.addAll(items.stream()..collect(Collectors.toList()));
        for (var item : items) {
            list.add(item.getValue());
        }

        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (children[i] != null) {
                children[i].items(list);
            }
        }
    }

    public boolean remove(T itemToRemove, Rect loc) {

        boolean isHere = items.removeIf(p -> p.getValue() == itemToRemove);

        if (!isHere) {
            for (int i = 0; i < NUM_CHILDREN; i++) {
                if (children[i] != null) {

                    // Todo, para que funcione eficientemente, el algoritmo se le debe de pasar 2 cosas, el elemento y su ubicación
                    // Todo, comprobar si un elemento hijo no tiene items, eliminarse

                    if (rectChildren[i].contains(loc) || rectChildren[i].overlaps(loc)) {
                        boolean isRemoved = children[i].remove(itemToRemove, loc);

                        if (isRemoved) {
                            if (children[i].getItems().isEmpty() && !children[i].hasChildren()) {
                                children[i] = null;
                            }
                        }

                        return isRemoved;
                    }


                }
            }
            return false;
        } else {
            return true;
        }
    }

    public boolean hasChildren() {
        boolean hasChildren = false;
        for (int i = 0; i < NUM_CHILDREN; i++) {
            if (children[i] != null) {
                return true;
            }
        }
        return false;
    }

    // ---

    // ---

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

            /*Vec2df ori = new Vec2df(rect.getPos());
            Vec2df size = new Vec2df(rect.getSize());
            size.multiply(0.2f);
            ori.add(size);
            pz.fillText("Items: " + items.size(), ori.getX(), ori.getY());*/
        }
        //double thick = pz.getGc().getLineWidth();
        //thick *= 0.8;
        //pz.getGc().setLineWidth(thick);
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

}
