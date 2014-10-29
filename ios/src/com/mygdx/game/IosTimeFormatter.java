package com.mygdx.game;

import com.mygdx.game.TimeFormatter;

public class IosTimeFormatter implements TimeFormatter {

    @Override
    public String format(float value) {
    	java.text.DecimalFormat nft = new java.text.DecimalFormat("#00.###");  
    	return nft.format(value);
    }
}
