package me.teamsheepy.sheepherder;

import me.teamsheepy.sheepherder.screens.GameScreen;
import me.teamsheepy.sheepherder.utils.AnalyticsEngine;
import me.teamsheepy.sheepherder.utils.SwipeDetector;
import me.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SheepHerder extends Game {
	
	public static final String VERSION = "3";
	public static final String TRACKER_ID = "UA-56280744-1";
	
	// used by all screens
	public SpriteBatch batcher;
	
	public static TimeFormatter timeFormatter = null;
	public static AnalyticsEngine analytics = null;

	public SheepHerder(TimeFormatter timeFormatter, AnalyticsEngine analytics) {
		SheepHerder.timeFormatter = timeFormatter;
		SheepHerder.analytics = analytics;
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
