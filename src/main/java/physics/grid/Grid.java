package physics.grid;

import base.vectors.points2d.Vec2df;
import base.vectors.points2d.Vec2di;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.quadTree.Rect;

import java.util.HashSet;
import java.util.Set;

public class Grid<T> {

    private Cell[] cells;

    private final int numRows;

    private final int numCols;

    public Grid(float posX, float posY, float width, float height, int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        resize(posX, posY, width, height);
    }

    public Grid(Rect area, int numRows, int numCols) {
        this(area.getPos().getX(), area.getPos().getY(), area.getSize().getX(), area.getSize().getY(), numRows, numCols);
    }

    // Mehtods

    public void resize(float posX, float posY, float width, float height) {
        Vec2df cellSize = new Vec2df(
                height / this.numRows,
                width / this.numCols
        );
        Vec2df inc = new Vec2df(posX, posY);
        int id = 0;

        cells = new Cell[this.numRows * this.numCols];
        for (int x = 0; x < this.numCols; x++) {
            for (int y = 0; y < this.numRows; y++) {
                Rect cellArea = new Rect(new Vec2df(inc), new Vec2df(cellSize));
                setCell(x, y, new Cell(id, new Vec2di(x, y), cellArea));
                inc.addToY(cellSize.getY());
                id++;
            }
            inc.setY(posY);
            inc.addToX(cellSize.getX());
        }
    }

    public void clear() {
        for (var c : cells) {
            c.clear();
        }
    }

    public int size() {
        int count = 0;
        for (var c : cells) {
            count += c.size();
        }
        return count;
    }

    public void insert(T item, Rect itemSize) {
        for (var c : cells) {
            if (c.getRect().overlaps(itemSize)) {
                c.getItems().add(new Pair<>(itemSize, item));
                return;
            }
        }
    }

    public Set<T> search(Rect area) {
        Set<T> setItems = new HashSet<>();
        for (Cell<T> c : cells) {
            if (!c.isEmpty()) {
                if (area.contains(c.getRect())) {
                    c.items(setItems);
                } else {
                    for (Pair<Rect, T> p : c.getItems()) {
                        if (area.overlaps(p.getKey())) {
                            setItems.add(p.getValue());
                        }
                    }
                }
            }
        }
        return setItems;
    }

    public Set<T> searchFast(Rect area) {
        Set<T> set = new HashSet<>();

        Cell<T> cell = null;
        for (Cell<T> c : cells) {
            if (!c.isEmpty()) {
                if (area.overlaps(c.getRect())) {
                    cell = c;
                    break;
                }
            }
        }

        if (cell != null) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    Cell<T> n = getNeighbour(cell.getPos(), i, j);
                    if (n != null) {
                        if (!n.isEmpty()) {
                            if (area.contains(n.getRect())) {
                                n.items(set);
                            } else {
                                for (Pair<Rect, T> p : n.getItems()) {
                                    if (area.overlaps(p.getKey())) {
                                        set.add(p.getValue());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return set;
    }

    public boolean remove(T itemToRemove, Rect loc) {
        for (Cell<T> c : cells) {
            if (c.getRect().overlaps(loc)) {
                return c.getItems().removeIf(p -> p.getValue().equals(itemToRemove));
            }
        }
        return false;
    }

    // Draw yourself method

    public void draw(PanAndZoom pz, Rect screen) {
        for (var c : cells) {
            if (screen.contains(c.getRect())) {
                if (!c.isEmpty()) {
                    c.draw(pz);
                }
            }
        }
    }

    // Getters and Setters

    public Cell<T>[] getCells() {
        return cells;
    }

    public Cell<T> getCell(int id) {
        return cells[id];
    }

    public Cell<T> getCell(int x, int y) {
        return cells[x + numCols * y];
    }

    public void setCell(int x, int y, Cell<T> c) {
        cells[x + numCols * y] = c; // todo ????
    }

    public Cell<T> getNeighbour(int positionX, int positionY, int i, int j) {
        int finalX = positionX + i;
        int finalY = positionY + j;
        if ( finalX < numCols && finalY < numRows && finalX >= 0 && finalY >= 0 ) {
            try {
                return getCell(finalX, finalY);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.printf("ArrayIndexOutOfBounds %dx %dy for %d cols %d rows\n", finalX, finalY, numCols, numRows);
            }

        }
        return null;
    }

    public Cell<T> getNeighbour(Vec2di pos, int addX, int addY) {
        return getNeighbour(pos.getX(), pos.getY(), addX, addY);
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

}
