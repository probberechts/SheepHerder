package com.mygdx.game;

import screens.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SheepHerder extends Game {
	// used by all screens
	public SpriteBatch batcher;
	
	public static TimeFormatter timeFormatter = null;

	public SheepHerder(TimeFormatter floatFormatter) {
		this.timeFormatter = floatFormatter;
	}
	   
	@Override
	public void create () {
		batcher = new SpriteBatch();
		Assets.load();
		setScreen(new GameScreen(this));
	}
	
	@Override
	public void render() {
		super.render();
	}
}
