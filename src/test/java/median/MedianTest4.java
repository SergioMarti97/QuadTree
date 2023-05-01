package median;

import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import median.split.KdTree2df;
import physics.ball.Ball;
import physics.spaceDivision.Rect;

import java.util.*;

public class MedianTest4 {

    public static final int NUM_SIMULATIONS = 100;

    public static final int NUM = 10;

    public static final Vec2df BALL_SIZE = new Vec2df(5, 10);

    public static final Vec2df BALL_VEL = new Vec2df(-30, 30);

    public static final Rect ARENA = new Rect(0, 0, 1000, 1000); // 2000, 2000

    public static Random rnd;

    public static List<Ball> balls;

    public static KdTree2df<Ball> tree;

    public static HashSet<Pair<Ball, Ball>> posibleCollidingPairs;

    public static List<Pair<Ball, Ball>> collidingPairs;

    public static int numCollisionsChecked = 0;

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
        b.getPos().set(rndFloat(0, ARENA.getSize().getX()), rndFloat(0, ARENA.getSize().getY()));
        float radius = rndFloat(BALL_SIZE);
        b.getSize().set(radius, radius);
        b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
        b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

        return b;
    }

    public static void initializeBalls(int num) {
        balls = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            balls.add(rndBall());
        }
    }

    public static void updateBallPosition(Ball b, float elapsedTime) {
        // Update position
        b.getPos().addToX(b.getVel().getX() * elapsedTime);
        b.getPos().addToY(b.getVel().getY() * elapsedTime);

        // Check area collisions
        if (b.getPos().getX() < ARENA.getPos().getX()) {
            b.getPos().setX(ARENA.getPos().getX());
            b.getVel().multiplyXBy(-1);
        }
        if (b.getPos().getX() + b.getSize().getX() >= ARENA.getSize().getX()) {
            b.getPos().setX(ARENA.getSize().getX() - b.getSize().getX());
            b.getVel().multiplyXBy(-1);
        }

        if (b.getPos().getY() < ARENA.getPos().getY()) {
            b.getPos().setY(ARENA.getPos().getY());
            b.getVel().multiplyYBy(-1);
        }
        if (b.getPos().getY() + b.getSize().getY() >= ARENA.getSize().getY()) {
            b.getPos().setY(ARENA.getSize().getY() - b.getSize().getY());
            b.getVel().multiplyYBy(-1);
        }
    }

    public static void updateStaticCollisions(Ball b, Ball n) {
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

    public static float runSimulation(int numBalls) {
        System.out.println("Points number: " + numBalls);

        initializeBalls(numBalls);

        long t1, t2;
        float elapsedTime;

        t1 = System.nanoTime();
        List<Pair<Vec2df, Ball>> pairs = new ArrayList<>();
        for (var b : balls) {
            b.calOri();
            pairs.add(new Pair<>(b.getOri(), b));
        }
        t2 = System.nanoTime();
        elapsedTime = (t2 - t1) / 1000000000f;

        System.out.printf("Elapsed time to create list of pairs: %.6f\n", elapsedTime);

        t1 = System.nanoTime();

        posibleCollidingPairs.clear();
        collidingPairs.clear();
        numCollisionsChecked = 0;

        tree = new KdTree2df<>(ARENA, pairs, posibleCollidingPairs);

        for (var p : posibleCollidingPairs) {
            Ball b1 = p.getKey();
            Ball b2 = p.getValue();
            if (b1.getId() != b2.getId()) {
                updateStaticCollisions(b1, b2);
            }
        }

        updateDynamicCollisions();

        t2 = System.nanoTime();
        elapsedTime = (t2 - t1) / 1000000000f;

        // tree.showResults();

        return elapsedTime;
    }

    public static void main(String[] args) {
        rnd = new Random();
        rnd.setSeed(123);

        posibleCollidingPairs = new HashSet<>();
        collidingPairs = new ArrayList<>();

        for (var num : new int[] {10, 20, 50, 100, 200, 300, 500, 1000, 1500, 15000}) {
            float elapsedTime = 0;
            elapsedTime += runSimulation(num);
            System.out.printf("Elapsed time to create the kd-tree: %.6f\n", elapsedTime / (float)NUM_SIMULATIONS);
            System.out.println("---".repeat(10));
        }
    }


}
