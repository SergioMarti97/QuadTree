package base;

import base.clock.GameClock;
import base.graphics.Renderer;
import base.input.Input;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This class should be implemented
 */
public class GameApplication extends Application {

    // Settings of the application

    private String appName = "default";

    private int width = 800;

    private int height = 600;

    // Classes which manages internally the game

    private AbstractGame game;

    // private Renderer renderer;

    private Input input;

    private GameClock clock;

    // Scene layout

    private Canvas canvas;

    @Override
    public void start(Stage stage) {
        // ----------------------------- //
        // - Set the application scene - //
        // ----------------------------- //

        // Set the canvas
        canvas = new Canvas();
        canvas.setFocusTraversable(true);

        // Choose between canvas or image view...
        StackPane pane = new StackPane(canvas);

        // bing width and height properties to the graphics display
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        // Input
        input = new Input(canvas);

        // Game Clock
        clock = new GameClock(this::update, this::render);

        // Set the scene
        Scene scene = new Scene(pane, width, height);
        stage.setScene(scene);
        stage.setTitle(appName);

        // Show the scene
        if (game != null) {
            game.initialize(this);
            clock.start();
            stage.show();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        game.stop(this);
    }

    // Update and Render methods

    protected void update(float elapsedTime) {
        game.update(this, elapsedTime);
        input.update();
    }

    protected void render() {
        game.render(this);
    }

    // Getters and Setters

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public AbstractGame getGame() {
        return game;
    }

    public void setGame(AbstractGame game) {
        this.game = game;
    }

    /*public Renderer getRenderer() {
        return renderer;
    }*/

    public Input getInput() {
        return input;
    }

    public GameClock getClock() {
        return clock;
    }

    public GraphicsContext getGraphicsContext() {
        return canvas.getGraphicsContext2D();
    }
}
