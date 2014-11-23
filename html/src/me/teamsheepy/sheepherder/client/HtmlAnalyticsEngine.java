package me.teamsheepy.sheepherder.client;

import me.teamsheepy.sheepherder.utils.AnalyticsEngine;


public class HtmlAnalyticsEngine implements AnalyticsEngine {
	@Override
    public void initialize() {
    }

    @Override
    public native void trackPageView(String screenName) /*-{
    $wnd._gaq.push(['_trackPageview', screenName]);
	}-*/;

    @Override
    public native void trackEvent(String category, String subCategory, String label, int value) /*-{
    $wnd._gaq.push(['_trackEvent', category, subCategory, label, value]);
	}-*/;

    @Override
    public void dispatch() {
    }

	@Override
	public void startSession() {
	}

	@Override
	public void stopSession() {
	}
}
