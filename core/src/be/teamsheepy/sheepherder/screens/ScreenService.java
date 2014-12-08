package be.teamsheepy.sheepherder.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;

/**
 * The screen service class.
 */
public class ScreenService extends ScreenAdapter {

    private static ScreenService instance = null;

    protected ScreenService() {

    }

    public static ScreenService getInstance() {
        if(instance == null) {
            instance = new ScreenService();
        }
        return instance;
    }

    private Screen base;
    private Screen overlay;


    /**
     * Adds the specified screen to the service.
     *
     * @param screen
     *         The screen.
     */
    public void add(final Screen screen) {
        screen.create();
        screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (screen.isOverlay()) {
            overlay = screen;
            base.pause();
        } else {
            base = screen;
            overlay = null;
        }
    }

    /** Releases all resources of this object. */
    @Override public void dispose() {
        try {
            base.dispose();
            overlay.dispose();
        } catch (NullPointerException e) {}
    }

    /**
     * Draws all screens in the service.
     */
    public void draw() {
        try {
            base.draw();
            overlay.draw();
        } catch (NullPointerException e) {}
    }

    /**
     * Pauses all screens in the service.
     */
    public void pause() {
        try {
            base.pause();
            overlay.pause();
        } catch (NullPointerException e) {}
    }

    /**
     * Called when the game window is resized.
     *
     * @param width
     *         The new game window width (in pixels).
     * @param height
     *         The new game window height (in pixels).
     */
    public void resize(final int width, final int height) {
        try {
            base.resize(width, height);
            overlay.resize(width, height);
        } catch (NullPointerException e) {}
    }

    /**
     * Resumes all screens in the service.
     */
    public void resume() {
        try {
            //base.resume();
            overlay.resume();
        } catch (NullPointerException e) {}
    }

    /**
     * Updates the screen service.
     *
     * @param dt
     *         The total amount of time, in seconds, since the last update.
     */
    public void update(final float dt) {
        try {
            base.update(dt);
            overlay.update(dt);
        } catch (NullPointerException e) {}
    }

    /**
     * Removes the specified screen from the service.
     *
     */
    public void removeOverlay(boolean resume) {
        try {
            overlay.dispose();
            overlay = null;
            if (resume)
                base.resume();
        } catch (NullPointerException e) {}
    }

    public void render(float delta) {
        update(delta);
        draw();
    }

    public boolean existOverlay() {
        return (overlay != null);
    }
}
