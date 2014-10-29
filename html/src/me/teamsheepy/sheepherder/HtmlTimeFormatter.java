package me.teamsheepy.sheepherder;

import me.teamsheepy.sheepherder.utils.TimeFormatter;

import com.google.gwt.i18n.client.NumberFormat;

public class HtmlTimeFormatter implements TimeFormatter {

    @Override
    public String format(float value) {
        NumberFormat format = NumberFormat.getFormat("#00.###");
        return format.format(value);
    }
}