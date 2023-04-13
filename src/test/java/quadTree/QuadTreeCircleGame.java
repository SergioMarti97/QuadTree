package quadTree;

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
import physics.quadTree.Rect;
import physics.quadTree.QuadTreeContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuadTreeCircleGame extends AbstractGame {

    private final int NUM_BALLS = 5000;

    private final float MAX_SEARCH_AREA = 5000.0f;

    private final Vec2df BALL_SIZE = new Vec2df(20, 50);

    private final Vec2df BALL_VEL = new Vec2df(-1000, 1000);

    private QuadTreeContainer<Ball> treeBalls;

    private List<Ball> balls;

    private List<Pair<Ball, Ball>> collidingPairs;

    private Rect arena = new Rect(0, 0, 10000, 10000);

    private PanAndZoom pz;

    private Random rnd;

    private float searchSize = 500.0f;

    private Ball selectedBall = null;

    private Rect searchRect;

    private Vec2df mouse;

    private boolean updateBalls = true;

    private boolean isDrawTree = false;

    private boolean isBallSelected = false;

    // Funciones

    private float rndFloat(float a, float b) {
        return rnd.nextFloat() * (b - a) + a;
    }

    private float rndFloat(Vec2df v) {
        return rndFloat(v.getX(), v.getY());
    }

    @Override
    public void initialize(GameApplication gc) {
        rnd = new Random();
        treeBalls = new QuadTreeContainer<>(arena);
        balls = new ArrayList<>();
        collidingPairs = new ArrayList<>();

        pz = new PanAndZoom(gc.getGraphicsContext());
        pz.getWorldScale().set(0.04f, 0.04f);

        mouse = new Vec2df();
        searchRect = new Rect();

        for (int i = 0; i < NUM_BALLS; i++) {
            Ball b = new Ball();

            b.setId(i);
            b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
            float radius = rndFloat(BALL_SIZE);
            b.getSize().set(radius, radius);
            //b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
            b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

            treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
            //balls.add(b);
        }

    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        pz.handlePanAndZoom(gc, MouseButton.MIDDLE, 0.001f, true, true);

        collidingPairs.clear();

        if (gc.getInput().isKeyDown(KeyCode.TAB)) {
            isDrawTree = !isDrawTree;
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
        searchRect.setColor(Color.rgb(255, 255, 255, 0.3));

        if (gc.getInput().isButtonHeld(MouseButton.PRIMARY) || gc.getInput().isKeyDown(KeyCode.V)) { // gc.getInput().isButtonDown(MouseButton.PRIMARY)
            Ball b = new Ball();

            b.setId(balls.size());
            b.getPos().set(this.mouse.getX(),this.mouse.getY());
            float radius = rndFloat(BALL_SIZE);
            b.getSize().set(radius, radius);
            //b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
            b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

            treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
            //balls.add(b);
        }

        if (gc.getInput().isButtonDown(MouseButton.SECONDARY)) {
            if (selectedBall == null) {
                var toSelected = treeBalls.search(searchRect);
                for (var b : toSelected) {
                    b.calOri();
                    b.calRadius();
                    if (b.getOri().dist(this.mouse) < b.getSize().getX()) {
                        isBallSelected = true;
                        b.getVel().set(0, 0);
                        selectedBall = b;
                    }
                }
            }
        }

        if (gc.getInput().isButtonUp(MouseButton.SECONDARY)) {
            if (selectedBall != null) {
                float velX = 1f * (selectedBall.getPos().getX() - this.mouse.getX());
                float velY = 1f * (selectedBall.getPos().getY() - this.mouse.getY());
                selectedBall.getVel().set(velX, velY);
                isBallSelected = false;
                selectedBall = null;
            }
        }

        if (gc.getInput().isKeyHeld(KeyCode.DELETE)) {
            var toRemove = treeBalls.search(searchRect);
            for (var obj : toRemove) {
                treeBalls.remove(obj);
                //balls.remove(obj);
            }
        }

        if (gc.getInput().isKeyDown(KeyCode.SPACE)) {
            updateBalls = !updateBalls;
        }

        final float resistance = -10f;

        if (updateBalls) {

            for (var b : treeBalls.getValues()) {

                //boolean isRemoved = treeBalls.remove(b);
                boolean isRemoved =  treeBalls.getRoot().remove(b, new Rect(b.getPos(), b.getSize()));

                // Update velocity
                b.getVel().addToY(-resistance * elapsedTime);

                // Clamp slow velocity to 0
                /*if (b.getVel().mag2() <= 0.001) {
                    b.getVel().set(0, 0);
                }*/

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

                // Calculate the ball area where the ball is gone
                Rect ballArea = b.getSearchRect(1);
                var neighbours = treeBalls.search(ballArea);
                for (var n : neighbours) {
                    if (b.getId() != n.getId()) {
                        if (b.overlaps(n)) {
                            collidingPairs.add(new Pair<>(b, n));

                            b.calOri();
                            b.calRadius();
                            n.calOri();
                            n.calRadius();

                            float dist = b.dist(n);
                            //if(dist > 0.0001) {
                                float overlap = 0.5f * (dist - b.getRadius().getX() - n.getRadius().getX());

                                float x = overlap * (b.getOri().getX() - n.getOri().getX()) / dist;
                                float y = overlap * (b.getOri().getY() - n.getOri().getY()) / dist;

                                b.getPos().addToX(-x);
                                b.getPos().addToY(-y);
                                // b.calPos();

                                n.getPos().addToX(x);
                                n.getPos().addToY(y);
                                // n.calPos();
                            //}
                        }
                    }
                }

                //treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
                treeBalls.getRoot().insert(b, new Rect(b.getPos(), b.getSize()));
            }

            /*for (var p : collidingPairs) {

            }*/
        }
    }

    @Override
    public void render(GameApplication gc) {
        Rect screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.BLACK);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Dibujar las pelotas
        int numBallsDrawn = 0;
        float ballsElapsedTime;
        long t1, t2;

        t1 = System.nanoTime();
        for (var ball : treeBalls.search(screen)) {
            pz.getGc().setFill(ball.getColor());
            pz.fillOval(ball.getPos(), ball.getSize());
            if (isDrawTree) {
                Rect ballArea = ball.getSearchRect(1);
                pz.getGc().setLineWidth(10);
                pz.getGc().setStroke(Color.WHITE);
                pz.strokeRect(ballArea.getPos(), ballArea.getSize());
            }
            numBallsDrawn++;
        }
        t2 = System.nanoTime();
        ballsElapsedTime = (t2 - t1) / 1000000000f;

        // Dibujar el árbol
        if (isDrawTree) {
            pz.getGc().setLineWidth(10);
            pz.getGc().setStroke(Color.WHITE);
            treeBalls.draw(pz, screen);
        }

        // Dibujar todas las pelotas
        for (var ball : balls) {
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(ball.getColor());
            pz.strokeRect(ball.getPos(), ball.getSize());
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
        gc.getGraphicsContext().fillText(String.format("Pelotas dibujadas: %d", numBallsDrawn), 10, 30);
        gc.getGraphicsContext().fillText(String.format("Tiempo necesario: %.6f", ballsElapsedTime), 10, 50);
        gc.getGraphicsContext().fillText(String.format("Número de colisiones: %d", collidingPairs.size()), 10, 70);
    }
}
