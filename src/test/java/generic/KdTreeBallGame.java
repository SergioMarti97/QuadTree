package generic;

import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import physics.spaceDivision.Rect;
import physics.spaceDivision.kdTree.KdTree2df;
import physics.spaceDivision.kdTree.Split2df;
import median.treeVisualizer.TreeVisualizer;
import physics.ball.Ball;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KdTreeBallGame extends AbstractBallGame {

    public KdTree2df<Ball> tree2df;

    // public HashSet<Pair<Ball, Ball>> posibleCollidingPairs;

    private boolean isDrawTree = false;

    private TreeVisualizer<Split2df<Ball>> treeVisualizer;

    private Image background;

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
        //posibleCollidingPairs.clear();

        List<Pair<Vec2df, Ball>> pairs = new ArrayList<>();
        for (var b : balls) {

            updateBallPosition(b, elapsedTime);

            b.calOri();
            pairs.add(new Pair<>(b.getOri(), b));
        }

        tree2df.recalculate(arena, pairs);
        treeVisualizer.recalculate(new Vec2df(arena.getRight() + 10, 0), tree2df.getRoot());

        for (var b : balls) {
            var neighbours = tree2df.search(b.getOri());

            for (var n : neighbours) {
                if (b.getId() != n.getId()) {
                    updateStaticCollisions(b, n);
                }
            }
        }

        /*for (var p : tree2df.getLeaves()) {
            Ball b1 = p.getKey();
            Ball b2 = p.getValue();

            if (b1.getId() != b2.getId()) {
                updateStaticCollisions(b1, b2);
            }
        }*/

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
        //posibleCollidingPairs = new HashSet<>();

        initializeBalls(NUM_BALLS);

        List<Pair<Vec2df, Ball>> pairs = new ArrayList<>();
        for (var b : balls) {
            b.calOri();
            pairs.add(new Pair<>(b.getOri(), b));
        }
        tree2df = new KdTree2df<>(arena, pairs);

        treeVisualizer = new TreeVisualizer<>(new Vec2df(arena.getRight() + 10, 0), tree2df.getRoot(), (node, pz) -> {
            Split2df<Ball> split2df = node.getItem();
            Vec2df pos = node.getPos();

            pz.fillText(split2df.toString(), pos.getX(), pos.getY());

            Vec2df s = new Vec2df(pos);

            final float offX = 10;

            Color beforeColor = (Color) pz.getFill();
            for (var p : split2df.getList()) {
                Ball b = p.getValue();
                pz.setFill(b.getColor());
                pz.fillOval(s, b.getSize());

                s.addToX(b.getSize().getX() + offX);
            }
            pz.setFill(beforeColor);

        }) {
            @Override
            public int getNumChildren(Split2df<Ball> node) {
                int numChildren = 0;
                if (node.getAbove() != null) {
                    numChildren++;
                }
                if (node.getBelow() != null) {
                    numChildren++;
                }
                return numChildren;
            }

            @Override
            public Split2df<Ball> getChild(Split2df<Ball> node, int childIndex) {
                if (childIndex == 0) {
                    return node.getAbove();
                }
                if (childIndex == 1) {
                    return node.getBelow();
                }
                return null;
            }
        };

        background = new Image("/stone_minecraft_16x16.jpg");
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        super.update(gc, elapsedTime);

        if (gc.getInput().isKeyDown(KeyCode.TAB)) {
            isDrawTree = !isDrawTree;
        }

        if (gc.getInput().isKeyDown(KeyCode.SHIFT)) {
            isUpdateUserInput = !isUpdateUserInput;
        }

        if (!isUpdateUserInput) {
            treeVisualizer.manageUserInput(gc.getInput(), MouseButton.PRIMARY, mouse);
        }
    }

    @Override
    public void render(GameApplication gc) {

        screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.BLACK);
        pz.fillRect(screen.getPos(), screen.getSize());
        pz.drawBackgroundRepeatImage(background);

        super.render(gc);

        pz.getGc().setLineWidth(1);
        pz.getGc().setFill(Color.WHITE);
        pz.getGc().setStroke(Color.WHITE);
        treeVisualizer.draw(pz);

        if (isDrawTree) {
            pz.getGc().setLineWidth(1);
            pz.getGc().setStroke(Color.WHITE);
            tree2df.draw(pz, screen);
        }

        gc.getGraphicsContext().setFill(Color.WHITE);
        Vec2df pos = new Vec2df(10, 30);
        gc.getGraphicsContext().fillText("Algorithm: KdTree", pos.getX(), pos.getY());
        pos.addToY(textLeading);
        pos = drawTexts(gc.getGraphicsContext(), pos);
        gc.getGraphicsContext().fillText(String.format("Update user input: %b", isUpdateUserInput), pos.getX(), pos.getY());
    }

}
