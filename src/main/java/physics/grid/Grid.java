package physics.grid;

import base.vectors.points2d.Vec2df;
import base.vectors.points2d.Vec2di;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import panAndZoom.PanAndZoom;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Grid {

    private final Cell[] cells;

    private final int numRows;

    private final int numCols;

    // Num rows and cols no data

    public Grid(float posX, float posY, float width, float height, float cellW, float cellH) {
        this.numCols = (int)(width / cellW);
        this.numRows = (int)(height / cellH);
        Vec2df cellSize = new Vec2df(cellW, cellH);
        Vec2df inc = new Vec2df(posX, posY);
        int id = 0;
        cells = new Cell[numRows * numCols];
        for (int x = 0; x < numCols; x++) {
            for (int y = 0; y < numRows; y++) {
                setCell(x, y,
                        new Cell(
                                id,
                                new Vec2di(x, y),
                                new Vec2df(inc),
                                new Vec2df(cellSize)
                        )
                );
                inc.addToY(cellSize.getY());
                id++;
            }
            inc.setY(posY);
            inc.addToX(cellSize.getX());
        }
    }

    public Grid(float width, float height, float cellW, float cellH) {
        this(0, 0, width, height, cellW, cellH);
    }

    public Grid(Vec2df size, Vec2df cellSize) {
        this(size.getX(), size.getY(), cellSize.getX(), cellSize.getY());
    }

    public Grid(Vec2df ori, Vec2df size, Vec2df cellSize) {
        this(ori.getX(), ori.getY(), size.getX(), size.getY(), cellSize.getX(), cellSize.getY());
    }

    // Cell Width and height no data

    public Grid(float posX, float posY, float width, float height, int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;

        Vec2df cellSize = new Vec2df(
                height / this.numRows,
                width/ this.numCols
        );
        Vec2df inc = new Vec2df(posX, posY);
        int id = 0;

        cells = new Cell[this.numRows * this.numCols];
        for (int x = 0; x < this.numCols; x++) {
            for (int y = 0; y < this.numRows; y++) {
                setCell(x, y,
                        new Cell(
                            id,
                            new Vec2di(x, y),
                            new Vec2df(inc),
                            new Vec2df(cellSize)
                        )
                );
                inc.addToY(cellSize.getY());
                id++;
            }
            inc.setY(posY);
            inc.addToX(cellSize.getX());
        }
    }

    public Grid(float width, float height, int rows, int numCols) {
        this(0, 0, width, height, rows, numCols);
    }

    public Grid(Vec2df ori, Vec2df size, Vec2di rowAndCols) {
        this(ori.getX(), ori.getY(), size.getX(), size.getY(), rowAndCols.getX(), rowAndCols.getY());
    }

    public Grid(Vec2df size, Vec2di rowAndCols) {
        this(size.getX(), size.getY(), rowAndCols.getX(), rowAndCols.getY());
    }

    // Draw method

    public void draw(PanAndZoom pz) {
        List<Cell> cells = Arrays.stream(this.cells).sorted(Comparator.comparingInt(Cell::getZIndex)).collect(Collectors.toList());

        for (Cell c : cells) {
            if (c.isChecking()) {
                Paint strokeBefore = pz.getGc().getStroke();
                double lineThick = pz.getGc().getLineWidth();
                Paint fillBefore = pz.getGc().getFill();

                pz.getGc().setStroke(Color.YELLOW);
                pz.getGc().setLineWidth(2);
                pz.getGc().setFill(Color.YELLOW);

                c.draw(pz);

                pz.getGc().setStroke(strokeBefore);
                pz.getGc().setLineWidth(lineThick);
                pz.getGc().setFill(fillBefore);
            } else {
                c.draw(pz);
            }
        }
    }

    // Getters

    public synchronized Cell[] getCells() {
        return cells;
    }

    public Cell getCell(int i) {
        return cells[i];
    }

    public Cell getCell(int x, int y) {
        return cells[x + numCols * y];
    }

    public Cell getNeighbour(int positionX, int positionY, int i, int j) {
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

    public Cell getNeighbour(Vec2di pos, int addX, int addY) {
        return getNeighbour(pos.getX(), pos.getY(), addX, addY);
    }

    public void setCell(int x, int y, Cell c) {
        cells[x + numCols * y] = c; // todo ????
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}
