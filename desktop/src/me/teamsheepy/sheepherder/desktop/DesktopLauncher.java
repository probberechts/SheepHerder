package me.teamsheepy.sheepherder.desktop;

import me.teamsheepy.sheepherder.SheepHerder;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
	public static void main (String[] arg) {
		// Automatic texture packing
		Settings settings = new Settings();
		settings.paddingX = 2;
	    settings.paddingY = 2;
	    settings.minWidth = 32;
	    settings.minHeight = 32;
	    settings.maxHeight = 1024;
	    settings.maxWidth = 1024;
	    settings.stripWhitespaceX = true;
	    settings.stripWhitespaceY = true;
	    settings.filterMag = TextureFilter.Nearest;
	    settings.filterMin = TextureFilter.Nearest;
	    settings.flattenPaths = true;
        TexturePacker.process(settings, "../graphics/png", "../android/assets", "game");

		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Sheep Herder";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new SheepHerder(new DesktopTimeFormatter()), config);
	}
}
