package me.teamsheepy.sheepherder;

import me.teamsheepy.sheepherder.utils.TimeFormatter;

public class IosTimeFormatter implements TimeFormatter {

    @Override
    public String format(float value) {
    	java.text.DecimalFormat nft = new java.text.DecimalFormat("#00.###");
    	return nft.format(value);
    }
}
