package physics.quadTree;

import panAndZoom.PanAndZoom;

import java.util.*;

public class QuadTreeContainer<T> {

    private HashMap<T, Rect> items;

    private QuadTree<T> root;

    public QuadTreeContainer(Rect rect, int depth) {
        root = new QuadTree<>(rect, depth);
        items = new HashMap<>();
    }

    public QuadTreeContainer(Rect rect) {
        root = new QuadTree<>(rect);
        items = new HashMap<>();
    }

    public QuadTreeContainer() {
        root = new QuadTree<>();
        items = new HashMap<>();
    }

    // ---

    public void resize(Rect area) {
        root.resize(area);
    }

    public int size() {
        return items.size();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
        root.clear();
    }

    // ---

    /*public Iterator<QuadTreeItemLocation<T>> iterator() {
        return items.iterator();
    }*/

    // ---

    public void insert(T item, Rect itemSize) {
        // Rect loc = root.insert(item, itemSize);
        // items.put(item, loc);
        root.insert(item, itemSize);
        items.put(item, itemSize);
    }

    public List<T> search(Rect rect) {
        return root.search(rect);
    }

    public boolean remove(T itemToRemove) {

        if (!items.containsKey(itemToRemove)) {
            return false;
        }

        Rect itemArea = items.remove(itemToRemove);

        if (itemArea != null) {
            return root.remove(itemToRemove, itemArea);
        }
        return false;

    }

    public void relocate(T item) {
        Rect itemArea = items.remove(item);
        if (itemArea != null) {
            root.remove(item, itemArea);
        }
        insert(item, itemArea);
    }

    public void relocate(T item, Rect newArea) {
        remove(item);
        insert(item, newArea);
    }

    public Collection<T> getValues() {
        return items.keySet();
    }

    public List<T> getItems() {
        return root.items();
    }

    public QuadTree<T> getRoot() {
        return root;
    }

    // drawYourSelf

    public void draw(PanAndZoom pz, Rect screen) {
        root.draw(pz, screen);
    }

}
