package me.teamsheepy.sheepherder.desktop;

import me.teamsheepy.sheepherder.utils.AnalyticsEngine;

import java.util.logging.Logger;

public class DesktopAnalyticsEngine implements AnalyticsEngine {
    private final static Logger LOGGER = Logger.getLogger("Desktop");

    @Override
    public void initialize() {
        LOGGER.info("initialize analytics");
    }

    @Override
    public void trackPageView(String screenName) {
        LOGGER.info("track " + screenName + " view");

    }

    @Override
    public void trackEvent(String category, String subCategory, String label, int value) {
        LOGGER.info("track event: " + category + " > " + subCategory + " > " + label + " > " + value);
    }

    @Override
    public void dispatch() {
        LOGGER.info("dispatch analytics data");
    }
}
