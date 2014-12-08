package be.teamsheepy.sheepherder.screens;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.SheepHerder;
import be.teamsheepy.sheepherder.SheepWorld;
import be.teamsheepy.sheepherder.objects.ConfettiMaker;
import be.teamsheepy.sheepherder.objects.Sheep;
import be.teamsheepy.sheepherder.utils.TimeFormatter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class GameOverScreen extends Screen {

    private GameScreen gameScreen;
    private long time;
    private int score;
    private boolean newBest;
    private ConfettiMaker confetti;
    private float confettiTimer;
    private TimeFormatter tfm = SheepHerder.timeFormatter;


    public GameOverScreen(GameScreen gameScreen, long time, int score, boolean newBest) {
        this.gameScreen = gameScreen;
        this.time = time;
        this.score = score;
        this.newBest = newBest;
        this.confetti = new ConfettiMaker();
    }

    @Override
    protected void draw() {
        SheepHerder.batch.begin();
        //confetti //TODO uitzoeken waarom alleen in linkeronderhoek vuurt
        confettiTimer -= Gdx.graphics.getDeltaTime();
        Random rand = new Random();
        if(confettiTimer <= 0) {
            confettiTimer = rand.nextFloat()+1;
            confetti.fire(new Vector2(rand.nextFloat() * SheepWorld.WORLD_WIDTH, rand.nextFloat() * SheepWorld.WORLD_HEIGHT));
        }
        confetti.render(SheepHerder.batch, Gdx.graphics.getDeltaTime());

        String timeString = tfm.format(this.time / 60000) + ":"
                             + tfm.format((this.time % 60000) / 1000);

        if (newBest) {
            SheepHerder.batch.draw(Assets.newbest, 50, 263, 380, 274);
        } else
            SheepHerder.batch.draw(Assets.gameover, 50, 263, 380, 274);
        String score = "SCORE: " + this.score + " + " + timeString + " = "
                + gameScreen.calculateScore(this.score, this.time);
        String best = "BEST: " + SavedData.highscore;
        Assets.font24.setColor(Color.BLACK);
        Assets.font22.setColor(Color.BLACK);
        float scoreWidth = Assets.font24.getBounds(score).width;
        float bestWidth = Assets.font22.getBounds(best).width;
        Assets.font24.draw(SheepHerder.batch, score, 240 - scoreWidth / 2, 435);
        Assets.font22.draw(SheepHerder.batch, best, 240 - bestWidth / 2, 395);
        Assets.font24.setColor(Color.WHITE);
        Assets.font22.setColor(Color.WHITE);
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
            if (touchPos.x > 175 && touchPos.x < 175 + 235
                    && touchPos.y > 285 && touchPos.y < 285 + 55) {
                // play again button touched

                /**
                 * Show questionnaire
                 */
                if (SavedData.gamesPlayed == 2 ||
                        !SavedData.questionnaireFilled && SavedData.gamesPlayed != 0
                                && SavedData.gamesPlayed % 5 == 0) {
                    ScreenService.getInstance().add(new QuestionnaireScreen());
                } else {
                    ScreenService.getInstance().add(new GameScreen());
                }
            } else if (touchPos.x > 70 && touchPos.x < 70 + 80
                    && touchPos.y > 285 && touchPos.y < 285 + 55) {
                //highscore button touched
                //SavedData.setUser("pieter","secret");
                if (!SavedData.userName.isEmpty() && !SavedData.password.isEmpty())
                    ScreenService.getInstance().add(new LeaderboardScreen());
                else
                    if (!SavedData.userName.isEmpty())
                        ScreenService.getInstance().add(new LoginScreen());
                    else
                        ScreenService.getInstance().add(new RegisterScreen());
            }
        }
    }

    @Override
    public void dispose() {

    }
}
