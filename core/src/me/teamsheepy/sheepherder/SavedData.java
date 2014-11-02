package me.teamsheepy.sheepherder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SavedData {
	public static int highscore;
	public static int gamesPlayed;
	public static boolean questionnaireFilled;
	static Preferences prefs;

	public static void load () {
		prefs = Gdx.app.getPreferences("SheepHerder");
		highscore = prefs.getInteger("highscore", 0);
		gamesPlayed = prefs.getInteger("gamesPlayed", 0);
		questionnaireFilled = prefs.getBoolean("questionnaireFilled", false);
	}

	private static void save () {
		prefs.putInteger("highscore", highscore);
		prefs.putInteger("gamesPlayed", gamesPlayed);
		prefs.putBoolean("questionnaireFilled", questionnaireFilled);
		prefs.flush();
	}

	public static void newHighscore (int score) {
		if (score > highscore)
			highscore = score;
		save();
	}
	
	public static void filledInQuestionaire() {
		questionnaireFilled = true;
		save();
	}
	
	
	public static void addGamePlayed () {
		gamesPlayed++;
		save();
	}
	
}
