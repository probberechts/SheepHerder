package be.teamsheepy.sheepherder.screens;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.SheepHerder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class QuestionnaireScreen extends Screen {

    public QuestionnaireScreen() {
        SheepHerder.analytics.trackPageView("questionnaire");
    }

    @Override
    protected void draw() {
        SheepHerder.batch.begin();
        SheepHerder.batch.draw(Assets.questionnaire, 50, 263, 380, 274);
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
            if (touchPos.x > 88 && touchPos.x < 88 + 95 && touchPos.y > 283
                    && touchPos.y < 283 + 55) {
                // answered yes
                Gdx.net.openURI("https://docs.google.com/forms/d/1I6um_3299Ux57FcbEwSJpWTBBHrBGrLknvol-5JANuo/viewform");
                SavedData.filledInQuestionaire();
                ScreenService.getInstance().add(new GameScreen());
            } else if (touchPos.x > 203 && touchPos.x < 203 + 200
                    && touchPos.y > 283 && touchPos.y < 283 + 55) {
                // answered later
                ScreenService.getInstance().add(new GameScreen());
            }
        }
    }

    @Override
    public void dispose() {

    }
}
