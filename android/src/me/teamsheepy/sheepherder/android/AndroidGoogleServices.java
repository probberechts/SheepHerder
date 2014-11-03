package me.teamsheepy.sheepherder.android;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import me.teamsheepy.sheepherder.utils.GoogleServices;

public class AndroidGoogleServices implements GoogleServices{

	@Override
	public void screenVisit(String pageName) {
		Tracker t = AndroidLauncher.tracker;
		t.setScreenName(pageName);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

}
