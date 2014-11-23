package me.teamsheepy.sheepherder;

import me.teamsheepy.sheepherder.screens.GameScreen;
import me.teamsheepy.sheepherder.utils.AnalyticsEngine;
import me.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SheepHerder extends Game {

	public static final boolean DEBUG = true;
	public static final String VERSION = "4";
	public static final String TRACKER_ID = "UA-56280744-3";
	public static final String DEBUG_TRACKER_ID = "UA-56280744-1";
	public static String TAP_OR_CLICK = "click";

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
		analytics.initialize();
		setScreen(new GameScreen(this));
	}
}
