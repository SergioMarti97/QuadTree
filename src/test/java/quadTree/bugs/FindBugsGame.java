package quadTree.bugs;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomUtils;
import physics.spaceDivision.Rect;
import physics.spaceDivision.quadTree.QuadTreeContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FindBugsGame extends AbstractGame {

    private final int NUM_BUSHES = 100000;

    private final float MAX_SEARCH_AREA = 5000.0f;

    private final Vec2df BUSH_SIZE = new Vec2df(50, 100);

    private QuadTreeContainer<Bush> treeBushes;

    private List<Image> bushesImages;

    private Rect arena = new Rect(0, 0, 10000, 10000);

    private PanAndZoom pz;

    private Random rnd;

    private Rect searchRect;

    private Vec2df mouse;

    private boolean isDrawTree = false;

    private float searchSize = 500.0f;

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
        bushesImages = new ArrayList<>();

        pz = new PanAndZoom(gc.getGraphicsContext());

        mouse = new Vec2df();
        searchRect = new Rect();

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

    }

    @Override
    public void render(GameApplication gc) {
        Rect screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.BLACK);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Dibujar los árbustos en pantalla
        int numBushesDrawn = 0;
        float bushesElapsedTime;
        long t1, t2;

        t1 = System.nanoTime();
        for (var bush : treeBushes.search(screen)) {
            pz.drawImage(bushesImages.get(bush.getImgId()), bush.getPos(), bush.getScale());
            numBushesDrawn++;
        }
        t2 = System.nanoTime();
        bushesElapsedTime = (t2 - t1) / 1000000000f;

        // Dibujar el árbol
        if (isDrawTree) {
            pz.getGc().setStroke(Color.WHITE);
            pz.getGc().setLineWidth(10);
            treeBushes.draw(pz, screen);
        }

        // Dibujar el área de búsqueda
        pz.getGc().setFill(searchRect.getColor());
        pz.fillRect(searchRect.getPos(), searchRect.getSize());

        // Dibujar textos
        gc.getGraphicsContext().setFill(Color.WHITE);
        gc.getGraphicsContext().fillText(String.format("Arbustos dibujados: %d", numBushesDrawn), 10, 10);
        gc.getGraphicsContext().fillText(String.format("Tiempo necesario: %.6f", bushesElapsedTime), 10, 30);
    }
}
