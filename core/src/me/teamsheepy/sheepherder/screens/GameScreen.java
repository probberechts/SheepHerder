package me.teamsheepy.sheepherder.screens;

import me.teamsheepy.sheepherder.Assets;
import me.teamsheepy.sheepherder.SavedData;
import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.World;
import me.teamsheepy.sheepherder.WorldGenerator;
import me.teamsheepy.sheepherder.WorldRenderer;
import me.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends ScreenAdapter {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_OVER = 3;
	
	private TimeFormatter tfm = SheepHerder.timeFormatter;

	private SheepHerder game;

	private int state;
	private OrthographicCamera camera;
	private World world;
	private WorldRenderer renderer;
	private int lastScore;
	private String sheepString;
	private String timeString;
	private Vector3 touchPos;

	public GameScreen (SheepHerder game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		state = GAME_READY;
		world = (new WorldGenerator()).createWorld();
		renderer = new WorldRenderer(game.batcher, world);
		lastScore = 0;
		sheepString = "SHEEPS: 0";
		timeString = "TIME: 2:00";
		touchPos = new Vector3(-500, -500, 0); //hide offscreen at start
	}

	public void update (float deltaTime) {
		if (deltaTime > 0.1f) deltaTime = 0.1f;

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
			updateGameOver();
			break;
		}
	}

	private void updateReady () {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
		}
	}

	private void updateRunning (float deltaTime) {
		if (Gdx.input.isTouched()) {
	        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	        camera.unproject(touchPos);
	        world.updateRotationSheeps(touchPos);
		}
		
		world.update(deltaTime);
		world.timeLeft -= deltaTime;
		timeString = "TIME: " + tfm.format(world.timeLeft/6000) + ":" + tfm.format((world.timeLeft%6000)/100);

		if (world.sheepsCollected != lastScore) {
			lastScore = world.sheepsCollected;
			sheepString = "SHEEPS: " + lastScore;
		}
		
		if (world.state == World.WORLD_STATE_GAME_OVER) {
			state = GAME_OVER;
		}
	}

	private void updatePaused () {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
		}
	}

	private void updateGameOver () {
		if (Gdx.input.justTouched()) {
			game.setScreen(new GameScreen(game));
		}
	}

	public void draw () {
		GL20 gl = Gdx.gl;
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.render(touchPos);

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
			break;
		case GAME_PAUSED:
			presentPaused();
			break;
		case GAME_OVER:
			presentGameOver();
			break;
		}
		game.batcher.end();
		
		// DEBUG: draw bounding boxes of objects
//		ShapeRenderer sr = new ShapeRenderer();
//		sr.setProjectionMatrix(camera.combined);
//		sr.begin(ShapeType.Line);
//		sr.setColor(1, 1, 0, 1);
//		for (Sheep sheep : world.sheeps)
//			sr.rect(sheep.bounds.x, sheep.bounds.y, sheep.bounds.width, sheep.bounds.height);
//		sr.setColor(1, 0, 0, 1);
//		for (River river : world.rivers) {
//			for (Rectangle col : river.collisionAreas)
//				sr.rect(col.x, col.y, col.width, col.height);
//			sr.setColor(0, 1, 0, 0);
//			for (Bridge bridge: river.bridges)
//				sr.rect(bridge.bounds.x, bridge.bounds.y, bridge.bounds.width, bridge.bounds.height);
//		}
//		sr.setColor(0, 1, 0, 1);
//		for (Tree tree : world.trees)
//			sr.rect(tree.bounds.x, tree.bounds.y, tree.bounds.width, tree.bounds.height);
//		sr.setColor(1, 0, 0, 1);
//		for (Rectangle col : world.pen.collisionAreas)
//			sr.rect(col.x, col.y, col.width, col.height);
//		sr.end();
	}

	private void presentReady () {
		Assets.font.draw(game.batcher, "--Press to play--", 140, 520);
		if(SavedData.highscore <= 0) { //only show tutorial when there have been no sheep collected so far
			Assets.font.draw(game.batcher, "Drag your finger across the screen", 4, 460);
			Assets.font.draw(game.batcher, "sheep will run away from it.", 60, 430);
			Assets.font.draw(game.batcher, "Guide the sheep to the pen.", 66, 400);
		}
	}

	private void presentRunning () {
		Assets.font.draw(game.batcher, timeString, 480-190, 800 - 20);
		Assets.font.draw(game.batcher, sheepString, 480-190, 800 - 50);
	}

	private void presentPaused () {
		Assets.font.draw(game.batcher, "--click to resume--", 140, 400);
		Assets.font.draw(game.batcher, timeString, 480-170, 800 - 20);
		Assets.font.draw(game.batcher, sheepString, 480-170, 800 - 50);
	}
	
	private int calculateScore(int sheepsCollected, int timeLeft) {
		if (sheepsCollected == 0) return 0;
		return sheepsCollected * 100 + timeLeft/100;
	}

	private void presentGameOver () {
		int newScore = calculateScore(world.sheepsCollected, world.timeLeft);
		if (newScore > SavedData.highscore) {
			SavedData.newHighscore(newScore);
			Assets.font.draw(game.batcher, "NEW HIGHSCORE!", 125, 450);
		} else
			Assets.font.draw(game.batcher, "GAME OVER!", 150, 450);
		String time = tfm.format(world.timeLeft/6000) + ":" + tfm.format((world.timeLeft%6000)/100);
		String score = "SCORE: " + world.sheepsCollected + " + " + time + " = " + newScore;
		float scoreWidth = Assets.font.getBounds(score).width;
		Assets.font.draw(game.batcher, score, 240 - scoreWidth / 2, 400);
		Assets.font.draw(game.batcher, "BEST: " + SavedData.highscore, 240 - scoreWidth / 2, 350);
		Assets.font.draw(game.batcher, "--click to continue--", 120, 300);
	}

	@Override
	public void render (float delta) {
		update(delta);
		draw();
	}

	@Override
	public void pause () {
		if (state == GAME_RUNNING) state = GAME_PAUSED;
	}
}
