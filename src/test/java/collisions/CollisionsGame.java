package collisions;

import javafx.scene.text.Font;
import physics.ball.Ball;
import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import physics.grid.Cell;
import physics.grid.Grid;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import panAndZoom.PanAndZoom;
import physics.overlap.OverlapUtils;

import java.util.ArrayList;
import java.util.Random;

public class CollisionsGame extends AbstractGame {

    private PanAndZoom pz;

    private Grid grid;

    private ArrayList<Ball> balls;

    private float elapsedTimeRender = 0;

    private Font fontNormal;

    private Font fontSmall;

    /**
     * Esto hace pasadas
     */
    private Checker checker;

    @Override
    public void initialize(GameApplication gc) {
        pz = new PanAndZoom(gc.getGraphicsContext());
        fontNormal = gc.getGraphicsContext().getFont();
        fontSmall = new Font(fontNormal.getName(), fontNormal.getSize() * 0.5);

        final int NUM_BALLS = 1;
        Vec2df ballSize = new Vec2df(50, 50);
        Vec2df velConst = new Vec2df(-1, 1);

        grid = new Grid(gc.getWidth(), gc.getHeight(), ballSize.getX(), ballSize.getY());

        Random rnd = new Random();
        rnd.setSeed(69);

        balls = new ArrayList<>();
        for (int i = 0; i < NUM_BALLS; i++) {
            Ball b = new Ball(
                    i,
                    new Vec2df(rnd.nextFloat() * gc.getWidth(), rnd.nextFloat() * gc.getHeight()),
                    new Vec2df(
                            rnd.nextFloat() * (velConst.getY() - velConst.getX()) + velConst.getX(),
                            rnd.nextFloat() * (velConst.getY() - velConst.getX()) + velConst.getX()
                    ),
                    new Vec2df(ballSize),
                    Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255), 0.2)
            );
            balls.add(b);
            Cell c = whereIsBall(b);
            if (c != null) {
                c.getContent().add(b);
                b.setCell(c);
            }
        }

        checker = new Checker(this.grid, 0);
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        pz.handlePanAndZoom(gc, MouseButton.MIDDLE, 0.001f, true, true);

        checker.setDt(elapsedTime);
        new Thread(checker).start();

        /*if (gc.getInput().isKeyDown(KeyCode.SPACE)) {
            System.out.println("Lanzando el trabajo");
            if (!checker.isChecking()) {

            } else {
                System.out.println("¡Ya se esta realizando una comprobación!");
            }
        }*/

    }

    @Override
    public void render(GameApplication gc) {
        long t1 = System.nanoTime();
        pz.getGc().setFont(fontSmall);

        // background
        gc.getGraphicsContext().setFill(Color.WHITE);
        gc.getGraphicsContext().fillRect(0, 0, gc.getWidth(), gc.getWidth());

        // physics.grid
        gc.getGraphicsContext().setFill(Color.BLACK);
        gc.getGraphicsContext().setStroke(Color.BLACK);
        gc.getGraphicsContext().setLineWidth(1);
        // grid.draw(pz);
        this.checker.getGrid().draw(pz);

        // balls
        gc.getGraphicsContext().setFill(Color.RED);
        for (Ball b : balls) {
            b.draw(pz, checker.getDt());
        }

        // Texts
        pz.getGc().setFont(fontNormal);
        gc.getGraphicsContext().setFill(Color.RED);
        gc.getGraphicsContext().fillText(String.format("Elapsed render time: %.3f ms", elapsedTimeRender), 10, 10);
        gc.getGraphicsContext().fillText(String.format("Elapsed check time: %.3f ms", checker.getElapsedTimeCheck()), 10, 40);

        long t2 = System.nanoTime();
        elapsedTimeRender = (t2 - t1) / 1000000f;
    }

    private Cell whereIsBall(Ball b) {
        for (Cell c : grid.getCells()) {
            if (isBallInCell(c, b)) {
                return c;
            }
        }
        return null;
    }

    private boolean isBallInCell(Cell c, Ball b) {
        return OverlapUtils.isBallInCell(b, c);
    }

}
