package test.measure;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import panAndZoom.PanAndZoom;
import physics.ball.Ball;
import physics.spaceDivision.Rect;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class MeasureTimeGame extends AbstractGame {

    private List<Pair<Integer, Vec2df>> numBalls = new ArrayList<>();

    private List<Float> meanElapsedTime = new ArrayList<>();

    private float meanDt = 0;

    private final Vec2df BALL_SIZE = new Vec2df(5, 10);

    private final Vec2df BALL_VEL = new Vec2df(-50, 50);

    private List<Ball> balls;

    private List<Pair<Ball, Ball>> collidingPairs;

    private List<Pair<Ball, Ball>> posibleCollidingPairs;

    private Rect arena = new Rect(0, 0, 50, 50);

    private PanAndZoom pz;

    private Random rnd;

    private float updateTime = 0;

    private final int NUM_FRAMES = 2500;

    private int numFrames = 0;

    private int count = 0;

    // Funciones

    private float rndFloat(float a, float b) {
        return rnd.nextFloat() * (b - a) + a;
    }

    private float rndFloat(Vec2df v) {
        return rndFloat(v.getX(), v.getY());
    }

    private boolean doIntervalsOverlap(float x1, float x2, float y1, float y2) {
        return Math.max(x1, y1) <= Math.min(x2, y2);
    }

    private boolean doIntervalsOverlap(Vec2df i1, Vec2df i2) {
        return doIntervalsOverlap(i1.getX(), i1.getY(), i2.getX(), i2.getY());
    }

    private void initializeBalls() {
        float numBalls = this.numBalls.get(count).getKey();
        arena.getSize().set(this.numBalls.get(count).getValue());
        initializeBalls((int) numBalls);
    }

    private void initializeBalls(int numBalls) {
        balls.clear();
        for (int i = 0; i < numBalls; i++) {
            Ball b = new Ball();

            b.setId(i);
            b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
            float radius = rndFloat(BALL_SIZE);
            b.getSize().set(radius, radius);
            b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
            b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

            balls.add(b);
        }
    }

    @Override
    public void initialize(GameApplication gc) {
        rnd = new Random();
        rnd.setSeed(69);

        balls = new ArrayList<>();
        collidingPairs = new ArrayList<>();
        posibleCollidingPairs = new ArrayList<>();

        pz = new PanAndZoom(gc.getGraphicsContext());

        numBalls.add(new Pair<>(2, new Vec2df(50, 50)));
        numBalls.add(new Pair<>(5, new Vec2df(75, 75)));
        numBalls.add(new Pair<>(10, new Vec2df(100, 100)));
        numBalls.add(new Pair<>(20, new Vec2df(120, 120)));
        numBalls.add(new Pair<>(50, new Vec2df(200, 200)));
        numBalls.add(new Pair<>(100, new Vec2df(250, 250)));
        numBalls.add(new Pair<>(200, new Vec2df(300, 300)));
        numBalls.add(new Pair<>(500, new Vec2df(500, 500)));
        numBalls.add(new Pair<>(1000, new Vec2df(1000, 1000)));
        numBalls.add(new Pair<>(2000, new Vec2df(2000, 2000)));
        numBalls.add(new Pair<>(100, new Vec2df(250, 250)));

        initializeBalls();
    }

    private void updateBalls(float elapsedTime) {
        for (var b : balls) {
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
    }

    private void sweepAndPrune() {
        // Calcule intervals
        balls.sort((b1, b2) -> Float.compare(b1.getPos().getX(), b2.getPos().getX()));

        // Sweep and Prune Algorithm
        Vec2df activeInterval = new Vec2df();
        List<Ball> activeBalls = new ArrayList<>();

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

        // Check collisions
        for (var p : posibleCollidingPairs) {
            Ball b = p.getKey();
            Ball n = p.getValue();

            b.calOri();
            b.calRadius();
            n.calOri();
            n.calRadius();

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

    private void allVsAll() {
        for (Ball b : balls) {
            for (Ball n : balls) {
                if (b.getId() != n.getId()) {
                    b.calOri();
                    b.calRadius();
                    n.calOri();
                    n.calRadius();

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
        }
    }

    private void updateDynamicCollisions() {
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

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        collidingPairs.clear();
        posibleCollidingPairs.clear();

        pz.handlePanAndZoom(gc, MouseButton.MIDDLE, 0.001f, true, true);

        long t1, t2;

        t1 = System.nanoTime();

        // Update balls position
        updateBalls(elapsedTime);

        // Resolve static collisions
        // allVsAll();
        sweepAndPrune();

        // Resolve dynamic collisions
        updateDynamicCollisions();

        t2 = System.nanoTime();
        updateTime = (t2 - t1) / 1000000000f;
        meanDt += updateTime;

        if (numFrames >= NUM_FRAMES) {
            if (count < numBalls.size()) {
                count++;
                meanElapsedTime.add(meanDt / numFrames);
                numFrames = 0;
                initializeBalls();
            } else {
                try {
                    File f = new File("C:\\Users\\Sergio\\IdeaProjects\\JAVAFX\\javafx-predator-prey-simulation\\src\\test\\java\\quadTree\\measure\\output.csv");
                    try (PrintWriter pw = new PrintWriter(f)) {
                        pw.println("numBalls;ElapsedTime");
                        for (int i = 0; i < meanElapsedTime.size(); i++) {
                            Integer numBalls = this.numBalls.get(i).getKey();
                            Float dt = meanElapsedTime.get(i);
                            pw.println(String.format("%d;%f", numBalls, dt));
                        }
                        System.out.println("Data output writted!!");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        float ballsElapsedTime;
        long t1, t2;

        t1 = System.nanoTime();
        for (var b : balls) {
            Rect r = new Rect(b.getPos(), b.getSize());
            if (screen.contains(r)) {
                pz.getGc().setFill(b.getColor());
                pz.fillOval(b.getPos(), b.getSize());
                numBallsDrawn++;
            }
        }
        t2 = System.nanoTime();
        ballsElapsedTime = (t2 - t1) / 1000000000f;

        // Dibujar textos
        gc.getGraphicsContext().setFill(Color.WHITE);
        gc.getGraphicsContext().fillText(String.format("Pelotas dibujadas: %d", numBallsDrawn), 10, 30);
        gc.getGraphicsContext().fillText(String.format("Tiempo necesario para dibujar las pelotas: %.6f milisegundos", ballsElapsedTime * 1000), 10, 50);
        gc.getGraphicsContext().fillText(String.format("Tiempo necesario para actualizar las pelotas: %.6f milisegundos", updateTime * 1000), 10, 70);
        gc.getGraphicsContext().fillText(String.format("Frames calculados: %d", numFrames), 10, 90);

        numFrames++;
    }
}
