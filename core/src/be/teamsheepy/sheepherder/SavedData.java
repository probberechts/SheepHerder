package be.teamsheepy.sheepherder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SavedData {
	public static int highscore;
	public static int gamesPlayed;
	public static boolean questionnaireFilled;
	public static boolean neverShowSwipeSuggestion;
	public static String userName = "";
	public static String password = "";
	static Preferences prefs;

	public static void load() {
		prefs = Gdx.app.getPreferences("SheepHerder");
		if (SheepHerder.DEBUG) prefs.clear();
		highscore = prefs.getInteger("highscore", 0);
		gamesPlayed = prefs.getInteger("gamesPlayed", 0);
		questionnaireFilled = prefs.getBoolean("questionnaireFilled", false);
		neverShowSwipeSuggestion = prefs.getBoolean("neverShowSwipeSuggestion",
				false);
		userName = prefs.getString("userName");
		userName = prefs.getString("password");
	}

	private static void save() {
		prefs.putInteger("highscore", highscore);
		prefs.putInteger("gamesPlayed", gamesPlayed);
		prefs.putBoolean("questionnaireFilled", questionnaireFilled);
		prefs.putBoolean("neverShowSwipeSuggestion", neverShowSwipeSuggestion);
		prefs.putString("userName", userName);
		prefs.putString("password", password);
		prefs.flush();
	}

	public static void newHighscore(int score) {
		if (score > highscore)
			highscore = score;
		save();
	}

	public static void filledInQuestionaire() {
		questionnaireFilled = true;
		save();
	}

	public static void addGamePlayed() {
		gamesPlayed++;
		save();
	}

	public static void neverShowSwipeSuggestion() {
		neverShowSwipeSuggestion = true;
		save();
	}

	public static void setUser(String user, String pass) {
		userName = user;
		password = pass;
		save();
	}

	public static void logout() {
		password = "";
		save();
	}

}
