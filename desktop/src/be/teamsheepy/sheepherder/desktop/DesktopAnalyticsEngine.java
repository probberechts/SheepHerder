package be.teamsheepy.sheepherder.desktop;

import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.SheepHerder;
import be.teamsheepy.sheepherder.utils.AnalyticsEngine;

import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;

public class DesktopAnalyticsEngine implements AnalyticsEngine {
	private final static Logger LOGGER = Logger.getLogger("Desktop");

	private String clientId;
	
	public DesktopAnalyticsEngine() {
		try {
			FileHandler fh = new FileHandler("../../sheepherderlogs.log", true);
			LOGGER.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
			LOGGER.info("SheepHerder version "+SheepHerder.VERSION);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize() {
		SavedData.setClientId(UUID.randomUUID().toString());
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
	public void trackTimedEvent(String category, String subCategory, String label,
						   Long value) {
		//TODO
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
		String tracker_id = SheepHerder.TRACKER_ID;
		if (SheepHerder.DEBUG)
			tracker_id = SheepHerder.DEBUG_TRACKER_ID;
		return new StringBuilder("v=").append(SheepHerder.VERSION)
				.append("&tid=").append(tracker_id).append("&cid=")
				.append(clientId).append("&je=1");
	}
}
