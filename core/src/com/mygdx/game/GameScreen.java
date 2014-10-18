package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends ScreenAdapter {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_OVER = 3;

	SheepHerder game;

	int state;
	Vector3 touchPoint;
	World world;
	WorldRenderer renderer;
	int lastScore;
	String scoreString;
	String timeString;

	public GameScreen (SheepHerder game) {
		this.game = game;

		state = GAME_READY;
		touchPoint = new Vector3();
		world = new World();
		renderer = new WorldRenderer(game.batcher, world);
		lastScore = 0;
		scoreString = "SHEEPS: 0";
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
		world.update(deltaTime);
		
		world.timeLeft -= deltaTime;
		timeString = String.format("TIME: %02d:%02d",  world.timeLeft/6000, (world.timeLeft%6000)/100);

		if (world.sheepsCollected != lastScore) {
			lastScore = world.sheepsCollected;
			scoreString = "SHEEPS: " + lastScore;
		}
		
		if (world.state == World.WORLD_STATE_GAME_OVER) {
			state = GAME_OVER;
			if (lastScore >= SavedData.highscore) {
				scoreString = "NEW HIGHSCORE: " + lastScore;
				SavedData.newHighscore(lastScore);

			} else
				scoreString = "SCORE: " + lastScore;
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
		Assets.font.draw(game.batcher, timeString, 480-170, 800 - 100);
		Assets.font.draw(game.batcher, scoreString, 480-170, 800 - 130);
	}

	private void presentPaused () {
		Assets.font.draw(game.batcher, "--click to resume--", 140, 400);
		Assets.font.draw(game.batcher, timeString, 480-170, 800 - 100);
		Assets.font.draw(game.batcher, scoreString, 480-170, 800 - 130);
	}

	private void presentGameOver () {
		//game.batcher.draw(Assets.gameOver, 160 - 160 / 2, 240 - 96 / 2, 160, 96);
		Assets.font.draw(game.batcher, "GAME OVER!", 20, 300);
		Assets.font.draw(game.batcher, "--click to continue--", 140, 200);
		float scoreWidth = Assets.font.getBounds(scoreString).width;
		Assets.font.draw(game.batcher, scoreString, 160 - scoreWidth / 2, 800 - 120);
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
