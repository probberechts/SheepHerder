package be.teamsheepy.sheepherder.utils;

public interface AnalyticsEngine {
    public void initialize();
    public void startSession();
    public void stopSession();
    public void trackPageView(String path);
    public void trackEvent(String category, String action, String label, int value);
    public void trackTimedEvent(String category, String action, String label, Long value);
    public void dispatch();
}
