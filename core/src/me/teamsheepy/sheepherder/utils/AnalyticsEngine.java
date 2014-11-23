package me.teamsheepy.sheepherder.utils;

public interface AnalyticsEngine {
    public void initialize();
    public void trackPageView(String path);
    public void trackEvent(String category, String action, String label, int value);
    public void trackTimedEvent(String category, String action, String label, long value);
    public void dispatch();
}
