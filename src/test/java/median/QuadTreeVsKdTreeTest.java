package median;

import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.ball.Ball;
import physics.spaceDivision.Rect;
import physics.spaceDivision.quadTree.QuadTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuadTreeVsKdTreeTest {

    public static final int NUM_BALLS = 2000;

    public static final Vec2df BALL_SIZE = new Vec2df(5, 10);

    public static final Vec2df BALL_VEL = new Vec2df(-30, 30);

    public static Random rnd;

    public static List<Ball> balls;

    public static QuadTree<Ball> treeBalls;

    public static Rect arena = new Rect(new Vec2df(), new Vec2df(1000, 1000));

    public static int numCollisionsChecked = 0;

    public static List<Pair<Ball, Ball>> collidingPairs = new ArrayList<>();

    // Methods

    public static float rndFloat(float a, float b) {
        return rnd.nextFloat() * (b - a) + a;
    }

    public static float rndFloat(Vec2df v) {
        return rndFloat(v.getX(), v.getY());
    }

    public static Ball rndBall() {
        Ball b = new Ball();

        b.setId(balls.size());
        b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
        float radius = rndFloat(BALL_SIZE);
        b.getSize().set(radius, radius);
        b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
        b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

        return b;
    }

    public static void addBall(Ball b) {
        balls.add(b);
        // treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
    }

    public static void initializeBalls() {
        balls = new ArrayList<>();
        for (int i = 0; i < NUM_BALLS; i++) {
            addBall(rndBall());
        }
    }

    // Update balls position

    public static void updateBallPosition(Ball b, float elapsedTime) {
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

    public static void updateStaticCollisions(Ball b, float elapsedTime) {
        // Calculate the ball area where the ball is gone
        Rect ballArea = b.getSearchRect(1);

        // For each neighbour
        var neighbours = treeBalls.search(ballArea);
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
                    treeBalls.remove(n, areaNeighbour);
                    n.getPos().addToX(x);
                    n.getPos().addToY(y);
                    treeBalls.insert(n, new Rect(n.getPos(), n.getSize()));
                }
            }
        }
    }

    public static void updateDynamicCollisions() {
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

    public static void calCollisions(float elapsedTime) {
        for (var b : balls) {
            Rect ballArea = new Rect(b.getPos(), b.getSize());
            treeBalls.remove(b, ballArea);
            updateBallPosition(b, elapsedTime);
            treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
        }

        for (var b : balls) {
            Rect ballArea = new Rect(b.getPos(), b.getSize());
            treeBalls.remove(b, ballArea);
            updateStaticCollisions(b, elapsedTime);
            treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
        }

        updateDynamicCollisions();
    }

    public static void simulation() {
        long t1, t2;
        float elapsedTime;

        // Inicializar el quadtree
        t1 = System.nanoTime();
        treeBalls = new QuadTree<>(arena);
        for (var b : balls) {
            treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
        }
        t2 = System.nanoTime();
        elapsedTime = (t2 - t1) / 1000000000f;
        System.out.printf("Tiempo consumido para construir el QuadTree: %.6f\n", elapsedTime);

        // Actualizar las pelotas
        t1 = System.nanoTime();
        calCollisions(0.1f);
        t2 = System.nanoTime();
        elapsedTime = (t2 - t1) / 1000000000f;
        System.out.printf("Tiempo consumido para actualizar las pelotas: %.6f\n", elapsedTime);
        System.out.println("Número de colisiones comprobadas: " + numCollisionsChecked);
        System.out.println("Número de colisiones: " + collidingPairs.size());
    }

    public static float repeatSimulation(int repeats) {
        long t1, t2;
        float elapsedTime = 0;
        for (int i = 0; i < repeats; i++) {
            System.out.println("Simulation number: " + i);
            t1 = System.nanoTime();
            simulation();
            t2 = System.nanoTime();
            elapsedTime += (t2 - t1) / 1000000000f;
        }
        return elapsedTime / (float)repeats;
    }

    // Main

    public static void main(String[] args) {
        rnd = new Random();
        rnd.setSeed(123);

        System.out.println("Balls number: " + NUM_BALLS);

        // Inicializar las pelotas
        long t1, t2;
        float elapsedTime;
        t1 = System.nanoTime();
        initializeBalls();
        t2 = System.nanoTime();
        elapsedTime = (t2 - t1) / 1000000000f;
        System.out.printf("Tiempo consumido para inicializar las pelotas: %.6f\n", elapsedTime);

        // simulation();
        elapsedTime = repeatSimulation(100);
        System.out.printf("Average elapsed time to simulation: %.6f\n", elapsedTime);
    }

}
