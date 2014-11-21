package me.teamsheepy.sheepherder.client;

import me.teamsheepy.sheepherder.HtmlTimeFormatter;
import me.teamsheepy.sheepherder.SheepHerder;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import me.teamsheepy.sheepherder.utils.AnalyticsEngine;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(480, 800);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                HtmlAnalyticsEngine analytics = new HtmlAnalyticsEngine();
                analytics.setGaVars();
                return new SheepHerder(new HtmlTimeFormatter(), analytics);
        }
}