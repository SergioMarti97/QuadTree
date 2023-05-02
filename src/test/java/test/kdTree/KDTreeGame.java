package test.kdTree;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import generic.AbstractBallGame;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.ball.Ball;
import physics.spaceDivision.Rect;
import deprecated.kdTree2.KDTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KDTreeGame extends AbstractBallGame {

    private final float MAX_SEARCH_AREA = 1000f;

    private KDTree<Ball> kdTree;

    private Rect searchRect;

    private float searchSize = 5.0f;

    private boolean isDrawTree = false;

    private void updateStaticCollisions() {
        var posibleCollisions = kdTree.getLeavesWithMoreOneItem();
        for (var ds : posibleCollisions) {
            for (var p1 : ds.getItems()) {
                for (var p2 : ds.getItems()) {
                    Ball b1 = p1.getValue();
                    Ball b2 = p2.getValue();

                    if (b1.getId() != b2.getId()) {
                        b1.calOri();
                        b1.calRadius();
                        b2.calOri();
                        b2.calRadius();

                        numCollisionsChecked++;

                        if (b1.doCirclesOverlap(b2)) {
                            collidingPairs.add(new Pair<>(b1, b2));

                            float dist = b1.dist(b2);

                            float overlap = 0.5f * (dist - b1.getRadius() - b2.getRadius());

                            if (dist == 0) {
                                dist = 1;
                            }

                            float x = overlap * (b1.getOri().getX() - b2.getOri().getX()) / dist;
                            float y = overlap * (b1.getOri().getY() - b2.getOri().getY()) / dist;

                            b1.getPos().addToX(-x);
                            b1.getPos().addToY(-y);

                            b2.getPos().addToX(x);
                            b2.getPos().addToY(y);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void addBall(Ball b) {
        balls.add(b);
        //kdTree.insert(b, new Rect(b.getPos(), b.getSize()));
    }

    @Override
    protected void updateBalls(float elapsedTime) {
        updateBallsPosition(elapsedTime);
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

        searchRect = new Rect();
        searchRect.setColor(Color.rgb(255, 255, 255, 0.3));

        initializeBalls(NUM_BALLS);

        List<Pair<Rect, Ball>> pairs = new ArrayList<>();
        for (var b : balls) {
            pairs.add(new Pair<>(new Rect(b.getPos(), b.getSize()), b));
        }
        kdTree = new KDTree(arena, pairs);
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        super.update(gc, elapsedTime);

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

        Vec2df searchArea = new Vec2df(this.searchSize);
        Vec2df rectOrigin = new Vec2df(this.mouse);
        searchArea.multiply(-0.5f);
        rectOrigin.add(searchArea);
        searchArea.multiply(-2);
        searchRect.set(rectOrigin, searchArea);

        List<Pair<Rect, Ball>> pairs = new ArrayList<>();
        for (var b : balls) {
            pairs.add(new Pair<>(new Rect(b.getPos(), b.getSize()), b));
        }
        // kdTree = new KDTree(arena, pairs);
    }

    @Override
    public void render(GameApplication gc) {
        super.render(gc);

        // Draw Tree
        if (isDrawTree) {
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(Color.WHITE);
            pz.getGc().setFill(Color.WHITE);
            kdTree.draw(pz, screen);
        }

        // Draw search area
        pz.getGc().setFill(searchRect.getColor());
        pz.fillRect(searchRect.getPos(), searchRect.getSize());

        // Draw texts
        gc.getGraphicsContext().setFill(Color.WHITE);
        Vec2df pos = new Vec2df(10, 30);
        gc.getGraphicsContext().fillText("Algorithm: KD-Tree", pos.getX(), pos.getY());
        pos.addToY(textLeading);
        drawTexts(gc.getGraphicsContext(), pos);

        if (isDrawTree) {
            pos.addToY(textLeading);
            pz.getGc().setStroke(Color.WHITE);
            pos.addToX(75);
            kdTree.drawTree(gc.getGraphicsContext(), pos);
        }
    }
}
