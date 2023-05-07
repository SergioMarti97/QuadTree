package verlet;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomUtils;
import physics.spaceDivision.Rect;
import physics.verlet.VerletCircle2df;
import shapes.Circle2df;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class AbstractVerletGame extends AbstractGame {

    protected final int NUM_BALLS = 1;

    protected final int SEED = 33;

    protected final int NUM_SUB_STEPS = 10;

    protected final Vec2df CIRCLE_SIZE = new Vec2df(3, 5);

    protected List<VerletCircle2df> balls;

    protected List<Pair<VerletCircle2df, VerletCircle2df>> collidingPairs;

    protected final Circle2df arena = new Circle2df(0, 0, 200);

    protected Rect screen;

    protected PanAndZoom pz;

    protected Random rnd;

    protected VerletCircle2df selectedBall = null;

    protected Vec2df mouse;

    protected float textLeading = 17;

    protected int numCollisionsChecked = 0;

    protected int numBallsDrawn = 0;

    protected float updateTime = 0;

    protected float drawBallsTime = 0;

    protected boolean isUpdatingBalls = false;

    // Abstract methods

    protected abstract void addBall(VerletCircle2df b);

    protected abstract void updateBalls(float dt);

    protected abstract void drawBall(VerletCircle2df b);

    protected abstract Collection<VerletCircle2df> getBallsToDrawn();

    // Methods

    private float rndFloat(float a, float b) {
        return rnd.nextFloat() * (b - a) + a;
    }

    private float rndFloat(Vec2df v) {
        return rndFloat(v.getX(), v.getY());
    }

    protected VerletCircle2df rndBall() {
        VerletCircle2df b = new VerletCircle2df();

        b.setId(balls.size());
        // b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
        // b.setOldPos(b.getPos());
        b.setPos(arena.getPos());
        float radius = rndFloat(CIRCLE_SIZE);
        b.setRadius(radius);
        b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

        return b;
    }

    protected void initBalls() {
        for (int i = 0; i < NUM_BALLS; i++) {
            addBall(rndBall());
        }
    }

    protected void checkConstrains(VerletCircle2df b) {
        Vec2df toObj = new Vec2df(b.getPos().getX() - arena.getPos().getX(), b.getPos().getY() - arena.getPos().getY());
        float dist = toObj.mag();
        float offset = arena.getRadius() - b.getRadius();
        if (dist > offset) {
            toObj.division(dist);
            float x = arena.getPos().getX() + toObj.getX() * offset;
            float y = arena.getPos().getY() + toObj.getY() * offset;
            b.getPos().set(x, y);
        }
    }

    // Drawing methods

    protected void drawBalls(Collection<VerletCircle2df> balls) {
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

        gc.fillText(String.format("Pan: %s, Zoom: %s", pz.getWorldOffset(), pz.getWorldScale()), pos.getX(), pos.getY());
        pos.addToY(textLeading);

        return pos;
    }

    // Override methods

    @Override
    public void initialize(GameApplication gc) {
        rnd = new Random();
        rnd.setSeed(SEED);

        balls = new ArrayList<>();
        collidingPairs = new ArrayList<>();

        pz = new PanAndZoom(gc.getGraphicsContext());

        mouse = new Vec2df();
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        collidingPairs.clear();

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
                    if (b.getPos().dist(this.mouse) < b.getSize().getX()) {
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
                selectedBall = null;
            }
        }

        if (gc.getInput().isKeyDown(KeyCode.SPACE)) {
            isUpdatingBalls = !isUpdatingBalls;
        }

        if (isUpdatingBalls) {
            long t1, t2;
            t1 = System.nanoTime();

            float subElapsedTime = elapsedTime / NUM_SUB_STEPS;
            for (int i = 0; i < NUM_SUB_STEPS; i++) {
                numCollisionsChecked = 0;
                updateBalls(subElapsedTime);
            }

            t2 = System.nanoTime();
            updateTime = (t2 - t1) / 1000000000f;
        }
    }

    @Override
    public void render(GameApplication gc) {
        screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.GRAY);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Dibujar la arena
        pz.setFill(Color.BLACK);
        arena.drawYourself(pz);

        // Dibujar las pelotas
        long t1, t2;
        t1 = System.nanoTime();
        drawBalls(getBallsToDrawn());
        t2 = System.nanoTime();
        drawBallsTime = (t2 - t1) / 1000000000f;

        // Dibujar los textos
        gc.getGraphicsContext().setFill(Color.WHITE);
        drawTexts(gc.getGraphicsContext(), new Vec2df(10, 30));
    }

}
