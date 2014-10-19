package com.mygdx.game;

import java.util.Random;

public class Sheep extends DynamicGameObject {
	public static final int SHEEP_STATE_FREE = 0;
	public static final int SHEEP_STATE_CATCHED = 1;
	public static final int SHEEP_STATE_DANGER = 2;
	public static final float SHEEP_MOVE_VELOCITY = 20;
	public static final float SHEEP_WIDTH = 55;
	public static final float SHEEP_HEIGHT = 50;

	int state;

	public Sheep (float x, float y) {
		super(x, y, SHEEP_WIDTH, SHEEP_HEIGHT);
		this.velocity.x = 5;
		this.velocity.y = 5;
	}

	public void update (float deltaTime) {
		//first get the direction the entity is pointed
		direction.x = (float) Math.cos(Math.toRadians(rotation));
		direction.y = (float) Math.sin(Math.toRadians(rotation));
		if (direction.len2() > 0) {
		    direction.nor();
		}
		//Then determine the new position based on speed, direction and deltaTime
		position.add(velocity.x * direction.x * deltaTime, velocity.y * direction.y * deltaTime);
	}

	public void hitPin () {
		velocity.set(0, 0);
		state = SHEEP_STATE_CATCHED;
	}

	public void closeToScreenBorder () {
		state = SHEEP_STATE_DANGER;
	}

}
