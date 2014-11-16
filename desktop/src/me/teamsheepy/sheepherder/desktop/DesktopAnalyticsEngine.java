package me.teamsheepy.sheepherder.desktop;

import me.teamsheepy.sheepherder.SavedData;
import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.utils.AnalyticsEngine;

import java.io.IOException;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;

public class DesktopAnalyticsEngine implements AnalyticsEngine {
	private final static Logger LOGGER = Logger.getLogger("Desktop");

	public DesktopAnalyticsEngine() {
		try {
			FileHandler fh = new FileHandler("sheepherderlogs.txt");
			LOGGER.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
			LOGGER.info("SheepHerder version "+SheepHerder.VERSION);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize() {
	}

	@Override
	public void trackPageView(String screenName) {
		HttpRequest request = defaultRequest();
		StringBuilder content = defaultParams();
		content.append("&t=screenview&an=SheepHerder&av=")
				.append(SheepHerder.VERSION).append("&aid=")
				.append("me.teamsheepy.sheepherder").append("&cd=")
				.append(screenName);
		request.setContent(content.toString());
		Gdx.net.sendHttpRequest(request, null);
		LOGGER.info("pageView "+screenName);
	}

	@Override
	public void trackEvent(String category, String subCategory, String label,
			int value) {
		HttpRequest request = defaultRequest();
		StringBuilder content = defaultParams();
		content.append("&t=event&ec=").append(category).append("&ea=")
				.append(subCategory).append("&el=").append(label)
				.append("&ev=" + value);
		request.setContent(content.toString());
		Gdx.net.sendHttpRequest(request, null);
		LOGGER.info("event: category="+category+";action="+subCategory+";label="+label+";value="+value );
	}

	@Override
	public void dispatch() {
		// TODO persist requests for if no internet connection, atm everything
		// is instantly sent.
	}

	private HttpRequest defaultRequest() {
		HttpRequest request = new HttpRequest("POST");
		request.setUrl("http://www.google-analytics.com/collect");
		request.setHeader("User-Agent", "SheepHerder Desktop");
		return request;
	}

	private StringBuilder defaultParams() {
		return new StringBuilder("v=").append(SheepHerder.VERSION)
				.append("&tid=").append(SheepHerder.TRACKER_ID).append("&cid=")
				.append(SavedData.getClientId()).append("&je=1");
	}
}
