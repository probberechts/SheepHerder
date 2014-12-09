package be.teamsheepy.sheepherder.desktop;

import be.teamsheepy.sheepherder.SheepHerder;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

import java.util.logging.Logger;

public class DesktopLauncher {
	private final static Logger LOGGER = Logger.getLogger("Desktop");

	public static void main (String[] arg) {
		// Automatic texture packing
//		Settings settings = new Settings();
//		settings.paddingX = 2;
//	    settings.paddingY = 2;
//	    settings.minWidth = 32;
//	    settings.minHeight = 32;
//	    settings.maxHeight = 1024;
//	    settings.maxWidth = 1024;
//	    settings.stripWhitespaceX = true;
//	    settings.stripWhitespaceY = true;
//	    settings.filterMag = TextureFilter.Nearest;
//	    settings.filterMin = TextureFilter.Nearest;
//	    settings.flattenPaths = true;
//      	TexturePacker.process(settings, "/users/probberechts/Development/SheepHerder/graphics/png", "/users/probberechts/Development/SheepHerder/android/assets", "game");
        //TexturePacker.process(settings, "D:/Schakeljaar Master TI/HCI/DEV/SheepHerder/graphics/png", "D:/Schakeljaar Master TI/HCI/DEV/SheepHerder/android/assets", "game");

		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Sheep Herder";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new SheepHerder(new DesktopTimeFormatter(), new DesktopAnalyticsEngine()), config);
	}
}
