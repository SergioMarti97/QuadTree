package base.graphics;

/**
 * This interface adds the method "draw yourself"
 * Any class which implements this interface becomes a drawable class, so its objects
 * can be drawn on screen
 *
 * There are multiple classes to draw objects on screen. e.i.: PixelRenderer, GraphicContext, PanAndZoom...
 * For this reason, its need to be specified what class uses to draw the objects
 *
 * @param <T> the class which has the drawing methods
 */
public interface Drawable<T> {

    /**
     * Method to drawn the object
     * @param g the graphic class to drawn things
     */
    void drawYourself(T g);

}
