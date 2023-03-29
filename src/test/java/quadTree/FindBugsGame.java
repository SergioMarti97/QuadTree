package quadTree;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomUtils;
import physics.ball.Ball;
import physics.quadTree.Rect;
import physics.quadTree.part1.QuadTreeContainer;
import quadTree.bugs.Bush;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FindBugsGame extends AbstractGame {

    private final int NUM_BUSHES = 100000;

    private final int NUM_BALLS = 10;

    private final float MAX_SEARCH_AREA = 5000.0f;

    private final Vec2df BUSH_SIZE = new Vec2df(50, 100);

    private final Vec2df BALL_SIZE = new Vec2df(500, 800);

    private final Vec2df BALL_VEL = new Vec2df(-1000, 1000);

    private QuadTreeContainer<Bush> treeBushes;

    private QuadTreeContainer<Ball> treeBalls;

    private List<Image> bushesImages;

    private List<Ball> balls;

    private Rect arena = new Rect(0, 0, 10000, 10000);

    private Vec2df screenSize;

    private PanAndZoom pz;

    private Random rnd;

    private boolean isDrawTree = false;

    private float searchSize = 500.0f;

    private Rect searchRect;

    private Vec2df mouse;

    private boolean updateBalls = false;

    // Funciones

    private float rndFloat(float a, float b) {
        return rnd.nextFloat() * (b - a) + a;
    }

    private float rndFloat(Vec2df v) {
        return rndFloat(v.getX(), v.getY());
    }

    // Funcionamiento del juego

    @Override
    public void initialize(GameApplication gc) {
        rnd = new Random();
        treeBushes = new QuadTreeContainer<>(arena);
        treeBalls = new QuadTreeContainer<>(arena);
        bushesImages = new ArrayList<>();
        balls = new ArrayList<>();

        pz = new PanAndZoom(gc.getGraphicsContext());

        mouse = new Vec2df();
        searchRect = new Rect();

        screenSize = new Vec2df(gc.getWidth(), gc.getHeight());

        bushesImages.add(new Image("/bush_01.png"));
        bushesImages.add(new Image("/bush_02.png"));
        bushesImages.add(new Image("/bush_03.png"));
        bushesImages.add(new Image("/bush_04.png"));
        bushesImages.add(new Image("/bush_05.png"));

        for (int i = 0; i < NUM_BUSHES; i++) {
            Bush b = new Bush();
            b.setImgId(rnd.nextInt(bushesImages.size()));
            b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
            b.getScale().set(rndFloat(BUSH_SIZE), rndFloat(BUSH_SIZE));
            treeBushes.insert(b, new Rect(b.getPos(), b.getScale()));
        }

        for (int i = 0; i < NUM_BALLS; i++) {
            Ball b = new Ball();

            b.setId(i);
            b.getPos().set(rndFloat(0, arena.getSize().getX()), rndFloat(0, arena.getSize().getY()));
            float radius = rndFloat(BALL_SIZE);
            b.getSize().set(radius, radius);
            b.getVel().set(rndFloat(BALL_VEL), rndFloat(BALL_VEL));
            b.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

            treeBalls.insert(b, new Rect(b.getPos(), b.getSize()));
            balls.add(b);
        }
    }

    @Override
    public void update(GameApplication gc, float elapsedTime) {
        pz.handlePanAndZoom(gc, MouseButton.MIDDLE, 0.001f, true, true);

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

        Vec2df mouse = new Vec2df((float)gc.getInput().getMouseX(), (float)gc.getInput().getMouseY());
        this.mouse.set(PanAndZoomUtils.screenToWorld(mouse, pz.getWorldOffset(), pz.getWorldScale()));

        Vec2df searchArea = new Vec2df(this.searchSize);
        Vec2df rectOrigin = new Vec2df(this.mouse);
        searchArea.multiply(-0.5f);
        rectOrigin.add(searchArea);
        searchArea.multiply(-2);
        searchRect.set(rectOrigin, searchArea);
        searchRect.setColor(Color.rgb(255, 255, 255, 0.3));

        if (gc.getInput().isKeyHeld(KeyCode.DELETE)) {
            var toRemove = treeBushes.search(searchRect);
            for (var obj : toRemove) {
                treeBushes.remove(obj);
            }
        }

        if (gc.getInput().isKeyDown(KeyCode.SPACE)) {
            updateBalls = !updateBalls;
        }

        if (updateBalls) {
            for (var b : balls) {
                b.getPos().addToX(b.getVel().getX() * elapsedTime);
                b.getPos().addToY(b.getVel().getY() * elapsedTime);

                treeBalls.relocate(b, new Rect(b.getPos(), b.getSize()));
            }
        }
    }

    @Override
    public void render(GameApplication gc) {
        Rect screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.BLACK);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Dibujar las pelotas
        int numBallsDrawn = 0;
        float ballsElapsedTime;
        long t1, t2;

        t1 = System.nanoTime();
        for (var ball : treeBalls.search(screen)) {
            pz.getGc().setFill(ball.getColor());
            pz.fillOval(ball.getPos(), ball.getSize());
            numBallsDrawn++;
        }
        t2 = System.nanoTime();
        ballsElapsedTime = (t2 - t1) / 1000000000f;

        // Dibujar los árbustos en pantalla
        int numBushesDrawn = 0;
        float bushesElapsedTime;

        t1 = System.nanoTime();
        for (var bush : treeBushes.search(screen)) {
            // pz.drawImage(bushesImages.get(bush.getImgId()), bush.getPos(), bush.getScale());
            numBushesDrawn++;
        }
        t2 = System.nanoTime();
        bushesElapsedTime = (t2 - t1) / 1000000000f;

        // Dibujar el árbol
        if (isDrawTree) {
            pz.getGc().setStroke(Color.WHITE);
            pz.getGc().setLineWidth(10);
            // treeBushes.draw(pz, screen);
            treeBalls.draw(pz, screen);
        }

        // Dibujar el área de búsqueda
        pz.getGc().setFill(searchRect.getColor());
        pz.fillRect(searchRect.getPos(), searchRect.getSize());

        // Dibujar textos
        gc.getGraphicsContext().setFill(Color.WHITE);
        gc.getGraphicsContext().fillText(String.format("Arbustos dibujados: %d", numBushesDrawn), 10, 10);
        gc.getGraphicsContext().fillText(String.format("Tiempo necesario: %.6f", bushesElapsedTime), 10, 30);
        gc.getGraphicsContext().fillText(String.format("Pelotas dibujadas: %d", numBallsDrawn), 10, 50);
        gc.getGraphicsContext().fillText(String.format("Tiempo necesario: %.6f", ballsElapsedTime), 10, 70);
    }
}
