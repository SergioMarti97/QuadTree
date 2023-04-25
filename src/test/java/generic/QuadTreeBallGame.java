package generic;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.ball.Ball;
import physics.spaceDivision.Rect;
import physics.spaceDivision.quadTree.QuadTree;

import java.util.Collection;

public class QuadTreeBallGame extends AbstractBallGame {

    private final float MAX_SEARCH_AREA = 1000f;

    private QuadTree<Ball> treeBalls;

    private Rect searchRect;

    private float searchSize = 5.0f;

    private boolean isDrawTree = false;

    private void updateStaticCollisions(Ball b, float elapsedTime) {
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

    @Override
    protected void addBall(Ball b) {
        balls.add(b);
        treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
    }

    @Override
    protected void updateBalls(float elapsedTime) {
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

    @Override
    protected void drawBall(Ball b) {
        pz.getGc().setFill(b.getColor());
        pz.fillOval(b.getPos(), b.getSize());
        numBallsDrawn++;
    }

    @Override
    protected Collection<Ball> getBallsToDrawn() {
        return treeBalls.search(screen);
    }

    @Override
    public void initialize(GameApplication gc) {
        super.initialize(gc);
        treeBalls = new QuadTree<>(arena);

        searchRect = new Rect();
        searchRect.setColor(Color.rgb(255, 255, 255, 0.3));

        initializeBalls(NUM_BALLS);
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

        if (gc.getInput().isKeyHeld(KeyCode.DELETE)) {
            var toRemove = treeBalls.search(searchRect);
            for (var obj : toRemove) {
                treeBalls.remove(obj, new Rect(obj.getPos(), obj.getSize()));
                balls.remove(obj);
            }
        }
    }

    @Override
    public void render(GameApplication gc) {
        super.render(gc);

        if (isDrawTree) {
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(Color.WHITE);
            treeBalls.draw(pz, screen);
        }

        // Dibujar el área de búsqueda
        pz.getGc().setFill(searchRect.getColor());
        pz.fillRect(searchRect.getPos(), searchRect.getSize());

        gc.getGraphicsContext().setFill(Color.WHITE);
        Vec2df pos = new Vec2df(10, 30);
        gc.getGraphicsContext().fillText("Algorithm: QuadTree", pos.getX(), pos.getY());
        pos.addToY(textLeading);
        drawTexts(gc.getGraphicsContext(), pos);
    }

}
