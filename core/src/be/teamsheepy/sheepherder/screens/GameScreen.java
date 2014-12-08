package be.teamsheepy.sheepherder.screens;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.SheepHerder;
import be.teamsheepy.sheepherder.SheepWorld;
import be.teamsheepy.sheepherder.WorldGenerator;
import be.teamsheepy.sheepherder.WorldRenderer;
import be.teamsheepy.sheepherder.objects.Sheep;
import be.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import be.teamsheepy.sheepherder.utils.TouchTracker;

public class GameScreen extends Screen {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;

	private TimeFormatter tfm = SheepHerder.timeFormatter;

	private int state;
	private SheepWorld world;
	private WorldRenderer renderer;
	private String scoreString;
	private String timeString;
	private boolean playScoreAnimation;
	private float scoreAnimationTime;
	private int lastScore;
	private Vector3 touchPos;
	private boolean suggestionShown = false;
	private TouchTracker touchTracker;
	private long startTime;
	private long lastPlayedSound = SheepWorld.GAME_TIME;
	private boolean alreadyUpdated;

	public GameScreen() {
		SheepHerder.analytics.trackPageView("game");

		world = (new WorldGenerator()).createWorld();
		renderer = new WorldRenderer(SheepHerder.batch, world);
		scoreString = "0";
		timeString = "1:00";

		state = GAME_READY;

		touchPos = new Vector3(-500, -500, 0); // hide offscreen at start
		touchTracker = new TouchTracker();
		Gdx.input.setInputProcessor(touchTracker);
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
		}
	}

	private void updateReady() {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
			startTime = System.currentTimeMillis();
		}
	}

	private void updateRunning(float deltaTime) {
		/**
		 * Handle screen touch
		 */
		if (Gdx.input.isTouched()) {
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			SheepHerder.camera.unproject(touchPos);

			if (touchPos.x > 25 && touchPos.x < 25 + 30
					&& touchPos.y > 800 - 52 && touchPos.y < 800 - 52 + 30) {
				// retry button touched
				ScreenService.getInstance().add(new GameScreen());
			}

			world.updateRotationSheeps(touchPos);
		}

		/**
		 * Update world objects, score and time left
		 */
		world.update(deltaTime);

		world.timeLeft = SheepWorld.GAME_TIME - (System.currentTimeMillis() - startTime);
		timeString = tfm.format(world.timeLeft / 60000) + ":"
				+ tfm.format((world.timeLeft % 60000) / 1000);

		int currentScore = (int) (world.sheepsCollected == 0 ? 0
				: (world.sheepsCollected * 100 + world.timeLeft / 1000));
		scoreString = "" + currentScore;

		/**
		 * Play +100 animation if the player leads a sheep into the pen
		 */
		if (world.sheepsCollected > lastScore) {
			playScoreAnimation = true;
			scoreAnimationTime = 0;
		}
		lastScore = world.sheepsCollected;

		/**
		 * Check if game over
		 */
		if (world.state == SheepWorld.WORLD_STATE_GAME_OVER) {
			if (!alreadyUpdated) {
				// wanneer spel gepauseerd wordt tijden game over wordt deze data een
				// tweede keer doorgestuurd
				alreadyUpdated = true;

				SavedData.addGamePlayed();

				SheepHerder.analytics.trackTimedEvent("gameOver", "gameTime",
						SavedData.gamesPlayed + "", SheepWorld.GAME_TIME - world.timeLeft);

				SheepHerder.analytics.trackEvent("gameOver", "sheepCollected",
						SavedData.gamesPlayed + "", world.sheepsCollected);

				int escapedSheep = 0;
				for (Sheep s : world.sheeps) {
					if (s.state == Sheep.SHEEP_STATE_ESCAPED)
						escapedSheep++;
				}
				SheepHerder.analytics.trackEvent("gameOver", "escapedSheep",
						SavedData.gamesPlayed + "", escapedSheep);

				SheepHerder.analytics.trackEvent("swipeData", "numTaps",
						SavedData.gamesPlayed + "", touchTracker.countTaps());
				SheepHerder.analytics.trackEvent("swipeData", "numSwipes",
						SavedData.gamesPlayed + "", touchTracker.countSwipes());
				SheepHerder.analytics.trackTimedEvent("swipeData", "avgSwipeTime",
						SavedData.gamesPlayed + "", touchTracker.getAverageTouchTime());
				SheepHerder.analytics.trackTimedEvent("swipeData", "minSwipeTime",
						SavedData.gamesPlayed + "", touchTracker.getMinTouchTime());
				SheepHerder.analytics.trackTimedEvent("swipeData", "maxSwipeTime",
						SavedData.gamesPlayed + "", touchTracker.getMaxTouchTime());
			}

			int newScore = calculateScore(world.sheepsCollected, world.timeLeft);
			if (newScore > SavedData.highscore) {
				SavedData.newHighscore(newScore);
				ScreenService.getInstance().add(new GameOverScreen(this, world.timeLeft, world.sheepsCollected, true));
			} else {
				ScreenService.getInstance().add(new GameOverScreen(this, world.timeLeft, world.sheepsCollected, false));

			}

			/**
			 * Show swipe suggestion to the user
			 */
		} else if (!suggestionShown
				&& touchTracker.countTaps() >= 20
				&& !SavedData.neverShowSwipeSuggestion) {
			suggestionShown = true;
			state = GAME_PAUSED;
			ScreenService.getInstance().add(new SwipeSuggestionScreen());
		}
	}

	private void updatePaused() {
		if(Gdx.input.justTouched() && !ScreenService.getInstance().existOverlay()) 
				state = GAME_RUNNING;
		
		startTime = System.currentTimeMillis() - (SheepWorld.GAME_TIME - world.timeLeft);
	}

	public void draw() {
		GL20 gl = Gdx.gl;
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.render(touchPos, SheepHerder.camera);

		SheepHerder.camera.update();
		SheepHerder.batch.setProjectionMatrix(SheepHerder.camera.combined);
		SheepHerder.batch.enableBlending();
		SheepHerder.batch.begin();

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
		}
		SheepHerder.batch.end();

		// DEBUG: draw bounding boxes of objects
		//ShapeRenderer sr = new ShapeRenderer();
		//sr.setProjectionMatrix(SheepHerder.camera.combined);
		//sr.begin(ShapeRenderer.ShapeType.Line);
		//sr.rect(70, 159, 70, 55);
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
		// sr.rect(26, 800-53, 30, 30);
		//sr.end();
	}

	@Override
	protected boolean isOverlay() {
		return false;
	}

	private void presentReady() {
		renderer.sleeping = true;
		Assets.font22.setColor(Color.WHITE);
		if (SavedData.highscore <= 0) { // only show tutorial when there have
										// been no sheep collected so far
			String line1 = "Drag your finger across the screen,";
			float line1W = Assets.font22.getBounds(line1).width;
			String line2 = "sheep will run away from it.";
			float line2W = Assets.font22.getBounds(line2).width;
			String line3 = "Guide the sheep to the pen.";
			float line3W = Assets.font22.getBounds(line3).width;

			Assets.font22.draw(SheepHerder.batch, line1, SheepWorld.WORLD_WIDTH / 2 - line1W / 2, 530);
			Assets.font22.draw(SheepHerder.batch, line2, SheepWorld.WORLD_WIDTH / 2 - line2W / 2, 500);
			Assets.font22.draw(SheepHerder.batch, line3, SheepWorld.WORLD_WIDTH / 2 - line3W / 2, 470);
		}
		Assets.font22.draw(SheepHerder.batch, "--" + SheepHerder.TAP_OR_CLICK + " to play--", 140, 400);
	}

	private void presentRunning() {
		renderer.sleeping = false;
		renderer.renderRetry();
		Assets.font22.setScale(0.9f);
		Assets.font22.setColor(Color.WHITE);

		if((world.timeLeft % 60000) / 1000 < 10) {
			//only 10 sec left
			SheepHerder.batch.setColor(Color.RED);
			Assets.font22.setColor(Color.RED);
		}
		SheepHerder.batch.draw(Assets.clock, 480 - 230, 800 - 53);
		SheepHerder.batch.draw(Assets.time, 480 - 218, 800 - 40,
				Assets.time.getRegionWidth() / 2f, 0,
				Assets.time.getRegionWidth(), Assets.time.getRegionHeight(),
				1f, 1f, world.timeLeft / 166.666f, false);
		Assets.font22.draw(SheepHerder.batch, timeString, 480 - 193, 800 - 30);

		SheepHerder.batch.setColor(Color.WHITE);
		Assets.font22.setColor(Color.WHITE);

		SheepHerder.batch.draw(Assets.score, 480 - 113, 800 - 53);
		Assets.font22.draw(SheepHerder.batch, scoreString, 480 - 75, 800 - 30);
		Assets.font22.setScale(0.7f);
		Assets.font22.draw(SheepHerder.batch, "Best: " + SavedData.highscore,
				480 - 113, 800 - 60);
		Assets.font22.setScale(1);
		long chance = world.timeLeft % 10000L;
		if(chance >= 0 && chance <= 3000 && lastPlayedSound-world.timeLeft >= 3000){
			Assets.sheepSound.play(.5f);
			lastPlayedSound = world.timeLeft;
		}
	}

	private void presentPaused() {
		if (!ScreenService.getInstance().existOverlay()) {
			Assets.font22.setColor(Color.WHITE);
			Assets.font22.draw(SheepHerder.batch, "--" + SheepHerder.TAP_OR_CLICK + " to resume--", 140, 400);
		}
	}

	public int calculateScore(int sheepCollected, long timeLeft) {
		if (sheepCollected == 0)
			return 0;
		return (int) (sheepCollected * 100 + timeLeft / 1000);
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
		Assets.font22.draw(SheepHerder.batch, ANIMATION_TEXT, posX, posY);
		Assets.font22.setScale(1);
		if (scoreAnimationTime >= END_TIME)
			playScoreAnimation = false;
	}

	@Override
	public void pause() {
		if (state == GAME_RUNNING)
			state = GAME_PAUSED;
	}

	@Override
	public void resume() {
		if (state == GAME_PAUSED)
			state = GAME_RUNNING;
	}

	@Override
	public void dispose() {

	}

}
