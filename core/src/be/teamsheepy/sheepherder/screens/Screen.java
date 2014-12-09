package be.teamsheepy.sheepherder.screens;

import com.badlogic.gdx.utils.Disposable;

/**
 * An abstract screen class.
 */
public abstract class Screen implements Disposable {

    /**
     * Constructs a Screen instance.
     */
    protected Screen() {}

    /**
     * Called when the screen is created.  Used for initialization.
     */
    protected void create() { }

    /**
     * Draws the screen.
     */
    protected abstract void draw();

    /**
     * Determines if the screen is an overlay or not.  Overlays will not cause screens below it to automatically exit.
     *
     * @return {@code true} if the screen is an overlay; otherwise, {@code false}.
     */
    protected abstract boolean isOverlay();

    /**
     * Pauses the screen.
     */
    protected void pause() { }

    /**
     * Called when the screen is resized.
     *
     * @param width
     *         The new game window width (in pixels).
     * @param height
     *         The new game window height (in pixels).
     */
    protected void resize(final int width, final int height) { }

    /**
     * Resumes the screen after a pause.
     */
    protected void resume() { }

    /**
     * Updates the screen.
     *
     * @param dt
     *         The total amount of time, in seconds, since the last update.
     */
    protected abstract void update(final float dt);
}
