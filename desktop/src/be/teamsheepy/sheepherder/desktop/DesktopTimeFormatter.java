package be.teamsheepy.sheepherder.desktop;

import be.teamsheepy.sheepherder.utils.TimeFormatter;

public class DesktopTimeFormatter implements TimeFormatter {

    @Override
    public String format(float value) {
    	java.text.DecimalFormat nft = new java.text.DecimalFormat("#00.###");
    	return nft.format(value);
    }
}
