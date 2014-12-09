package be.teamsheepy.sheepherder.screens;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.SheepHerder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class SwipeSuggestionScreen extends Screen {
    private boolean swipeCheckboxTicked = false;

    public SwipeSuggestionScreen() {
        SheepHerder.analytics.trackPageView("swipe suggestion");
    }

    @Override
    protected void draw() {
        SheepHerder.batch.begin();
        if (swipeCheckboxTicked) {
            SheepHerder.batch.draw(Assets.fullCheckBox, 50, 263);
        } else {
            SheepHerder.batch.draw(Assets.emptyCheckBox, 50, 263);
        }
        SheepHerder.batch.end();
    }

    @Override
    protected boolean isOverlay() {
        return true;
    }

    @Override
    protected void update(float dt) {
        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            SheepHerder.camera.unproject(touchPos);
            if (touchPos.x > 380 && touchPos.x < 420
                    && touchPos.y > 490 && touchPos.y < 530) {
                if (swipeCheckboxTicked)
                    SavedData.neverShowSwipeSuggestion();
                ScreenService.getInstance().removeOverlay(false);
            } else if (touchPos.x > 110 && touchPos.x < 160
                    && touchPos.y > 285 && touchPos.y < 318) {
                swipeCheckboxTicked = !swipeCheckboxTicked;
            }
        }
    }

    @Override
    public void dispose() {

    }
}
