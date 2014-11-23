package me.teamsheepy.sheepherder.desktop;

import java.util.HashMap;
import java.util.Map;

public class AnalyticsRequest {

	private Map<String, String> params = new HashMap<String, String>();
	private final long queryTime;

	public AnalyticsRequest(){
		queryTime = System.currentTimeMillis();
	}
	
	public Map<String, String> getParams() {
		return params;
	}

	public long getQueryTime() {
		return queryTime;
	}

	public void addParameter(String string, String s2) {
		params.put(string, s2);
	}
}
