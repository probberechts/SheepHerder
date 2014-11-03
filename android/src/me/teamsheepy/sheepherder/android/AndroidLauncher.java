package me.teamsheepy.sheepherder.android;

import me.teamsheepy.sheepherder.SheepHerder;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mygdx.game.android.R;

public class AndroidLauncher extends AndroidApplication {
    
	public static Tracker tracker;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(R.xml.tracker);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new SheepHerder(new AndroidGoogleServices(), new AndroidTimeFormatter()), config);
	}
	
}
