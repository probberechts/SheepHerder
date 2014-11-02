package me.teamsheepy.sheepherder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

	public static TextureRegion sheep;
	public static TextureRegion pen;
	public static TextureRegion tree;
	public static Animation sheepUnderTreeAnimation;
	public static TextureRegion river;
	public static TextureRegion bridge;
	public static TextureRegion alert;
	public static TextureRegion touchmarker;
	public static Texture gameover;
	public static Texture newbest;
	public static Texture questionnaire;
	public static BitmapFont font32white;
	public static BitmapFont font32black;
	public static BitmapFont font28black;


	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public static void load () {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack.atlas"));
        
        TextureRegion[] animationFrames = new TextureRegion[5];
        for (int i = 1; i < 6; i++)
            animationFrames[i-1] = atlas.findRegion("tree" + i);
		sheepUnderTreeAnimation = new Animation( 1/5f, animationFrames );

		sheep = atlas.findRegion("sheep");
		pen = atlas.findRegion("pen");
		tree = atlas.findRegion("tree");
		river = atlas.findRegion("river");
		bridge = atlas.findRegion("bridge");
		alert = atlas.findRegion("alert");
		touchmarker = atlas.findRegion("touchmarker");
		gameover = loadTexture("gameover.png");
		gameover.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		newbest = loadTexture("newbest.png");
		newbest.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		questionnaire = loadTexture("questionnaire.png");
		questionnaire.setFilter(TextureFilter.Linear, TextureFilter.Linear);
				
		font32white = new BitmapFont(Gdx.files.internal("font32white.fnt"), Gdx.files.internal("font32white.png"), false);
		font32black = new BitmapFont(Gdx.files.internal("font32black.fnt"), Gdx.files.internal("font32black.png"), false);
		font28black = new BitmapFont(Gdx.files.internal("font28black.fnt"), Gdx.files.internal("font28black.png"), false);
	}

}
