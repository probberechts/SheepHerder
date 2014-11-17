package me.teamsheepy.sheepherder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {

	public static TextureRegion sheep;
	public static Animation sheepAnimation;
	public static TextureRegion pen;
	public static TextureRegion tree;
	public static Animation sheepUnderTreeAnimation;
	public static TextureRegion river;
	public static TextureRegion bridge;
	public static TextureRegion alert;
	public static TextureRegion clock;
	public static TextureRegion time;
	public static TextureRegion score;
	public static TextureRegion retry;
	public static TextureRegion touchmarker;
	public static AtlasRegion gameover;
	public static AtlasRegion newbest;
	public static AtlasRegion questionnaire;
	public static BitmapFont font28;
	public static BitmapFont font24;
	public static BitmapFont font22;
	public static TextureRegion emptyCheckBox;
	public static TextureRegion fullCheckBox;


	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public static void load () {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("game.atlas"));
        
        TextureRegion[] animationFrames = new TextureRegion[5];
        for (int i = 1; i < 6; i++)
            animationFrames[i-1] = atlas.findRegion("tree" + i);
		sheepUnderTreeAnimation = new Animation( 1/5f, animationFrames );

		animationFrames = new TextureRegion[8];
		for (int i = 1; i < 9; i++)
			animationFrames[i-1] = atlas.findRegion("sheep_walk" + i);
		sheepAnimation = new Animation( 1/8f, animationFrames );

		sheep = atlas.findRegion("sheep");
		pen = atlas.findRegion("pen");
		tree = atlas.findRegion("tree");
		river = atlas.findRegion("river");
		bridge = atlas.findRegion("bridge");
		alert = atlas.findRegion("alert");
		clock = atlas.findRegion("clock");
		clock.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		time = atlas.findRegion("time");
		time.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		score = atlas.findRegion("score");
		retry = atlas.findRegion("retry");
		retry.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		touchmarker = atlas.findRegion("touchmarker");
		touchmarker.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		gameover = atlas.findRegion("gameover");
		gameover.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		newbest = atlas.findRegion("newbest");
		newbest.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		questionnaire = atlas.findRegion("questionnaire");
		questionnaire.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		emptyCheckBox = atlas.findRegion("tip_notap_v2");
		emptyCheckBox.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		fullCheckBox = atlas.findRegion("tip_notap_v2_checked");
		fullCheckBox.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
				
		font28 = new BitmapFont(Gdx.files.internal("ArchitectsDaughter-28.fnt"), atlas.findRegion("font"), false);
		font24 = new BitmapFont(Gdx.files.internal("ArchitectsDaughter-24.fnt"), atlas.findRegion("font"), false);
		font22 = new BitmapFont(Gdx.files.internal("ArchitectsDaughter-22.fnt"), atlas.findRegion("font"), false);
	}

}
