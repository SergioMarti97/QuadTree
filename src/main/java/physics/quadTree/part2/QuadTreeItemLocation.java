package physics.quadTree.part2;

import javafx.util.Pair;
import physics.quadTree.Rect;

import java.util.List;
import java.util.ListIterator;

public class QuadTreeItemLocation<T> {

    private Rect loc;

    private T item;

    public QuadTreeItemLocation(Rect loc, T item) {
        this.loc = loc;
        this.item = item;
    }

    public void set(Rect loc, T item) {
        this.loc = loc;
        this.item = item;
    }

    public Rect getLoc() {
        return loc;
    }

    public void setLoc(Rect loc) {
        this.loc = loc;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }
}
