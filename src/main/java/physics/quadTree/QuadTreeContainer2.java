package physics.quadTree;

import javafx.util.Pair;
import panAndZoom.PanAndZoom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Deprecated
public class QuadTreeContainer2<T> {

    private HashMap<T, Rect> items;

    private QuadTree<T> root;

    public QuadTreeContainer2(Rect rect, int depth) {
        root = new QuadTree<>(rect, depth);
        items = new HashMap<>();
    }

    public QuadTreeContainer2(Rect rect) {
        root = new QuadTree<>(rect);
        items = new HashMap<>();
    }

    public QuadTreeContainer2() {
        root = new QuadTree<>();
        items = new HashMap<>();
    }

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

    public void insert(T item, Rect itemSize) {
        root.insert(item, itemSize);
        items.put(item, itemSize);
    }

    public List<T> search(Rect rect) {
        return root.search(rect);
    }

    public boolean remove(T item) {
        Rect area = items.remove(item);
        root.remove(item, area);
        return true;
    }

    public boolean relocate(T item, Rect area) {
        Rect oldArea = items.get(item);
        if (oldArea != null) {
            items.replace(item, oldArea, area);
            root.remove(item, oldArea);
            root.insert(item, area);
        } else {
            System.out.println("El item: " + item.toString() + " tiene un área nula");
        }
        return true;
    }

    public Collection<T> getItems() {
        return items.keySet();
    }

    public QuadTree<T> getRoot() {
        return root;
    }

    // drawYourSelf

    public void draw(PanAndZoom pz, Rect screen) {
        root.draw(pz, screen);
    }


}
