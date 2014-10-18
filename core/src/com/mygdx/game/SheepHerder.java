package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SheepHerder extends Game {
	// used by all screens
	public SpriteBatch batcher;
	
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
