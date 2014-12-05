package be.teamsheepy.sheepherder.android;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import be.teamsheepy.sheepherder.SheepHerder;
import be.teamsheepy.sheepherder.SheepWorld;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import be.teamsheepy.sheepherder.utils.AnalyticsEngine;

import java.util.HashMap;

public class AndroidLauncher extends AndroidApplication implements AnalyticsEngine {

	Tracker t;

	public enum TrackerName {
		APP_TRACKER, // Tracker used in production.
		DEBUG_TRACKER, // Tracker used for debugging.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.DEBUG_TRACKER) ? analytics.newTracker(R.xml.debug_tracker)
					: analytics.newTracker(R.xml.tracker);
			mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.hideStatusBar = true;
		SheepWorld.WORLD_MARGIN = 40;
		initialize(new SheepHerder(new AndroidTimeFormatter(), this), config);

		SheepHerder.TAP_OR_CLICK = "tap";
		if (SheepHerder.DEBUG)
			t = getTracker(TrackerName.DEBUG_TRACKER);
		else
			t = getTracker(TrackerName.APP_TRACKER);
	}

	@Override
	public void onStart() {
		super.onStart();
		startSession();
	}

	@Override
	public void onStop() {
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		dispatch();
		super.onStop();
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
	public void trackTimedEvent(String category, String subCategory, String label, Long value) {
		// Build and send an Event.
		t.send(new HitBuilders.TimingBuilder()
				.setCategory(category)
				.setValue(value)
				.setVariable(subCategory)
				.setLabel(label)
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
