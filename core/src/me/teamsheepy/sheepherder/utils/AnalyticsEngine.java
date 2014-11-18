package me.teamsheepy.sheepherder.utils;

/**
 * Created by probberechts on 08/11/14.
 */
public interface AnalyticsEngine {
    public void initialize();
    public void trackPageView(String path);
    public void trackEvent(String category, String action, String label, int value);
    public void dispatch();
}
