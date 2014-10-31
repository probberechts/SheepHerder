package me.teamsheepy.sheepherder.desktop;

import me.teamsheepy.sheepherder.SheepHerder;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Sheep Herder";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new SheepHerder(new DesktopTimeFormatter()), config);
	}
}