package me.teamsheepy.sheepherder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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
	public static BitmapFont font;


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
				
		font = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
	}

}
