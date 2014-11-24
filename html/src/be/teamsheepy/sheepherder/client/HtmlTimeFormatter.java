package be.teamsheepy.sheepherder.client;

import be.teamsheepy.sheepherder.utils.TimeFormatter;

import com.google.gwt.i18n.client.NumberFormat;

public class HtmlTimeFormatter implements TimeFormatter {

    @Override
    public String format(float value) {
        NumberFormat format = NumberFormat.getFormat("#00.###");
        return format.format(value);
    }
}