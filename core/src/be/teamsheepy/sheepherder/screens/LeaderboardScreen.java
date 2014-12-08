package be.teamsheepy.sheepherder.screens;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.utils.Leaderboard;
import be.teamsheepy.sheepherder.SheepHerder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class LeaderboardScreen extends Screen {

    private Leaderboard leaderboard;
    private float loadDots = 1;

    public LeaderboardScreen() {
        SheepHerder.analytics.trackPageView("leaderboard");

        this.leaderboard = new Leaderboard(SavedData.userName, SavedData.password);
    }

    @Override
    protected void draw() {
        SheepHerder.batch.begin();
        SheepHerder.batch.draw(Assets.highscore, 25, 25, 446, 764);
        Assets.font22.setColor(Color.BLACK);
        Assets.font28.setColor(Color.BLACK);

        /**
         * Leaderboard
         */
        if (leaderboard.status == Leaderboard.BUSY) {
            String loading = "Loading";
            loadDots = loadDots + 0.1f;
            String dots = new String(new char[(int)loadDots%4]).replace("\0", ".");
            float loadingW = Assets.font22.getBounds(loading).width;
            Assets.font22.draw(SheepHerder.batch, loading + dots, 240 - loadingW / 2, 500);
        } else if (leaderboard.status == Leaderboard.FAILED) {
            String error = leaderboard.error;
            float errorW = Assets.font22.getBounds(error).width;
            Assets.font22.draw(SheepHerder.batch, error, 240 - errorW / 2, 500);
        } else{
            Assets.font28.draw(SheepHerder.batch, "#", 57, 700);
            Assets.font28.draw(SheepHerder.batch, "User", 108, 700);
            Assets.font28.draw(SheepHerder.batch, "Score", 320, 700);
            for (int i =0; i<leaderboard.leaderBoardPage.size(); i++) {
                Assets.font22.draw(SheepHerder.batch, leaderboard.leaderBoardPage.get(i).rank + "", 60, 650 - 40*i);
                Assets.font22.draw(SheepHerder.batch, leaderboard.leaderBoardPage.get(i).playerName + "", 110, 650 - 40*i);
                Assets.font22.draw(SheepHerder.batch, leaderboard.leaderBoardPage.get(i).score + "", 320, 650 - 40*i);
            }
        }

        /**
         * Pager
         */
        if (leaderboard.status == Leaderboard.SUCCESS) {
            if (leaderboard.totalPages <= 1) {
                SheepHerder.batch.draw(Assets.current, 240 - Assets.current.getRegionWidth() / 2, 167);
                Assets.font22.draw(SheepHerder.batch, "1", 240 - Assets.font22.getBounds("1").width / 2, 195);
            } else {
                if (leaderboard.page == 0) {
                    SheepHerder.batch.draw(Assets.current, 240 - Assets.current.getRegionWidth() / 2, 167);
                    SheepHerder.batch.draw(Assets.next, 280, 167);
                    Assets.font22.draw(SheepHerder.batch, "1", 240 - Assets.font22.getBounds("1").width / 2, 195);
                } else {
                    SheepHerder.batch.draw(Assets.first, 75, 167);
                    SheepHerder.batch.draw(Assets.previous, 155, 167);
                    SheepHerder.batch.draw(Assets.current, 240 - Assets.current.getRegionWidth() / 2, 167);
                    if (leaderboard.page < leaderboard.totalPages-1)
                        SheepHerder.batch.draw(Assets.next, 280, 167);
                    Assets.font22.draw(SheepHerder.batch, leaderboard.page+1+"", 240 - Assets.font22.getBounds(leaderboard.page+1 + "").width / 2, 195);
                }
            }

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
            if (touchPos.x > 410 && touchPos.x < 410+60
                    && touchPos.y > 730 && touchPos.y < 730+60) {
                // close highscore window
                ScreenService.getInstance().removeOverlay(true);
            } else if (touchPos.x > 0 && touchPos.x < 800
                    && touchPos.y > 100 && touchPos.y < 300) {
                // other page selected
                if (touchPos.x > 70 && touchPos.x < 70+70
                        && touchPos.y > 159 && touchPos.y < 159+55)
                    // first page
                    leaderboard = new Leaderboard(0);
                if (touchPos.x > 152 && touchPos.x < 152+50
                        && touchPos.y > 159 && touchPos.y < 159+55)
                    // previous page
                    leaderboard = new Leaderboard(leaderboard.page - 1);
                if (touchPos.x > 275 && touchPos.x < 275+50
                        && touchPos.y > 159 && touchPos.y < 159+55)
                    // next page
                    leaderboard = new Leaderboard(leaderboard.page + 1);
            } else if (touchPos.x > 170 && touchPos.x < 170+145
                    && touchPos.y > 65 && touchPos.y < 65+55) {
                // logout
                SavedData.logout();
                ScreenService.getInstance().add(new LoginScreen());
            }
        }
    }

    @Override
    public void dispose() {

    }
}
