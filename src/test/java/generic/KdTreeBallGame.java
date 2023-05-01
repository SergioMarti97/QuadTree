package generic;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import median.split.KdTree2df;
import physics.ball.Ball;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class KdTreeBallGame extends AbstractBallGame {

    public KdTree2df<Ball> tree2df;

    public HashSet<Pair<Ball, Ball>> posibleCollidingPairs;

    private boolean isDrawTree = false;

    public void updateStaticCollisions(Ball b, Ball n) {
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

    @Override
    protected void addBall(Ball b) {
        balls.add(b);
    }

    @Override
    protected void updateBalls(float elapsedTime) {
        posibleCollidingPairs.clear();

        List<Pair<Vec2df, Ball>> pairs = new ArrayList<>();
        for (var b : balls) {

            updateBallPosition(b, elapsedTime);

            b.calOri();
            pairs.add(new Pair<>(b.getOri(), b));
        }

        tree2df.recalculate(arena, pairs, posibleCollidingPairs);

        for (var p : posibleCollidingPairs) {
            Ball b1 = p.getKey();
            Ball b2 = p.getValue();

            if (b1.getId() != b2.getId()) {
                updateStaticCollisions(b1, b2);
            }
        }

        updateDynamicCollisions();

    }

    @Override
    protected void drawBall(Ball b) {
        pz.getGc().setFill(b.getColor());
        pz.fillOval(b.getPos(), b.getSize());
        numBallsDrawn++;
    }

    @Override
    protected Collection<Ball> getBallsToDrawn() {
        return balls;
    }

    @Override
    public void initialize(GameApplication gc) {
        super.initialize(gc);
        posibleCollidingPairs = new HashSet<>();

        initializeBalls(NUM_BALLS);

        List<Pair<Vec2df, Ball>> pairs = new ArrayList<>();
        for (var b : balls) {
            b.calOri();
            pairs.add(new Pair<>(b.getOri(), b));
        }
        tree2df = new KdTree2df<>(arena, pairs, posibleCollidingPairs);
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        super.update(gc, elapsedTime);

        if (gc.getInput().isKeyDown(KeyCode.TAB)) {
            isDrawTree = !isDrawTree;
        }
    }

    @Override
    public void render(GameApplication gc) {
        super.render(gc);

        if (isDrawTree) {
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(Color.WHITE);
            tree2df.draw(pz, screen);
        }

        gc.getGraphicsContext().setFill(Color.WHITE);
        Vec2df pos = new Vec2df(10, 30);
        gc.getGraphicsContext().fillText("Algorithm: KdTree", pos.getX(), pos.getY());
        pos.addToY(textLeading);
        drawTexts(gc.getGraphicsContext(), pos);
    }

}
