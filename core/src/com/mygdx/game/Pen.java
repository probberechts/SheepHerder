package com.mygdx.game;

import com.badlogic.gdx.math.Rectangle;


public class Pen extends GameObject {
	public static float PEN_WIDTH = 200;
	public static float PEN_HEIGHT = 225;

	public Pen (float x, float y) {
		super(x, y, PEN_WIDTH, PEN_HEIGHT);
		bounds.height -= 30; 
	}
	
	public boolean canEnter(Rectangle object) {
		Rectangle entryZone = new Rectangle(bounds.x + 100, bounds.y, 100, 5);
		return object.overlaps(entryZone);
	}
}
