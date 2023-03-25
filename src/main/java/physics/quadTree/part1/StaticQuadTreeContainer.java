package physics.quadTree.part1;

import panAndZoom.PanAndZoom;
import physics.quadTree.Rect;
import physics.quadTree.part2.QuadTreeItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StaticQuadTreeContainer<T> {

    private StaticQuadTree<Integer> root;

    private HashMap<Integer, T> items;

    public StaticQuadTreeContainer(Rect rect, int depth) {
        root = new StaticQuadTree<>(rect, depth);
        items = new HashMap<>();
    }

    public StaticQuadTreeContainer(Rect rect) {
        root = new StaticQuadTree<>(rect);
        items = new HashMap<>();
    }

    public StaticQuadTreeContainer() {
        root = new StaticQuadTree<>();
        items = new HashMap<>();
    }

    public void clear() {
        items.clear();
        root.clear();
    }

    public int size() {
        return items.size();
    }

    public void insert(T item, Rect itemSize) {
        int id = items.size();
        items.put(id, item);

        QuadTreeItem<T> newItem = new QuadTreeItem<>(item);

        root.insert(id, itemSize);
    }

    public List<T> search(Rect rect) {
        List<Integer> list = root.search(rect);
        List<T> l = new ArrayList<>();
        for (var id : list) {
            l.add(items.get(id));
        }
        return l;
    }

    public boolean remove(T itemToRemove) {
        AtomicInteger idToRemove = new AtomicInteger();
        long t1 = System.nanoTime();
        boolean isRemoved = items.entrySet().removeIf(e -> {
            if (e.getValue() == itemToRemove) {
                idToRemove.set(e.getKey());
                return true;
            } else {
                return false;
            }
        });
        long t2 = System.nanoTime();
        float elapsedTime = (t2 - t1) / 1000000000f;
        System.out.printf("Tiempo tomado para borrar los elementos de la lista de items: %6f\n", elapsedTime);
        t1 = System.nanoTime();
        root.remove(idToRemove.get());
        t2 = System.nanoTime();
        elapsedTime = (t2 - t1) / 1000000000f;
        System.out.printf("Tiempo tomado para borrar los elementos del arbol: %6f\n", elapsedTime);
        return isRemoved;
    }

    // Getters

    public StaticQuadTree<Integer> getRoot() {
        return root;
    }

    public Collection<T> getItems() {
        return items.values();
    }

    // drawYourSelf

    public void draw(PanAndZoom pz, Rect screen) {
        root.draw(pz, screen);
    }

}
