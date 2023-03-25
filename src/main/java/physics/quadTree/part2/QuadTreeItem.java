package physics.quadTree.part2;

public class QuadTreeItem<T> {

    private T item;

    private QuadTreeItemLocation<T> location;

    public QuadTreeItem(T item, QuadTreeItemLocation<T> location) {
        this.item = item;
        this.location = location;
    }

    public QuadTreeItem(T item) {
        this.item = item;
    }

    public T getItem() {
        return item;
    }

    public QuadTreeItemLocation<T> getLocation() {
        return location;
    }

}
