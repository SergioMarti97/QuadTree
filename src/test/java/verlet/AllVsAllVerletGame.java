package verlet;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.verlet.VerletCircle2df;

import java.util.Collection;

public class AllVsAllVerletGame extends AbstractVerletGame {

    private final Vec2df GRAVITY = new Vec2df(0, 100f);

    // Methods

    private void applyGravity(VerletCircle2df b) {
        b.accelerate(GRAVITY);
    }

    private void checkCollisions() {
        for (int i = 0; i < balls.size(); i++) {
            VerletCircle2df b1 = balls.get(i);
            for (int j = i + 1; j < balls.size(); j++) {
                VerletCircle2df b2 = balls.get(j);
                if (b1.getId() != b2.getId()) {
                    numCollisionsChecked++;
                    if (b1.overlaps(b2)) {
                        collidingPairs.add(new Pair<>(b1, b2));

                        /*Vec2df collisionAxis = new Vec2df(
                                b1.getPos().getX() - b2.getPos().getX(),
                                b1.getPos().getY() - b2.getPos().getY()
                        );

                        float dist = collisionAxis.mag();

                        collisionAxis.normalize();

                        float delta = b1.getRadius() + b2.getRadius() - dist;

                        delta *= 0.5f;

                        collisionAxis.multiply(delta);

                        b1.getPos().add(collisionAxis);
                        b2.getPos().sub(collisionAxis);*/

                        float dist = b1.dist(b2);

                        float overlap = 0.5f * (dist - b1.getRadius() - b2.getRadius());

                        if (dist == 0) {
                            dist = 1;
                        }

                        float x = overlap * (b1.getPos().getX() - b2.getPos().getX()) / dist;
                        float y = overlap * (b1.getPos().getY() - b2.getPos().getY()) / dist;

                        b1.getPos().addToX(-x);
                        b1.getPos().addToY(-y);

                        b2.getPos().addToX(x);
                        b2.getPos().addToY(y);
                    }
                }
            }
        }
    }

    // Override methods

    @Override
    protected void addBall(VerletCircle2df b) {
        balls.add(b);
    }

    @Override
    protected void updateBalls(float dt) {
        for (var b : balls) {
            applyGravity(b);
            checkConstrains(b);
            b.doVerletStep(dt);
        }

        checkCollisions();
    }

    @Override
    protected void drawBall(VerletCircle2df b) {
        numBallsDrawn++;
        pz.setFill(Color.WHITE);
        b.drawYourself(pz);
    }

    @Override
    protected Collection<VerletCircle2df> getBallsToDrawn() {
        return balls;
    }

    @Override
    public void initialize(GameApplication gc) {
        super.initialize(gc);
        initBalls();
    }
}
