package com.mygdx.game;

import java.util.Random;

public class Sheep extends DynamicGameObject {
	public static final int SHEEP_STATE_FREE = 0;
	public static final int SHEEP_STATE_CATCHED = 1;
	public static final int SHEEP_STATE_DANGER = 2;
	public static final float SHEEP_MOVE_VELOCITY = 20;
	public static final float SHEEP_WIDTH = 0.8f;
	public static final float SHEEP_HEIGHT = 0.8f;

	int state;

	public Sheep (float x, float y) {
		super(x, y, SHEEP_WIDTH, SHEEP_HEIGHT);
		Random rand = new Random();
		int rand1 = rand.nextInt(3) - 1;
		int rand2 = rand.nextInt(3) - 1;
		this.velocity.x = rand1 * 5;
		this.velocity.y = rand2 * 5;
	}

	public void update (float deltaTime) {
		position.add(velocity.x * deltaTime, velocity.y * deltaTime);
	}

	public void hitPin () {
		velocity.set(0, 0);
		state = SHEEP_STATE_CATCHED;
	}

	public void closeToScreenBorder () {
		state = SHEEP_STATE_DANGER;
	}

}
