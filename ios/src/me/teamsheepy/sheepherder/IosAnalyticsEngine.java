package me.teamsheepy.sheepherder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpRequest;

import me.teamsheepy.sheepherder.utils.AnalyticsEngine;


public class IosAnalyticsEngine implements AnalyticsEngine {
	
	private int clientId;
	
	public IosAnalyticsEngine() {
		clientId = (int) (Math.random()*1000000D);
	}
	
	@Override
    public void initialize() {
    }

    @Override
    public void trackPageView(String screenName) {
    	HttpRequest request = defaultRequest();
    	StringBuilder content = defaultParams();
    	content.append("&t=screenview&an=SheepHerder&av=").append(SheepHerder.VERSION).append("&aid=").append("me.teamsheepy.sheepherder").append("&cd=").append(screenName);
    	request.setContent(content.toString());
    	Gdx.net.sendHttpRequest(request, null);
    }

    @Override
    public void trackEvent(String category, String subCategory, String label, int value) {
    	HttpRequest request = defaultRequest();
    	StringBuilder content = defaultParams();
    	content.append("&t=event&ec=").append(category).append("&ea=").append(subCategory).append("&el=").append(label).append("&ev="+value);
    	request.setContent(content.toString());
    	Gdx.net.sendHttpRequest(request, null);
    }

    @Override
    public void dispatch() {
    	//TODO persist requests for if no internet connection, atm everything is instantly sent.
    }
    
    private HttpRequest defaultRequest(){
    	HttpRequest request = new HttpRequest("POST");
    	request.setUrl("http://www.google-analytics.com/collect");
    	request.setHeader("User-Agent", "SheepHerder Desktop");
    	return request;
    }
    private StringBuilder defaultParams(){
    	return new StringBuilder("v=")
    	.append(SheepHerder.VERSION)
    	.append("&tid=")
    	.append(SheepHerder.TRACKER_ID)
    	.append("&cid=")
    	.append(clientId)
    	.append("&je=0");
    }
    
}