package generic;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.ball.Ball;
import physics.spaceDivision.Rect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SweepAndPruneBallGame extends AbstractBallGame {

    private List<Pair<Ball, Ball>> posibleCollidingPairs;

    private boolean drawSweepAndPrune = true;

    private boolean doIntervalsOverlap(float x1, float x2, float y1, float y2) {
        return Math.max(x1, y1) <= Math.min(x2, y2);
    }

    private boolean doIntervalsOverlap(Vec2df i1, Vec2df i2) {
        return doIntervalsOverlap(i1.getX(), i1.getY(), i2.getX(), i2.getY());
    }

    private void updateStaticCollisions() {
        // Sort balls
        balls.sort((b1, b2) -> Float.compare(b1.getPos().getX(), b2.getPos().getX()));

        // Sweep and Prune Algorithm
        Vec2df activeInterval = new Vec2df();
        List<Ball> activeBalls = new ArrayList<>();
        posibleCollidingPairs.clear();

        for (var b : balls) {
            // Calcular el intervalo de la pelota en X
            Vec2df interval = new Vec2df(b.getPos().getX(), b.getPos().getX() + b.getSize().getX());

            if (doIntervalsOverlap(activeInterval, interval)) {
                // Añadimos como posibles colisiones todas las pelotas en la lista de pelotas activas
                for (Ball activeBall : activeBalls) {
                    posibleCollidingPairs.add(new Pair<>(activeBall, b));
                }
                // Actualizamos el valor máximo del intervalo activo
                activeInterval.setY(interval.getY());
            } else {
                // Actualizamos el valor de inicio y fin del intervalo activo
                activeInterval.set(interval);
                // Limpiamos la lista de pelotas activas
                activeBalls.clear();
            }
            // Siempre se añade al final una nueva pelota
            activeBalls.add(b);
        }

        // Check Collisions
        for (var p : posibleCollidingPairs) {
            Ball b = p.getKey();
            Ball n = p.getValue();

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

                n.getPos().addToX(x);
                n.getPos().addToY(y);
            }
        }
    }

    @Override
    protected void addBall(Ball b) {
        balls.add(b);
    }

    @Override
    protected void updateBalls(float elapsedTime) {
        updateBallsPosition(elapsedTime);
        updateStaticCollisions();
        updateDynamicCollisions();
    }

    @Override
    protected void drawBall(Ball b) {
        Rect r = new Rect(b.getPos(), b.getSize());
        if (screen.contains(r)) {
            pz.getGc().setFill(b.getColor());
            pz.fillOval(b.getPos(), b.getSize());

            if (drawSweepAndPrune) {
                // Dibujar los intervalos
                Vec2df interval = new Vec2df(b.getPos().getX(), b.getPos().getX() + b.getSize().getX());
                float intervalHeight = 2.5f;
                pz.fillRect(
                        new Vec2df(interval.getX(),
                                arena.getPos().getY() + arena.getSize().getY()),
                        new Vec2df(interval.getY() - interval.getX(),
                                intervalHeight)
                );
            }

            numBallsDrawn++;
        }
    }

    @Override
    protected Collection<Ball> getBallsToDrawn() {
        return balls;
    }

    @Override
    public void initialize(GameApplication gc) {
        super.initialize(gc);
        posibleCollidingPairs = new ArrayList<>();
        initializeBalls(NUM_BALLS);
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        super.update(gc, elapsedTime);

        if (gc.getInput().isKeyDown(KeyCode.TAB)) {
            drawSweepAndPrune = !drawSweepAndPrune;
        }
    }

    @Override
    public void render(GameApplication gc) {
        super.render(gc);

        // Dibujar los posibles pares de pelotas en colisión
        if (drawSweepAndPrune) {
            for (var p : posibleCollidingPairs) {
                Ball b1 = p.getKey();
                Ball b2 = p.getValue();

                pz.getGc().setStroke(b1.getColor());
                pz.getGc().setStroke(b1.getColor());
                pz.strokeRect(b1.getPos(), b1.getSize());

                pz.getGc().setStroke(b2.getColor());
                pz.strokeRect(b2.getPos(), b2.getSize());
            }
        }

        gc.getGraphicsContext().setFill(Color.WHITE);
        Vec2df pos = new Vec2df(10, 30);
        gc.getGraphicsContext().fillText("Algorithm: Sweep And Prune", pos.getX(), pos.getY());
        pos.addToY(textLeading);
        drawTexts(gc.getGraphicsContext(), pos);
    }
}
