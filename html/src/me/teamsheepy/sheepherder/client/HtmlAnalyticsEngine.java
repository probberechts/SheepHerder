package me.teamsheepy.sheepherder.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import me.teamsheepy.sheepherder.utils.AnalyticsEngine;


public class HtmlAnalyticsEngine implements AnalyticsEngine {
	@Override
    public void initialize() {
        Document doc = Document.get();
        ScriptElement script = doc.createScriptElement();
        script.setSrc("https://ssl.google-analytics.com/ga.js");
        script.setType("text/javascript");
        script.setLang("javascript");
        doc.getBody().appendChild(script);
    }

    public native void setGaVars() /*-{
        $wnd._gaq = $wnd._gaq || [];
        $wnd._gaq.push(['_setAccount', 'UA-56280744-3']);
    }-*/;

    public native void setGaVarsDebug() /*-{
        $wnd._gaq = $wnd._gaq || [];
        $wnd._gaq.push(['_setAccount', 'UA-56280744-1']);
        $wnd._gaq.push(['_setDomainName', 'none']);
    }-*/;

    @Override
    public native void trackPageView(String screenName) /*-{
        try {
            $wnd._gaq.push(['_trackPageview', screenName]);
        } catch (e) {
            console.log("AnalyticsError: " + e)
        }
	}-*/;

    @Override
    public native void trackEvent(String category, String subCategory, String label, int value) /*-{
        try {
            $wnd._gaq.push(['_trackEvent', category, subCategory, label, value]);
        } catch (e) {
            console.log("AnalyticsError: " + e)
        }
	}-*/;

    public void trackTimedEvent(String category, String subCategory, String label, Long value) {
        // JSNI doesn't accept long values
        trackTimedEvent_hack(category, subCategory, label, value.doubleValue());
    }

    public native void trackTimedEvent_hack(String category, String subCategory, String label, double value) /*-{
        try {
            $wnd._gaq.push(['_trackTiming', category, subCategory, value, label, 100]);
        } catch (e) {
            console.log("AnalyticsError: " + e)
        }
	}-*/;

    @Override
    public void dispatch() {
        //ga.js dispatches immediately
    }
}
