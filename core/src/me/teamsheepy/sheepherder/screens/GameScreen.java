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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends ScreenAdapter {
	static final int GAME_READY = 0;
	static final int GAME_RUNNING = 1;
	static final int GAME_PAUSED = 2;
	static final int GAME_OVER = 3;
	static final int NEW_BEST = 4;
	static final int QUESTIONNAIRE = 5;
	
	private TimeFormatter tfm = SheepHerder.timeFormatter;

	private SheepHerder game;

	private int state;
	private OrthographicCamera camera;
	private World world;
	private WorldRenderer renderer;
	private int lastScore;
	private String sheepString;
	private String timeString;	
	private int currentScore;
	private Vector3 touchPos;

	public GameScreen (SheepHerder game) {
		this.game = game;

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);
		world = (new WorldGenerator()).createWorld();
		renderer = new WorldRenderer(game.batcher, world);
		lastScore = 0;
		sheepString = "SHEEPS: 0";
		timeString = "TIME: 2:00";		
		if (!SavedData.questionnaireFilled 
				&& SavedData.gamesPlayed != 0 
				&& SavedData.gamesPlayed % 5 == 0)
			state = QUESTIONNAIRE;
		else
			state = GAME_READY;
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
		case NEW_BEST:
			updateGameOver();
			break;
		case QUESTIONNAIRE:
			updateQuestionnaire();
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
		currentScore = world.sheepsCollected == 0 ? 0 : (world.sheepsCollected*100+world.timeLeft/100);

		if (world.state == World.WORLD_STATE_GAME_OVER) {
			SavedData.addGamePlayed();
			int newScore = calculateScore(world.sheepsCollected, world.timeLeft);
			if (newScore > SavedData.highscore) {
				SavedData.newHighscore(newScore);
				state = NEW_BEST;
			} else {
				state = GAME_OVER;
			}
		}
	}

	private void updatePaused () {
		if (Gdx.input.justTouched()) {
			state = GAME_RUNNING;
		}
	}

	private void updateGameOver () {
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
	        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
	        camera.unproject(touchPos);
	        if (touchPos.x > 120 && touchPos.x < 120+240 
	        		&& touchPos.y > 285 && touchPos.y < 285+55) {
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
	        if (touchPos.x > 88 && touchPos.x < 88+95
	        		&& touchPos.y > 283 && touchPos.y < 283+55) {
	        	// answered yes
	    		Gdx.net.openURI("https://docs.google.com/forms/d/1eLUKnRGSiimqk4Mzr7ArOWwbGUMii8vZ7PagRqbDVe4/viewform?usp=send_form");
	    		state = GAME_READY;
	        } else if (touchPos.x > 203 && touchPos.x < 203+200
	        		&& touchPos.y > 283 && touchPos.y < 283+55) {
	        	// answered later
	    		state = GAME_READY;
	        }
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
			presentGameOver(false);
			break;
		case NEW_BEST:
			presentGameOver(true);
			break;
		case QUESTIONNAIRE:
			presentQuestionnaire();
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
		Assets.font24.draw(game.batcher, "--click to play--", 140, 400);
		if(SavedData.highscore <= 0) { //only show tutorial when there have been no sheep collected so far
			Assets.font24.draw(game.batcher, "Drag your finger across the screen", 4, 460);
			Assets.font24.draw(game.batcher, "sheep will run away from it.", 60, 430);
			Assets.font24.draw(game.batcher, "Guide the sheep to the pen.", 66, 400);
		}
	}

	private void presentRunning () {
		Assets.font24.draw(game.batcher, timeString, 26, 800 - 20);
		//Assets.font.draw(game.batcher, sheepString, 480-190, 800 - 50);
		Assets.font24.draw(game.batcher, "Score: "+currentScore, 26, 800-50);
		Assets.font24.draw(game.batcher, "Best score: "+SavedData.highscore, 26, 800-80);
		//debug
		//Assets.font.draw(game.batcher, Gdx.input.getX()+","+Gdx.input.getY(), 480-190, 800-110);
	}

	private void presentPaused () {
		Assets.font24.draw(game.batcher, "--click to resume--", 140, 400);
		Assets.font24.draw(game.batcher, timeString, 480-170, 800 - 20);
		Assets.font24.draw(game.batcher, sheepString, 480-170, 800 - 50);
	}
	
	private int calculateScore(int sheepsCollected, int timeLeft) {
		if (sheepsCollected == 0) return 0;
		return sheepsCollected * 100 + timeLeft/100;
	}

	private void presentGameOver (boolean newBest) {
		if (newBest) {
			game.batcher.draw(Assets.newbest, 50, 263, 380, 274);
		} else
			game.batcher.draw(Assets.gameover, 50, 263, 380, 274);
		String time = tfm.format(world.timeLeft/6000) + ":" + tfm.format((world.timeLeft%6000)/100);
		String score = "SCORE: " + world.sheepsCollected + " + " + time + " = " + calculateScore(world.sheepsCollected, world.timeLeft);
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
