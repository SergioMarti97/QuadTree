package test.grid;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomUtils;
import physics.ball.Ball;
import physics.spaceDivision.grid.Grid;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridCircleGame extends AbstractGame {

    private final int NUM_BALLS = 1000; // 12000

    private final int NUM_COLS_AND_ROWS = 30;

    private final float MAX_SEARCH_AREA = 1000f;

    private final Vec2df BALL_SIZE = new Vec2df(5, 10);

    private final Vec2df BALL_VEL = new Vec2df(-30, 30);

    private Grid<Ball> ballsGrid;

    private List<Ball> balls;

    private List<Pair<Ball, Ball>> collidingPairs;

    private Rect arena = new Rect(0, 0, 2000, 2000);

    private PanAndZoom pz;

    private Random rnd;

    private Vec2df mouse;

    private Rect searchRect;

    private float searchSize = 5.0f;

    private Ball selectedBall = null;

    private float updateTime = 0;

    private boolean updateBalls = false;

    private boolean isDrawGrid = false;

    private int numCollisionsChecked = 0;

    private float rndFloat(float a, float b) {
        return rnd.nextFloat() * (b - a) + a;
    }

    private float rndFloat(Vec2df v) {
        return rndFloat(v.getX(), v.getY());
    }

    @Override
    public void initialize(GameApplication gc) {
        rnd = new Random();
        rnd.setSeed(123);

        balls = new ArrayList<>();
        collidingPairs = new ArrayList<>();

        ballsGrid = new Grid<>(arena, NUM_COLS_AND_ROWS, NUM_COLS_AND_ROWS);

        pz = new PanAndZoom(gc.getGraphicsContext());
        pz.getWorldScale().set(0.370f, 0.370f);
        pz.getWorldOffset().set(-1825f, -80f);

        mouse = new Vec2df();
        searchRect = new Rect();
        searchRect.setColor(Color.rgb(255, 255, 255, 0.3));

        for (int i = 0; i < NUM_BALLS; i++) {
            Ball b = new Ball();

            b.setId(i);
            b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
            b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
            float radius = rndFloat(BALL_SIZE);
            b.getSize().set(radius, radius);
            b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

            balls.add(b);
            ballsGrid.insert(b, new Rect(b.getPos(), b.getSize()));
        }
    }

    private void updateBallPosition(Ball b, float elapsedTime) {
        // Update position
        b.getPos().addToX(b.getVel().getX() * elapsedTime);
        b.getPos().addToY(b.getVel().getY() * elapsedTime);

        // Check area collisions
        if (b.getPos().getX() < arena.getPos().getX()) {
            b.getPos().setX(arena.getPos().getX());
            b.getVel().multiplyXBy(-1);
        }
        if (b.getPos().getX() + b.getSize().getX() >= arena.getSize().getX()) {
            b.getPos().setX(arena.getSize().getX() - b.getSize().getX());
            b.getVel().multiplyXBy(-1);
        }

        if (b.getPos().getY() < arena.getPos().getY()) {
            b.getPos().setY(arena.getPos().getY());
            b.getVel().multiplyYBy(-1);
        }
        if (b.getPos().getY() + b.getSize().getY() >= arena.getSize().getY()) {
            b.getPos().setY(arena.getSize().getY() - b.getSize().getY());
            b.getVel().multiplyYBy(-1);
        }
    }

    private void updateStaticCollisions(Ball b, float elapsedTime) {
        // Calculate the ball area where the ball is gone
        Rect ballArea = b.getSearchRect(1);

        // For each neighbour
        var neighbours = ballsGrid.searchFast(ballArea);
        for (var n : neighbours) {
            if (n.getId() != b.getId()) {
                b.calOri();
                b.calRadius();
                n.calOri();
                n.calRadius();

                numCollisionsChecked++;

                if (b.doCirclesOverlap(n)) {
                    collidingPairs.add(new Pair<>(b, n));

                    float dist = b.dist(n);

                    float overlap = 0.5f * (dist - b.getRadius() - n.getRadius());

                    if (dist == 0) {
                        dist = 1;
                    }

                    float x = overlap * (b.getOri().getX() - n.getOri().getX()) / dist;
                    float y = overlap * (b.getOri().getY() - n.getOri().getY()) / dist;

                    b.getPos().addToX(-x);
                    b.getPos().addToY(-y);

                    Rect areaNeighbour = new Rect(n.getPos(), n.getSize());
                    ballsGrid.remove(n, areaNeighbour);
                    n.getPos().addToX(x);
                    n.getPos().addToY(y);
                    ballsGrid.insert(n, new Rect(n.getPos(), n.getSize()));
                }
            }
        }
    }

    private void updateDynamicCollisions() {
        for (var p : collidingPairs) {
            Ball b1 = p.getKey();
            Ball b2 = p.getValue();

            Rect area1 = new Rect(b1.getPos(), b1.getSize());
            // treeBalls.remove(b1, area1);
            Rect area2 = new Rect(b2.getPos(), b2.getSize());
            // treeBalls.remove(b2, area2);

            b1.calOri();
            b1.calRadius();
            b2.calOri();
            b2.calRadius();

            // Distance between balls
            float dist = b1.dist(b2);

            if (dist == 0) {
                dist = 1;
            }

            // Normal
            Vec2df n = new Vec2df(b2.getOri());
            n.sub(b1.getOri()).division(dist);

            // Tangent
            Vec2df t = (Vec2df) n.perpendicular();

            // Dot Product Tangent
            float dpTan1 = b1.getVel().dotProduct(t);
            float dpTan2 = b2.getVel().dotProduct(t);

            // Dot Product Normal
            float dpNorm1 = b1.getVel().dotProduct(n);
            float dpNorm2 = b2.getVel().dotProduct(n);

            float b1Mass = b1.getRadius();
            float b2Mass = b2.getRadius();

            // Conservation of momentum in 1D
            float m1 = (dpNorm1 * (b1Mass - b2Mass) + 2.0f * b2Mass * dpNorm2) / (b1Mass + b2Mass);
            float m2 = (dpNorm2 * (b2Mass - b1Mass) + 2.0f * b1Mass * dpNorm1) / (b1Mass + b2Mass);

            // Update ball b.getVelocities
            b1.getVel().setX(t.getX() * dpTan1 + n.getX() * m1);
            b1.getVel().setY(t.getY() * dpTan1 + n.getY() * m1);

            b2.getVel().setX(t.getX() * dpTan2 + n.getX() * m2);
            b2.getVel().setY(t.getY() * dpTan2 + n.getY() * m2);

            // treeBalls.insert(b1, new Rect(b1.getPos(), b1.getSize()));
            // treeBalls.insert(b2, new Rect(b2.getPos(), b2.getSize()));
        }
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        numCollisionsChecked = 0;
        collidingPairs.clear();

        pz.handlePanAndZoom(gc, MouseButton.MIDDLE, 0.001f, true, true);

        if (gc.getInput().isKeyDown(KeyCode.TAB)) {
            isDrawGrid = !isDrawGrid;
        }

        float inc = 10.0f;
        if (gc.getInput().isKeyHeld(KeyCode.Q)) {
            searchSize += inc;
        }
        if (gc.getInput().isKeyHeld(KeyCode.A)) {
            searchSize -= inc;
        }
        searchSize = Math.max(10f, Math.min(MAX_SEARCH_AREA, searchSize));

        Vec2df mouse = new Vec2df((float)gc.getInput().getMouseX(), (float)gc.getInput().getMouseY());
        this.mouse.set(PanAndZoomUtils.screenToWorld(mouse, pz.getWorldOffset(), pz.getWorldScale()));

        Vec2df searchArea = new Vec2df(this.searchSize);
        Vec2df rectOrigin = new Vec2df(this.mouse);
        searchArea.multiply(-0.5f);
        rectOrigin.add(searchArea);
        searchArea.multiply(-2);
        searchRect.set(rectOrigin, searchArea);

        if (gc.getInput().isButtonHeld(MouseButton.PRIMARY)) {
            Ball b = new Ball();

            b.setId(balls.size());
            b.getPos().set(this.mouse);
            b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
            float radius = rndFloat(BALL_SIZE);
            b.getSize().set(radius, radius);
            b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

            ballsGrid.insert(b, new Rect(b.getPos(), b.getSize()));
            balls.add(b);
        }

        if (gc.getInput().isButtonDown(MouseButton.SECONDARY)) {
            if (selectedBall == null) {
                var toSelected = ballsGrid.search(searchRect);
                for (var b : toSelected) {
                    b.calOri();
                    b.calRadius();
                    if (b.getOri().dist(this.mouse) < b.getSize().getX()) {
                        b.getVel().set(0, 0);
                        selectedBall = b;
                    }
                }
            }
        }

        if (gc.getInput().isButtonUp(MouseButton.SECONDARY)) {
            if (selectedBall != null) {
                float velX = (selectedBall.getPos().getX() - this.mouse.getX());
                float velY = (selectedBall.getPos().getY() - this.mouse.getY());
                selectedBall.getVel().set(velX, velY);
                selectedBall = null;
            }
        }

        if (gc.getInput().isKeyHeld(KeyCode.DELETE)) {
            var toRemove = ballsGrid.search(searchRect);
            for (var obj : toRemove) {
                ballsGrid.remove(obj, new Rect(obj.getPos(), obj.getSize()));
                balls.remove(obj);
            }
        }

        if (gc.getInput().isKeyDown(KeyCode.SPACE)) {
            updateBalls = !updateBalls;
        }

        if (updateBalls) {
            long t1, t2;
            t1 = System.nanoTime();
            for (var b : balls) {
                Rect ballArea = new Rect(b.getPos(), b.getSize());
                ballsGrid.remove(b, ballArea);

                updateBallPosition(b, elapsedTime);
                updateStaticCollisions(b, elapsedTime);

                ballsGrid.insert(b, new Rect(b.getPos(), b.getSize()));
            }
            updateDynamicCollisions();

            t2 = System.nanoTime();
            updateTime = (t2 - t1) / 1000000000f;
        }
    }

    @Override
    public void render(GameApplication gc) {
        Rect screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.BLACK);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Dibujar el marco de la arena
        pz.getGc().setStroke(Color.WHITE);
        pz.getGc().setLineWidth(1);
        pz.strokeRect(arena.getPos(), arena.getSize());

        // Dibujar las pelotas
        int numBallsDrawn = 0;
        float drawBallsElapsedTime;
        long t1, t2;

        t1 = System.nanoTime();
        for (var b : ballsGrid.search(screen)) {
            pz.getGc().setFill(b.getColor());
            pz.fillOval(b.getPos(), b.getSize());

            numBallsDrawn++;
        }
        t2 = System.nanoTime();
        drawBallsElapsedTime = (t2 - t1) / 1000000000f;

        // Dibujar la regilla
        if (isDrawGrid) {
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(Color.WHITE);
            pz.getGc().setFill(Color.WHITE);
            ballsGrid.draw(pz, screen);
        }

        // Dibujar las colisiones
        for (var p : collidingPairs) {
            Ball b1 = p.getKey();
            Ball b2 = p.getValue();
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(Color.RED);
            b1.calOri();
            b2.calOri();
            pz.strokeLine(b1.getOri(), b2.getOri());
        }

        // Dibujar la línea pelota-ratón de la pelota seleccionada
        if (selectedBall != null) {
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(Color.WHITE);
            selectedBall.calOri();
            pz.strokeLine(selectedBall.getOri(), mouse);
        }

        // Dibujar el área de búsqueda
        pz.getGc().setFill(searchRect.getColor());
        pz.fillRect(searchRect.getPos(), searchRect.getSize());

        // Dibujar textos
        gc.getGraphicsContext().setFill(Color.WHITE);
        gc.getGraphicsContext().fillText(String.format("Algorithm Grid"), 10, 30);
        gc.getGraphicsContext().fillText(String.format("Balls drawn: %d", numBallsDrawn), 10, 50);
        gc.getGraphicsContext().fillText(String.format("Time needed to draw: %.6fms", drawBallsElapsedTime * 1000), 10, 70);
        gc.getGraphicsContext().fillText(String.format("Time needed to update: %.6fms", updateTime * 1000), 10, 90);
        gc.getGraphicsContext().fillText(String.format("Number of checks: %d", numCollisionsChecked), 10, 110);
        gc.getGraphicsContext().fillText(String.format("Number of collisions: %d", collidingPairs.size()), 10, 130);
    }
}
