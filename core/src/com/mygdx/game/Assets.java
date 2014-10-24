package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets {

	public static Texture sheep;
	public static Texture pen;
	public static Texture tree;
	public static Texture river;
	public static Texture bridge;
	public static Texture gameOver;
	public static Texture alert;
	public static Texture touchmarker;
	public static BitmapFont font;


	public static Texture loadTexture (String file) {
		return new Texture(Gdx.files.internal(file));
	}

	public static void load () {
		sheep = loadTexture("sheep.png");
		pen = loadTexture("pin.png");
		tree = loadTexture("tree.png");
		river = loadTexture("river.png");
		bridge = loadTexture("bridge.png");
		alert = loadTexture("alert.png");
		touchmarker = loadTexture("touchmarker.png");
		
		//gameOver = loadTexture("tree.png");
		
		font = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
	}

}
