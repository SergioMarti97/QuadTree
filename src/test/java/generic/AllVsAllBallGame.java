package generic;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.ball.Ball;
import physics.spaceDivision.Rect;

import java.util.Collection;

public class AllVsAllBallGame extends AbstractBallGame {

    private void updateStaticCollisions() {
        for (var b : balls) {
            for (var n : balls) {
                if (b.getId() != n.getId()) {

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
        initializeBalls(NUM_BALLS);
    }

    @Override
    public void render(GameApplication gc) {
        super.render(gc);
        gc.getGraphicsContext().setFill(Color.WHITE);
        Vec2df pos = new Vec2df(10, 30);
        gc.getGraphicsContext().fillText("Algorithm: All vs All", pos.getX(), pos.getY());
        pos.addToY(leading);
        drawTexts(gc.getGraphicsContext(), pos);
    }
}
