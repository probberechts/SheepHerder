package me.teamsheepy.sheepherder.android;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mygdx.game.android.R;

import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.SheepWorld;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import me.teamsheepy.sheepherder.utils.AnalyticsEngine;

public class AndroidLauncher extends AndroidApplication implements AnalyticsEngine {

	Tracker t;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		SheepWorld.WORLD_MARGIN = 40;
		initialize(new SheepHerder(new AndroidTimeFormatter(), this), config);

		t = GoogleAnalytics.getInstance(this).newTracker(R.xml.tracker);
	}

	@Override
	public void onStart() {
		super.onStart();
		startSession();
	}

	@Override
	public void onStop() {
		super.onStop();
		stopSession();
	}

	@Override
	public void initialize() {

	}

	@Override
	public void trackPageView(String screenName) {
		// Set screen name.
		t.setScreenName(screenName);

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	@Override
	public void trackEvent(String category, String subCategory, String label, int value) {
		// Build and send an Event.
		t.send(new HitBuilders.EventBuilder()
				.setCategory(category)
				.setAction(subCategory)
				.setLabel(label)
				.setValue(value)
				.build());
	}

	@Override
	public void dispatch() {
		GoogleAnalytics.getInstance(this).dispatchLocalHits();
	}

	@Override
	public void startSession() {
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	public void stopSession() {
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}
}
