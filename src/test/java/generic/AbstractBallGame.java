package generic;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomUtils;
import physics.ball.Ball;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class AbstractBallGame extends AbstractGame {

    protected final int NUM_BALLS = 10000; // 1000

    protected final Vec2df BALL_SIZE = new Vec2df(5, 10);

    protected final Vec2df BALL_VEL = new Vec2df(-30, 30);

    protected List<Ball> balls;

    protected List<Pair<Ball, Ball>> collidingPairs;

    protected final Rect arena = new Rect(0, 0, 2000, 2000); // 2000, 2000

    protected Rect screen;

    protected PanAndZoom pz;

    protected Random rnd;

    protected Ball selectedBall = null;

    protected Vec2df mouse;

    protected float textLeading = 30;

    protected int numCollisionsChecked = 0;

    protected int numBallsDrawn = 0;

    protected float updateTime = 0;

    protected float drawBallsTime = 0;

    protected boolean isUpdatingBalls = false;

    // Funciones Abstractas

    protected abstract void addBall(Ball b);

    protected abstract void updateBalls(float elapsedTime);

    protected abstract void drawBall(Ball b);

    protected abstract Collection<Ball> getBallsToDrawn();

    // Funciones

    private float rndFloat(float a, float b) {
        return rnd.nextFloat() * (b - a) + a;
    }

    private float rndFloat(Vec2df v) {
        return rndFloat(v.getX(), v.getY());
    }

    protected Ball rndBall() {
        Ball b = new Ball();

        b.setId(balls.size());
        b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
        float radius = rndFloat(BALL_SIZE);
        b.getSize().set(radius, radius);
        b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
        b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

        return b;
    }

    protected void initializeBalls(int numBalls) {
        for (int i = 0; i < numBalls; i++) {
            addBall(rndBall());
        }
    }

    protected void updateBallPosition(Ball b, float elapsedTime) {
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

    protected void updateBallsPosition(float elapsedTime) {
        for (var b : balls) {
            updateBallPosition(b, elapsedTime);
        }
    }

    protected void updateDynamicCollisions() {
        for (var p : collidingPairs) {
            Ball b1 = p.getKey();
            Ball b2 = p.getValue();

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
        }
    }

    protected void drawBalls(Collection<Ball> balls) {
        numBallsDrawn = 0;
        for (var b : balls) {
            drawBall(b);
        }
    }

    protected Vec2df drawTexts(GraphicsContext gc, Vec2df pos) {
        // Dibujar textos
        gc.fillText(String.format("Balls drawn: %d", numBallsDrawn), pos.getX(), pos.getY());
        pos.addToY(textLeading);

        gc.fillText(String.format("Time needed to draw: %.6fms", drawBallsTime * 1000), pos.getX(), pos.getY());
        pos.addToY(textLeading);

        gc.fillText(String.format("Time needed to update: %.6fms", updateTime * 1000), pos.getX(), pos.getY());
        pos.addToY(textLeading);

        gc.fillText(String.format("Number of checks: %d", numCollisionsChecked), pos.getX(), pos.getY());
        pos.addToY(textLeading);

        gc.fillText(String.format("Number of collisions: %d", collidingPairs.size()), pos.getX(), pos.getY());
        pos.addToY(textLeading);

        // gc.fillText(String.format("Pan: %s, Zoom: %s", pz.getWorldOffset(), pz.getWorldScale()), pos.getX(), pos.getY());
        // pos.addToY(leading);

        return pos;
    }

    @Override
    public void initialize(GameApplication gc) {
        rnd = new Random();
        rnd.setSeed(33);

        balls = new ArrayList<>();
        collidingPairs = new ArrayList<>();

        gc.getGraphicsContext().setFont(new Font(gc.getGraphicsContext().getFont().getName(), 24));

        pz = new PanAndZoom(gc.getGraphicsContext());
        // pz.getWorldScale().set(0.370f, 0.370f);
        // pz.getWorldOffset().set(-1825f, -80f);
        pz.getWorldScale().set(7.26f, 7.26f);
        pz.getWorldOffset().set(-93.23f, -5.74f);

        mouse = new Vec2df();
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        collidingPairs.clear();

        numCollisionsChecked = 0;

        pz.handlePanAndZoom(gc, MouseButton.MIDDLE, 0.001f, true, true);

        Vec2df mouse = new Vec2df((float)gc.getInput().getMouseX(), (float)gc.getInput().getMouseY());
        this.mouse.set(PanAndZoomUtils.screenToWorld(mouse, pz.getWorldOffset(), pz.getWorldScale()));

        if (gc.getInput().isButtonHeld(MouseButton.PRIMARY)) {
            var b = rndBall();
            b.getPos().set(this.mouse);
            addBall(b);
        }

        if (gc.getInput().isButtonDown(MouseButton.SECONDARY)) {
            if (selectedBall == null) {
                for (var b : balls) {
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
                final Vec2df multiplier = new Vec2df(1);
                float velX = multiplier.getX() * (selectedBall.getPos().getX() - this.mouse.getX());
                float velY = multiplier.getY() * (selectedBall.getPos().getY() - this.mouse.getY());
                selectedBall.getVel().set(velX, velY);
                selectedBall = null;
            }
        }

        if (gc.getInput().isKeyDown(KeyCode.SPACE)) {
            isUpdatingBalls = !isUpdatingBalls;
        }

        if (isUpdatingBalls) {
            long t1, t2;
            t1 = System.nanoTime();
            updateBalls(elapsedTime);
            t2 = System.nanoTime();
            updateTime = (t2 - t1) / 1000000000f;
        }
    }

    @Override
    public void render(GameApplication gc) {
        screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.BLACK);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Dibujar el marco de la arena
        pz.getGc().setStroke(Color.WHITE);
        pz.getGc().setLineWidth(1);
        pz.strokeRect(arena.getPos(), arena.getSize());

        // Dibujar las pelotas
        long t1, t2;
        t1 = System.nanoTime();
        drawBalls(getBallsToDrawn());
        t2 = System.nanoTime();
        drawBallsTime = (t2 - t1) / 1000000000f;

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

    }

}
