package collisions;

import physics.ball.Ball;
import physics.grid.Cell;
import physics.grid.Grid;
import physics.overlap.OverlapUtils;

import java.util.ArrayList;

public class Checker implements Runnable {

    private Grid grid;

    private boolean isChecking;

    private boolean isSlow;

    private int millisSleep = 1;

    private float dt;

    private float elapsedTimeCheck = 0;

    public Checker(Grid grid, float dt) {
        this.grid = grid;
        this.isChecking = false;
        this.isSlow = false;
        this.dt = dt;
    }

    private boolean isBallInCell(Cell c, Ball b) {
        return OverlapUtils.isBallInCell(b, c);
    }

    @Override
    public void run() {
        long t1 = System.nanoTime();
        isChecking = true;
        for (Cell c : grid.getCells()) {

            ArrayList<Ball> toRemove = new ArrayList<>();

            c.setChecking(true);
            c.setZIndex(1);

            for (Ball b : c.getContent()) {
                b.getPos().addToX(b.getVel().getX() * dt);
                b.getPos().addToY(b.getVel().getY() * dt);

                if (!isBallInCell(c, b)) {
                    System.out.println("  ¡La pelota " + c.getId() + " ha cambiado del celda!");
                    b.setCell(null);
                    toRemove.add(b);
                    Cell n;

                    // 8 Posible outcomes
                    // Left
                    n = grid.getNeighbour(c.getPos(), - 1, 0);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                    // Right
                    n = grid.getNeighbour(c.getPos(), + 1, 0);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                    // Top
                    n = grid.getNeighbour(c.getPos(), 0, - 1);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                    // Bottom
                    n = grid.getNeighbour(c.getPos(), 0, + 1);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                    // Top Left
                    n = grid.getNeighbour(c.getPos(), - 1, - 1);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                    // Top Right
                    n = grid.getNeighbour(c.getPos(), + 1, - 1);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                    // Bottom Left
                    n = grid.getNeighbour(c.getPos(), - 1, 1);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                    // Bottom Right
                    n = grid.getNeighbour(c.getPos(), + 1, 1);
                    if (n != null) {
                        if (isBallInCell(n, b)) {
                            b.setCell(n);
                            n.getContent().add(b);
                        }
                    }

                }
            }
            c.getContent().removeAll(toRemove);
            toRemove.clear();

            // Ralentizamos un poco la cosa para ver el efecto
            if (isSlow) {
                try {
                    Thread.sleep(millisSleep);
                } catch (InterruptedException e) {
                    System.out.println("Fallo en el hilo de ejecución: " + e.getMessage());
                }
            }

            c.setChecking(false);
            c.setZIndex(0);

        }
        // System.out.println("Trabajo terminado");
        isChecking = false;
        long t2 = System.nanoTime();
        elapsedTimeCheck = (t2 - t1) / 1000000f;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public boolean isChecking() {
        return isChecking;
    }

    public void setChecking(boolean checking) {
        isChecking = checking;
    }

    public float getDt() {
        return dt;
    }

    public void setDt(float dt) {
        this.dt = dt;
    }

    public boolean isSlow() {
        return isSlow;
    }

    public void setSlow(boolean slow) {
        isSlow = slow;
    }

    public int getMillisSleep() {
        return millisSleep;
    }

    public void setMillisSleep(int millisSleep) {
        this.millisSleep = millisSleep;
    }

    public float getElapsedTimeCheck() {
        return elapsedTimeCheck;
    }
}
