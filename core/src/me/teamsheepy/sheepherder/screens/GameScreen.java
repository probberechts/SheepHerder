package me.teamsheepy.sheepherder.screens;

import me.teamsheepy.sheepherder.Assets;
import me.teamsheepy.sheepherder.SavedData;
import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.SheepWorld;
import me.teamsheepy.sheepherder.WorldGenerator;
import me.teamsheepy.sheepherder.WorldRenderer;
import me.teamsheepy.sheepherder.objects.*;
import me.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
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
	private SheepWorld world;
	private WorldRenderer renderer;
	private int lastScore;
	private String sheepString;
	private String timeString;	
	private int currentScore;
	private Vector3 touchPos;

	public GameScreen (SheepHerder game) {
		SheepHerder.analytics.trackPageView("game");

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

		if (world.state == SheepWorld.WORLD_STATE_GAME_OVER) {
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
		if(SavedData.highscore <= 0) { //only show tutorial when there have been no sheep collected so far
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

	private void presentRunning () {
		Assets.font22.draw(game.batcher, timeString, 26, 800 - 20);
		//Assets.font.draw(game.batcher, sheepString, 480-190, 800 - 50);
		Assets.font22.draw(game.batcher, "Score: "+currentScore, 26, 800-50);
		Assets.font22.draw(game.batcher, "Best score: "+SavedData.highscore, 26, 800-80);
		
		//debug
		//Assets.font.draw(game.batcher, Gdx.input.getX()+","+Gdx.input.getY(), 480-190, 800-110);
		int i = 1;
		for(Sheep s : world.sheeps) {
			String xSpeed = s.body.getPosition().x + "";
			String ySpeed = s.body.getPosition().y + "";
			xSpeed = xSpeed.substring(0, xSpeed.indexOf('.'));
			ySpeed = ySpeed.substring(0, ySpeed.indexOf('.'));
			Assets.font22.draw(game.batcher, i + ": x:" + xSpeed + " y:" + ySpeed + " rot:" + s.rotation, 10, 10 + i*30);
			//Assets.font22.draw(game.batcher, "c: " + Math.cos(Math.toRadians(30)) + " s:" + Math.sin(Math.toRadians(30)), 10, 10 + i*30);
			i++;
		}
	}

	private void presentPaused () {
		Assets.font22.draw(game.batcher, "--click to resume--", 140, 400);
		Assets.font22.draw(game.batcher, timeString, 480-170, 800 - 20);
		Assets.font22.draw(game.batcher, sheepString, 480-170, 800 - 50);
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
