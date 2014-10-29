package me.teamsheepy.sheepherder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class SavedData {
	public static int highscore = 0;
	public final static String file = ".sheepherder";

	public static void load () {
		try {
			FileHandle filehandle = Gdx.files.external(file);
			
			String[] strings = filehandle.readString().split("\n");
			
			highscore = Integer.parseInt(strings[0]);
			
		} catch (Throwable e) {
			// without highscores is fine too ...
		}
	}

	public static void save () {
		try {
			FileHandle filehandle = Gdx.files.external(file);
			
			filehandle.writeString(Integer.toString(highscore)+"\n", false);
		} catch (Throwable e) {
			// highscore didn't save
		}
	}

	public static void newHighscore (int score) {
		if (score > highscore)
			highscore = score;
		save();
	}
}
