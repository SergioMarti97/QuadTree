package base.clock;

import javafx.animation.AnimationTimer;

public class GameClock extends AnimationTimer {

    protected Updater updater;

    protected Render render;

    protected float elapsedTime = 0;

    protected long firstTime = 0;

    protected long lastTime = 0;

    protected long accumulatedTime = 0;

    protected int frames = 0;

    public GameClock(Updater updater, Render render) {
        this.updater = updater;
        this.render = render;
    }

    @Override
    public void handle(long now) {
        if (lastTime > 0) {
            long elapsedTime = now - lastTime;
            accumulatedTime += elapsedTime;
            this.elapsedTime = elapsedTime / 1000000000.0f;
            updater.update(this.elapsedTime);
        } else {
            firstTime = now;
        }
        lastTime = now;

        if (accumulatedTime >= 1000000000L) {
            accumulatedTime -= 1000000000L;
            frames = 0;
        }
        render.render();
        frames++;
    }

    public Updater getUpdater() {
        return updater;
    }

    public void setUpdater(Updater updater) {
        this.updater = updater;
    }

    public Render getRender() {
        return render;
    }

    public void setRender(Render render) {
        this.render = render;
    }

    public float getElapsedTime() {
        return elapsedTime;
    }

}
