package me.teamsheepy.sheepherder;

import me.teamsheepy.sheepherder.screens.GameScreen;
import me.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SheepHerder extends Game {
	// used by all screens
	public SpriteBatch batcher;
	
	public static TimeFormatter timeFormatter = null;

	public SheepHerder(TimeFormatter timeFormatter) {
		SheepHerder.timeFormatter = timeFormatter;
	}
	   
	@Override
	public void create () {
		batcher = new SpriteBatch();
		SavedData.load();
		Assets.load();
		setScreen(new GameScreen(this));
	}
	
	@Override
	public void render() {
		super.render();
	}
}
