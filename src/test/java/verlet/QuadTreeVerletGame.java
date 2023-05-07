package verlet;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.spaceDivision.Rect;
import physics.spaceDivision.quadTree.QuadTree;
import physics.verlet.VerletCircle2df;
import shapes.Circle2df;

import java.util.Collection;

public class QuadTreeVerletGame extends AbstractVerletGame {

    private final Vec2df GRAVITY = new Vec2df(0, 100f);

    private final float MAX_SEARCH_AREA = 1000f;

    private QuadTree<VerletCircle2df> tree;

    private Rect searchRect;

    private float searchSize = 5.0f;

    private boolean isDrawTree = false;

    private void applyGravity(VerletCircle2df b) {
        b.accelerate(GRAVITY);
    }

    private Rect getBallRect(Circle2df b) {
        float x = b.getPos().getX() - b.getRadius();
        float y = b.getPos().getY() - b.getRadius();
        float w = b.getRadius() * 2;
        float h = b.getRadius() * 2;
        return new Rect(x, y, w, h);
    }

    private Rect getBallSearchRect(Circle2df b, float df) {
        float delta = df * df * 5;
        float x = b.getPos().getX() - b.getRadius() - delta;
        float y = b.getPos().getY() - b.getRadius() - delta;
        float w = b.getRadius() * 2 + delta;
        float h = b.getRadius() * 2 + delta;

        return new Rect(x, y, w, h);
    }

    private void checkCollisions(float dt) {
        for (int i = 0; i < balls.size(); i++) {
            VerletCircle2df b1 = balls.get(i);

            Rect search = getBallSearchRect(b1, dt);

            var neigbours = tree.search(search);

            for (var b2 : neigbours) {
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

                        Rect r = getBallRect(b1);
                        tree.remove(b1, r);
                        b1.getPos().addToX(-x);
                        b1.getPos().addToY(-y);
                        r = getBallRect(b1);
                        tree.insert(b1, r);

                        r = getBallRect(b2);
                        tree.remove(b2, r);
                        b2.getPos().addToX(x);
                        b2.getPos().addToY(y);
                        r = getBallRect(b2);
                        tree.insert(b2, r);
                    }
                }
            }
        }
    }

    @Override
    protected void addBall(VerletCircle2df b) {
        balls.add(b);
        tree.insert(b, getBallRect(b));
    }

    @Override
    protected void updateBalls(float dt) {
        for (var b : balls) {
            applyGravity(b);
            Rect r = getBallRect(b);
            tree.remove(b, r);
            checkConstrains(b);
            b.doVerletStep(dt);
            r = getBallRect(b);
            tree.insert(b, r);
        }

        checkCollisions(dt);
    }

    @Override
    protected void drawBall(VerletCircle2df b) {
        numBallsDrawn++;
        pz.setFill(b.getColor());
        b.drawYourself(pz);
    }

    @Override
    protected Collection<VerletCircle2df> getBallsToDrawn() {
        return tree.search(screen);
    }

    @Override
    public void initialize(GameApplication gc) {
        super.initialize(gc);
        tree = new QuadTree<>(getBallRect(arena));

        searchRect = new Rect();
        searchRect.setColor(Color.rgb(255, 255, 255, 0.3));
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
            var toRemove = tree.search(searchRect);
            for (var obj : toRemove) {
                tree.remove(obj, getBallRect(obj));
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
            tree.draw(pz, screen);
        }

        // Dibujar el área de búsqueda
        pz.getGc().setFill(searchRect.getColor());
        pz.fillRect(searchRect.getPos(), searchRect.getSize());

    }
}
