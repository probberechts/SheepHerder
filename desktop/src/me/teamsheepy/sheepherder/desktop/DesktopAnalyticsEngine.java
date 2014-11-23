package me.teamsheepy.sheepherder.desktop;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import me.teamsheepy.sheepherder.SavedData;
import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.utils.AnalyticsEngine;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DesktopAnalyticsEngine implements AnalyticsEngine {
	private final static Logger LOGGER = Logger.getLogger("Desktop");

	private static final String CACHE_BUSTER = "z";
	private static final String QUEUE_TIME = "qt";
	
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	private static int cacheBuster = 0;

	public DesktopAnalyticsEngine() {

	}

	@Override
	public void initialize() {
		try {
			FileHandler fh = new FileHandler("../../sheepherderlogs.log", true);
			LOGGER.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
			LOGGER.info("SheepHerder version " + SheepHerder.VERSION);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		SavedData.setClientId(UUID.randomUUID().toString());
	}

	public void startSession() {
		AnalyticsRequest req = defaultRequest();
		req.addParameter("t", "event");
		req.addParameter("sc", "start");
		executor.submit(new SendRequest(req));
		LOGGER.info("start session");
	}

	public void stopSession() {
		AnalyticsRequest req = defaultRequest();
		req.addParameter("t", "event");
		req.addParameter("sc", "stop");
		executor.submit(new SendRequest(req));
		LOGGER.info("stop session");
	}

	@Override
	public void trackPageView(String screenName) {
		screenName = screenName+"DTTEST";
		AnalyticsRequest req = defaultRequest();
		req.addParameter("av", "1");
		req.addParameter("an", "SheepHerder");
		req.addParameter("t", "appview");
		req.addParameter("dt", screenName);
		req.addParameter("cd", screenName);
		executor.submit(new SendRequest(req));
		LOGGER.info("pageView " + screenName);
	}

	@Override
	public void trackEvent(String category, String subCategory, String label,
			int value) {
		AnalyticsRequest req = defaultRequest();
		req.addParameter("an", "SheepHerder");
		req.addParameter("t", "event");
		req.addParameter("ec", category);
		req.addParameter("ea", subCategory);
		req.addParameter("el", label);
		req.addParameter("ev", "" + value);
		executor.submit(new SendRequest(req));
		LOGGER.info("event: category=" + category + ";action=" + subCategory
				+ ";label=" + label + ";value=" + value);
	}

	@Override
	public void trackTimedEvent(String category, String subCategory, String label,
						   Long value) {
		//TODO
	}

	@Override
	public void dispatch() {
	}

	private void sendRequest(AnalyticsRequest req) throws IOException,
			URISyntaxException {
		URIBuilder builder = new URIBuilder(new URI(
				"http://www.google-analytics.com/collect"));
		for (String a : req.getParams().keySet()) {
			builder.setParameter(a, req.getParams().get(a));
		}
		builder.setParameter(CACHE_BUSTER, "" + cacheBuster);
		builder.setParameter(QUEUE_TIME,
				"" + (System.currentTimeMillis() - req.getQueryTime()));
		cacheBuster++;
		URI uri = builder.build();
		//System.out.println(uri);
		HttpGet post = new HttpGet(uri);
		post.setHeader("User-Agent", "sheepherder.desktop/4");
		CloseableHttpClient client = HttpClients.createDefault();
		HttpResponse resp = client.execute(post);
		//System.out.println("send req "+resp.getStatusLine().getReasonPhrase());
	}

	private AnalyticsRequest defaultRequest() {
		AnalyticsRequest req = new AnalyticsRequest();
		req.addParameter("v", SheepHerder.VERSION);
		req.addParameter("tid", SheepHerder.DEBUG ? SheepHerder.DEBUG_TRACKER_ID : SheepHerder.TRACKER_ID);
		req.addParameter("cid", SavedData.clientId);
//		req.addParameter("sr", Gdx.app.getGraphics().getWidth() + "x"
//				+ Gdx.app.getGraphics().getHeight());
//		req.addParameter("ul", Locale.getDefault().getLanguage());
		req.addParameter("je", "1");
		return req;
	}
	
	class SendRequest implements Runnable{
		private AnalyticsRequest req;
		
		SendRequest(AnalyticsRequest req){
			this.req = req;
		}
		
		@Override
		public void run() {
			for(;;){
				try{
					sendRequest(req);
					break;
				}catch(Exception e){
					e.printStackTrace();//TODO remove
				}
			}
		}
	}
}
