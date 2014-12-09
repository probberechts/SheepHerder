package be.teamsheepy.sheepherder;

import be.teamsheepy.sheepherder.screens.GameScreen;
import be.teamsheepy.sheepherder.screens.ScreenService;
import be.teamsheepy.sheepherder.utils.AnalyticsEngine;
import be.teamsheepy.sheepherder.utils.TimeFormatter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SheepHerder extends Game {

	public static final boolean DEBUG = false;
	public static final String VERSION = "6";
	public static final String TRACKER_ID = "UA-56280744-3";
	public static final String DEBUG_TRACKER_ID = "UA-56280744-1";
	public static String TAP_OR_CLICK = "click";

	// used by all screens
	public static SpriteBatch batch;
	public static OrthographicCamera camera;

	public static TimeFormatter timeFormatter = null;
	public static AnalyticsEngine analytics = null;

	public SheepHerder(TimeFormatter timeFormatter, AnalyticsEngine analytics) {
		SheepHerder.timeFormatter = timeFormatter;
		SheepHerder.analytics = analytics;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 480, 800);

		SavedData.load();
		Assets.load();

		analytics.initialize();

		ScreenService screens = ScreenService.getInstance();
		screens.add(new GameScreen());
		setScreen(screens);
	}


}
