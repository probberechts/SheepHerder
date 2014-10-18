package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;

public class World {

	public static final float WORLD_WIDTH = 480;
	public static final float WORLD_HEIGHT = 800;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_GAME_OVER = 1;

	public Pen pen;
	public final List<Sheep> sheeps;
	public final List<Tree> trees;

	public int timeLeft;
	public int sheepsCollected;
	public int state;

	public World () {
		this.pen = new Pen(5, 1);
		this.sheeps = new ArrayList<Sheep>();
		this.trees = new ArrayList<Tree>();
		generateLevel();

		this.timeLeft = 12000;
		this.sheepsCollected = 0;
		this.state = WORLD_STATE_RUNNING;
	}

	private void generateLevel () {
		//TODO
		pen = new Pen(20, 480);
		Sheep sheep1 = new Sheep(110, 110);
		Sheep sheep2 = new Sheep(10, 280);
		Sheep sheep3 = new Sheep(387, 356);
		Sheep sheep4 = new Sheep(56, 178);
		sheeps.add(sheep1);
		sheeps.add(sheep2);
		sheeps.add(sheep3);
		sheeps.add(sheep4);
		Tree tree = new Tree(200,100);
		trees.add(tree);
	}

	public void update (float deltaTime) {
		updateSheeps(deltaTime);
		checkCollisions();
		checkGameOver();
	}


	private void updateSheeps (float deltaTime) {
		for (Sheep sheep : sheeps)
			sheep.update(deltaTime);
	}

	private void checkCollisions () {
		//TODO
	}

	private void checkGameOver () {
		if (timeLeft <= 0f) {
			state = WORLD_STATE_GAME_OVER;
		}
		
		for (Sheep sheep: sheeps)
			if (sheep.state != Sheep.SHEEP_STATE_CATCHED)
				return;
		state = WORLD_STATE_GAME_OVER;
	}
}
