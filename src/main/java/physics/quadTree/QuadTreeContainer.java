package physics.quadTree;

import javafx.util.Pair;
import panAndZoom.PanAndZoom;

import java.util.*;

@Deprecated
public class QuadTreeContainer<T> {

    private List<Pair<T, Rect>> items;

    private QuadTree<T> root;

    public QuadTreeContainer(Rect rect, int depth) {
        root = new QuadTree<>(rect, depth);
        items = new ArrayList<>();
    }

    public QuadTreeContainer(Rect rect) {
        root = new QuadTree<>(rect);
        items = new ArrayList<>();
    }

    public QuadTreeContainer() {
        root = new QuadTree<>();
        items = new ArrayList<>();
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

    public void insert(T item, Rect itemSize) {
        root.insert(item, itemSize);
        items.add(new Pair<>(item, itemSize));
    }

    public List<T> search(Rect rect) {
        return root.search(rect);
    }

    public boolean remove(T itemToRemove) {

        var p = items.stream().filter(pair -> pair.getKey().equals(itemToRemove)).findAny().orElse(null);

        if (p == null) {
            System.out.println("El elemento " + itemToRemove.toString() + " no se encuentra en los items");
            return false;
        }

        items.remove(p);
        root.remove(p.getKey(), p.getValue());

        return false;
    }

    public boolean relocate(T item, Rect area) {
        /*var p = items.stream().filter(pair -> pair.getKey().equals(item)).findFirst().orElse(null);
        if (p == null) {
            System.out.println("El elemento " + item.toString() + " no se encuentra en los items");
            return false;
        }

        items.set(items.indexOf(p), new Pair<>(item, area));
        root.remove(p.getKey(), p.getValue());
        root.insert(item, area);
        */

        int i;
        for (i = 0; i < items.size(); i++) {
            var p = items.get(i);
            if (p.getKey().equals(item)) {
                root.remove(p.getKey(), p.getValue());
                root.insert(item, area);
                break;
            }
        }
        items.set(i, new Pair<>(item, area));

        return true;
    }

    public List<Pair<T, Rect>> getItems() {
        return items;
    }

    public QuadTree<T> getRoot() {
        return root;
    }

    // drawYourSelf

    public void draw(PanAndZoom pz, Rect screen) {
        root.draw(pz, screen);
    }

}
