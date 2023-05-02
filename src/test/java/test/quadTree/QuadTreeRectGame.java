package test.quadTree;

import base.AbstractGame;
import base.GameApplication;
import base.vectors.points2d.Vec2df;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import panAndZoom.PanAndZoom;
import panAndZoom.PanAndZoomUtils;
import physics.spaceDivision.Rect;
import deprecated.QuadTreeContainer;

import java.util.Random;

@Deprecated
public class QuadTreeRectGame extends AbstractGame {

    private final float FIELD_AREA = 100000f;

    private final int NUM_OBJECTS = 1000000;

    private final float MAX_SEARCH_AREA = 5000.0f;

    private final Vec2df RECT_SIZE = new Vec2df(1, 100);

    private QuadTreeContainer<Rect> tree;

    private PanAndZoom pz;

    private float searchSize = 500.0f;

    private Rect searchRect;

    private Random rnd;

    private Vec2df mouse;

    private boolean isDrawTree = false;

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
        pz = new PanAndZoom(gc.getGraphicsContext());

        tree = new QuadTreeContainer<>(new Rect(0,0, FIELD_AREA, FIELD_AREA));

        for (int i = 0; i < NUM_OBJECTS; i++) {
            Rect r = new Rect();
            r.getPos().set(rndFloat(0, FIELD_AREA), rndFloat(0, FIELD_AREA));
            r.getSize().set(rndFloat(RECT_SIZE), rndFloat(RECT_SIZE));
            r.setColor(Color.rgb(rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255)));

            tree.insert(r, new Rect(r.getPos(), r.getSize()));
        }

        mouse = new Vec2df();
        searchRect = new Rect();
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

        var toRemove = tree.search(searchRect);
        if (gc.getInput().isKeyHeld(KeyCode.DELETE)) {
            for (var obj : toRemove) {
                tree.remove(obj);
            }
        }
    }

    @Override
    public void render(GameApplication gc) {
        Rect screen = new Rect(pz.getWorldTopLeft(), pz.getWorldVisibleArea());

        // Dibujar el fondo
        gc.getGraphicsContext().setFill(Color.BLACK);
        pz.fillRect(screen.getPos(), screen.getSize());

        // Dibujar los objetos en pantalla
        int numObjDrawn = 0;
        float elapsedTime;

        long t1 = System.nanoTime();
        for (var obj : tree.search(screen)) {
            if (screen.overlaps(obj)) {
                pz.getGc().setFill(obj.getColor());
                pz.fillRect(obj.getPos(), obj.getSize());
                numObjDrawn++;
            }
        }
        long t2 = System.nanoTime();
        elapsedTime = (t2 - t1) / 1000000000f;

        // Dibujar el quadTree
        if (isDrawTree) {
            pz.getGc().setFill(Color.WHITE);
            pz.getGc().setStroke(Color.WHITE);
            pz.getGc().setLineWidth(10);
            tree.draw(pz, screen);
        }

        // Dibujar el área de búsqueda
        pz.getGc().setFill(searchRect.getColor());
        pz.fillRect(searchRect.getPos(), searchRect.getSize());

        // Dibujar los objetos seleccionados
        pz.getGc().setFill(Color.RED);
        for (var obj : tree.search(searchRect)) {
            if (screen.overlaps(obj)) {
                Vec2df ori = new Vec2df(obj.getPos());
                Vec2df size = new Vec2df(obj.getSize());
                size.multiply(0.5f);
                ori.add(size);
                Vec2df textPos = pz.worldToScreen(obj.getPos());

                String str = obj.toString();
                str = str.replace("physics.spaceDivision.Rect", "");
                gc.getGraphicsContext().fillText(str, textPos.getX(), textPos.getY());
            }
        }

        // Dibujar textos
        gc.getGraphicsContext().setFill(Color.WHITE);
        gc.getGraphicsContext().fillText(String.format("Objetos dibujados: %d", numObjDrawn), 10, 10);
        gc.getGraphicsContext().fillText(String.format("Tiempo necesario: %.6f", elapsedTime), 10, 30);
    }

}
