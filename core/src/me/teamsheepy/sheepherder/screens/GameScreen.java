package me.teamsheepy.sheepherder.screens;

import me.teamsheepy.sheepherder.Assets;
import me.teamsheepy.sheepherder.SavedData;
import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.SheepWorld;
import me.teamsheepy.sheepherder.WorldGenerator;
import me.teamsheepy.sheepherder.WorldRenderer;
import me.teamsheepy.sheepherder.objects.Sheep;
import me.teamsheepy.sheepherder.utils.SwipeDetector2;
import me.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends ScreenAdapter {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_OVER = 3;
	static final int NEW_BEST = 4;
	static final int QUESTIONNAIRE = 5;
	static final int SWIPE_SUGGESTION = 6;

	private TimeFormatter tfm = SheepHerder.timeFormatter;

	private SheepHerder game;

	private int state;
	private OrthographicCamera camera;
	private SheepWorld world;
	private WorldRenderer renderer;
	private String scoreString;
	private String timeString;
	private boolean playScoreAnimation;
	private float scoreAnimationTime;
	private int lastScore;
	private Vector3 touchPos;
	private boolean swipeCheckboxTicked;
	private boolean suggestionShown = false;

	public GameScreen(SheepHerder game) {
		SheepHerder.analytics.trackPageView("game");

		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		world = (new WorldGenerator()).createWorld();
		renderer = new WorldRenderer(game.batcher, world);
		scoreString = "0";
		timeString = "1:00";
		if (!SavedData.questionnaireFilled && SavedData.gamesPlayed != 0
				&& SavedData.gamesPlayed % 5 == 0)
			state = QUESTIONNAIRE;
		else
			state = GAME_READY;
		touchPos = new Vector3(-500, -500, 0); // hide offscreen at start
		// Gdx.input.setInputProcessor(new SwipeDetector(world));
		Gdx.input.setInputProcessor(new GestureDetector(new SwipeDetector2(world)));
	}

	public void update(float deltaTime) {
		if (deltaTime > 0.1f)
			deltaTime = 0.1f;

		switch (state) {
		case GAME_READY:
			updateReady();
			break;
		case GAME_RUNNING:
			updateRunning(deltaTime);
			break;
		case GAME_PAUSED:
			updatePaused();
			break;
		case GAME_OVER:
		case NEW_BEST:
			updateGameOver();
			break;
		case QUESTIONNAIRE:
			updateQuestionnaire();
			break;
		case SWIPE_SUGGESTION:
			updateSwipeSuggestion();
			break;
		}
	}

	private void updateReady() {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
		}
	}

	private void updateRunning(float deltaTime) {
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);

			if (touchPos.x > 25 && touchPos.x < 25 + 30
					&& touchPos.y > 800-52 && touchPos.y < 800-52 + 30) {
				// retry button touched
				game.setScreen(new GameScreen(game));
			}

			world.updateRotationSheeps(touchPos);
		}

		world.update(deltaTime);
		world.timeLeft -= deltaTime;
		timeString = tfm.format(world.timeLeft / 6000) + ":"
				+ tfm.format((world.timeLeft % 6000) / 100);

		if (world.sheepsCollected > lastScore) {
			playScoreAnimation = true;
			scoreAnimationTime = 0;
		}
		lastScore = world.sheepsCollected;

		int currentScore = world.sheepsCollected == 0 ? 0
				: (world.sheepsCollected * 100 + world.timeLeft / 100);
		scoreString = "" + currentScore;

		if (world.state == SheepWorld.WORLD_STATE_GAME_OVER) {
			SavedData.addGamePlayed();
			SheepHerder.analytics.trackEvent("gameEvent", "gameOver",
					"gameTime", SheepWorld.GAME_TIME - world.timeLeft);
			SheepHerder.analytics.trackEvent("gameEvent", "gameOver",
					"sheepCollected", world.sheepsCollected);
			int escapedSheep = 0;
			for (Sheep s : world.sheeps) {
				if (s.state == Sheep.SHEEP_STATE_ESCAPED)
					escapedSheep++;
			}
			SheepHerder.analytics.trackEvent("gameEvent", "gameOver",
					"escapedSheep", escapedSheep);

			int newScore = calculateScore(world.sheepsCollected, world.timeLeft);
			if (newScore > SavedData.highscore) {
				SavedData.newHighscore(newScore);
				state = NEW_BEST;
			} else {
				state = GAME_OVER;
			}

		} else if (!suggestionShown
				&& world.tapCount >= 20
				&& !SavedData.neverShowSwipeSuggestion) {
			state = SWIPE_SUGGESTION;
			world.state = SheepWorld.WORLD_STATE_SWIPE_SUGGESTION;
		}
	}

	private void updatePaused() {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
		}
	}

	private void updateGameOver() {
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if (touchPos.x > 120 && touchPos.x < 120 + 240 && touchPos.y > 285
					&& touchPos.y < 285 + 55) {
				// play again button touched
				game.setScreen(new GameScreen(game));
			}
		}
	}

	private void updateQuestionnaire() {
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if (touchPos.x > 88 && touchPos.x < 88 + 95 && touchPos.y > 283
					&& touchPos.y < 283 + 55) {
				// answered yes
				Gdx.net.openURI("https://docs.google.com/forms/d/1eLUKnRGSiimqk4Mzr7ArOWwbGUMii8vZ7PagRqbDVe4/viewform?usp=send_form");
				state = GAME_READY;
			} else if (touchPos.x > 203 && touchPos.x < 203 + 200
					&& touchPos.y > 283 && touchPos.y < 283 + 55) {
				// answered later
				state = GAME_READY;
			}
		}
	}

	private void updateSwipeSuggestion() {
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			if (touchPos.x > 380 && touchPos.x < 420 && touchPos.y > 490
					&& touchPos.y < 530) {
				if (swipeCheckboxTicked)
					SavedData.neverShowSwipeSuggestion();
				suggestionShown = true;
				state = GAME_READY;
				world.state = SheepWorld.WORLD_STATE_RUNNING;
			} else if (touchPos.x > 110 && touchPos.x < 160 && touchPos.y > 285
					&& touchPos.y < 318) {
				swipeCheckboxTicked = !swipeCheckboxTicked;
			}
		}
	}

	public void draw() {
		GL20 gl = Gdx.gl;
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.render(touchPos, camera);

		camera.update();
		game.batcher.setProjectionMatrix(camera.combined);
		game.batcher.enableBlending();
		game.batcher.begin();

		switch (state) {
		case GAME_READY:
			presentReady();
			break;
		case GAME_RUNNING:
			presentRunning();
			if (playScoreAnimation)
				playScoreAnimation();
			break;
		case GAME_PAUSED:
			presentPaused();
			break;
		case GAME_OVER:
			presentGameOver(false);
			break;
		case NEW_BEST:
			presentGameOver(true);
			break;
		case QUESTIONNAIRE:
			presentQuestionnaire();
			break;
		case SWIPE_SUGGESTION:
			presentSwipeSuggestion();
			break;
		}
		game.batcher.end();

		// DEBUG: draw bounding boxes of objects
//		ShapeRenderer sr = new ShapeRenderer();
//		sr.setProjectionMatrix(camera.combined);
//		sr.begin(ShapeRenderer.ShapeType.Line);
		// sr.setColor(1, 1, 0, 1);
		// for (Sheep sheep : world.sheep)
		// sr.rect(sheep.bounds.x, sheep.bounds.y, sheep.bounds.width,
		// sheep.bounds.height);
		// sr.setColor(1, 0, 0, 1);
		// for (River river : world.rivers) {
		// for (Rectangle col : river.collisionAreas)
		// sr.rect(col.x, col.y, col.width, col.height);
		// sr.setColor(0, 1, 0, 0);
		// for (Bridge bridge: river.bridges)
		// sr.rect(bridge.bounds.x, bridge.bounds.y, bridge.bounds.width,
		// bridge.bounds.height);
		// }
		// sr.setColor(0, 1, 0, 1);
		// for (Tree tree : world.trees)
		// sr.rect(tree.bounds.x, tree.bounds.y, tree.bounds.width,
		// tree.bounds.height);
		// sr.setColor(1, 0, 0, 1);
		// for (Rectangle col : world.pen.collisionAreas)
		// sr.rect(col.x, col.y, col.width, col.height);
//		sr.rect(26, 800-53, 30, 30);
//		sr.end();
	}

	private void presentReady() {
		if (SavedData.highscore <= 0) { // only show tutorial when there have
										// been no sheep collected so far
			String line1 = "Drag your finger across the screen,";
			float line1W = Assets.font22.getBounds(line1).width;
			String line2 = "sheep will run away from it.";
			float line2W = Assets.font22.getBounds(line2).width;
			String line3 = "Guide the sheep to the pen.";
			float line3W = Assets.font22.getBounds(line3).width;

			Assets.font22.draw(game.batcher, line1, SheepWorld.WORLD_WIDTH / 2 - line1W / 2, 530);
			Assets.font22.draw(game.batcher, line2, SheepWorld.WORLD_WIDTH / 2 - line2W / 2, 500);
			Assets.font22.draw(game.batcher, line3, SheepWorld.WORLD_WIDTH / 2 - line3W / 2, 470);
		}
		Assets.font22.draw(game.batcher, "--click to play--", 140, 400);
	}

	private void presentRunning() {
		renderer.renderRetry();
		Assets.font22.setScale(0.9f);
		game.batcher.draw(Assets.clock, 480 - 230, 800 - 53);
		game.batcher.draw(Assets.time, 480 - 218, 800 - 40,
				Assets.time.getRegionWidth() / 2f, 0,
				Assets.time.getRegionWidth(), Assets.time.getRegionHeight(),
				1f, 1f, world.timeLeft / 16.666f, false);
		Assets.font22.draw(game.batcher, timeString, 480 - 193, 800 - 30);
		game.batcher.draw(Assets.score, 480 - 113, 800 - 53);
		Assets.font22.draw(game.batcher, scoreString, 480 - 75, 800 - 30);
		Assets.font22.setScale(0.7f);
		Assets.font22.draw(game.batcher, "Best: " + SavedData.highscore,
				480 - 113, 800 - 60);
		Assets.font22.setScale(1);
		
		//debug
		//Assets.font.draw(game.batcher, Gdx.input.getX()+","+Gdx.input.getY(), 480-190, 800-110);
		int i = 1;
		for(Sheep s : world.sheeps) {
			String xSpeed = s.body.getPosition().x + "";
			String ySpeed = s.body.getPosition().y + "";
			xSpeed = xSpeed.substring(0, xSpeed.indexOf('.'));
			ySpeed = ySpeed.substring(0, ySpeed.indexOf('.'));
			//Assets.font22.draw(game.batcher, i + ": x:" + xSpeed + " y:" + ySpeed + " rot:" + s.rotation, 10, 10 + i*30);
			//Assets.font22.draw(game.batcher, "c: " + Math.cos(Math.toRadians(30)) + " s:" + Math.sin(Math.toRadians(30)), 10, 10 + i*30);
			i++;
		}
	}

	private void presentSwipeSuggestion() {
		if (swipeCheckboxTicked) {
			game.batcher.draw(Assets.fullCheckBox, 50, 263);
		} else {
			game.batcher.draw(Assets.emptyCheckBox, 50, 263);
		}
	}

	private void presentPaused() {
		Assets.font22.draw(game.batcher, "--click to resume--", 140, 400);
	}

	private int calculateScore(int sheepCollected, int timeLeft) {
		if (sheepCollected == 0)
			return 0;
		return sheepCollected * 100 + timeLeft / 100;
	}

	private void presentGameOver(boolean newBest) {
		if (newBest) {
			game.batcher.draw(Assets.newbest, 50, 263, 380, 274);
		} else
			game.batcher.draw(Assets.gameover, 50, 263, 380, 274);
		String time = tfm.format(world.timeLeft / 6000) + ":"
				+ tfm.format((world.timeLeft % 6000) / 100);
		String score = "SCORE: " + world.sheepsCollected + " + " + time + " = "
				+ calculateScore(world.sheepsCollected, world.timeLeft);
		String best = "BEST: " + SavedData.highscore;
		Assets.font24.setColor(Color.BLACK);
		Assets.font22.setColor(Color.BLACK);
		float scoreWidth = Assets.font24.getBounds(score).width;
		float bestWidth = Assets.font22.getBounds(best).width;
		Assets.font24.draw(game.batcher, score, 240 - scoreWidth / 2, 435);
		Assets.font22.draw(game.batcher, best, 240 - bestWidth / 2, 395);
		Assets.font24.setColor(Color.WHITE);
		Assets.font22.setColor(Color.WHITE);
	}

	private void presentQuestionnaire() {
		game.batcher.draw(Assets.questionnaire, 50, 263, 380, 274);
	}

	private void playScoreAnimation() {
		int END_TIME = 100; // in ms
		String ANIMATION_TEXT = "+100";
		int START_POS_X = 130;
		int END_POS_X = 405;
		int START_POS_Y = 660;
		int END_POS_Y = 770;
		int START_SCALE = 3;
		int END_SCALE = 0;
		scoreAnimationTime += Gdx.graphics.getDeltaTime();
		float posX = START_POS_X + (END_POS_X - START_POS_X)
				* (scoreAnimationTime * 100 / END_TIME);
		float posY = START_POS_Y + (END_POS_Y - START_POS_Y)
				* (scoreAnimationTime * 100 / END_TIME);
		float scale = START_SCALE + (END_SCALE - START_SCALE)
				* (scoreAnimationTime * 100 / END_TIME);
		Assets.font22.setScale(scale);
		Assets.font22.draw(game.batcher, ANIMATION_TEXT, posX, posY);
		Assets.font22.setScale(1);
		if (scoreAnimationTime >= END_TIME)
			playScoreAnimation = false;
	}

	@Override
	public void render(float delta) {
		update(delta);
		draw();
	}

	@Override
	public void pause() {
		if (state == GAME_RUNNING)
			state = GAME_PAUSED;
	}
}
