package be.teamsheepy.sheepherder.desktop;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import be.teamsheepy.sheepherder.SavedData;
import be.teamsheepy.sheepherder.SheepHerder;
import be.teamsheepy.sheepherder.utils.AnalyticsEngine;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import me.teamsheepy.sheepherder.desktop.AnalyticsRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DesktopAnalyticsEngine implements AnalyticsEngine {

	public DesktopAnalyticsEngine() {

	}

	@Override
	public void initialize() {
	}

	@Override
	public void startSession() {

	}

	@Override
	public void stopSession() {

	}

	@Override
	public void trackPageView(String screenName) {
	}

	@Override
	public void trackEvent(String category, String subCategory, String label,
			int value) {
	}

	@Override
	public void trackTimedEvent(String category, String subCategory, String label,
						   Long value) {
	}

	@Override
	public void dispatch() {
	}

}
