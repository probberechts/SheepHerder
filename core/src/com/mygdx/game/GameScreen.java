package com.mygdx.game;

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

	SheepHerder game;

	int state;
	OrthographicCamera camera;
	Vector3 touchPoint;
	World world;
	WorldRenderer renderer;
	int lastScore;
	String sheepString;
	String timeString;

	public GameScreen (SheepHerder game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		state = GAME_READY;
		touchPoint = new Vector3();
		world = new World();
		renderer = new WorldRenderer(game.batcher, world);
		lastScore = 0;
		sheepString = "SHEEPS: 0";
		timeString = "TIME: 2:00";
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
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
	        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	        camera.unproject(touchPos);
	        world.updateRotationSheeps(touchPos);
		}
		
		world.update(deltaTime);
		
		world.timeLeft -= deltaTime;
		timeString = String.format("TIME: %02d:%02d",  world.timeLeft/6000, (world.timeLeft%6000)/100);

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

		renderer.render();

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
	}

	private void presentReady () {
		Assets.font.draw(game.batcher, "--click to play--", 140, 400);
	}

	private void presentRunning () {
		Assets.font.draw(game.batcher, timeString, 480-170, 800 - 20);
		Assets.font.draw(game.batcher, sheepString, 480-170, 800 - 50);
	}

	private void presentPaused () {
		Assets.font.draw(game.batcher, "--click to resume--", 140, 400);
		Assets.font.draw(game.batcher, timeString, 480-170, 800 - 20);
		Assets.font.draw(game.batcher, sheepString, 480-170, 800 - 50);
	}

	private void presentGameOver () {
		//TODO: zoek een deftige formule
		int newScore = world.sheepsCollected + world.timeLeft;
		System.out.println(SavedData.highscore);
		if (newScore > SavedData.highscore) {
			SavedData.newHighscore(newScore);
			Assets.font.draw(game.batcher, "NEW HIGHSCORE!", 125, 450);
		} else
			Assets.font.draw(game.batcher, "GAME OVER!", 150, 450);
		String score = String.format("SCORE: %d + %02d:%02d = %d", world.sheepsCollected, world.timeLeft/6000, (world.timeLeft%6000)/100, newScore);
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
