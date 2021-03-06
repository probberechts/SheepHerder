package be.teamsheepy.sheepherder;

import be.teamsheepy.sheepherder.utils.AnalyticsEngine;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.bindings.googleanalytics.GAIDictionaryBuilder;
import org.robovm.bindings.googleanalytics.GAIFields;


public class IosAnalyticsEngine implements AnalyticsEngine {

    private GAITrackerImpl tracker;

    @Override
    public void initialize() {
        GAI.getSharedInstance().setTrackUncaughtExceptions(true);
        GAI.getSharedInstance().setDispatchInterval(20);

        if (SheepHerder.DEBUG)
            tracker = GAI.getSharedInstance().getTracker(SheepHerder.DEBUG_TRACKER_ID);
        else
            tracker = GAI.getSharedInstance().getTracker(SheepHerder.TRACKER_ID);

        GAI.getSharedInstance().setDefaultTracker(tracker);
    }

    @Override
    public void trackEvent(String category, String action, String label, int value) {
        tracker.send(GAIDictionaryBuilder.createEvent(category, action, label, NSNumber.valueOf(value)).build());
    }

    @Override
    public void trackPageView(String name) {
        tracker.set(GAIFields.kGAIScreenName, name);
        tracker.send(GAIDictionaryBuilder.createAppView().build());
    }

    @Override
    public void trackTimedEvent(String category, String name, String label, Long timeInMilliseconds) {
        tracker.send(GAIDictionaryBuilder.createTiming(category, NSNumber.valueOf(timeInMilliseconds), name, label).build());
    }

    @Override
    public void dispatch() {
        GAI.getSharedInstance().dispatch();
    }

	@Override
	public void startSession() {
	}

	@Override
	public void stopSession() {
	}

}
