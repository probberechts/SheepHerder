package com.mygdx.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import objects.GameObject;
import objects.Pen;
import objects.River;
import objects.Sheep;
import objects.Tree;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class World {

	public static final float WORLD_WIDTH = 480;
	public static final float WORLD_HEIGHT = 800;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_GAME_OVER = 1;
	public static final int WORLD_MARGIN = 20;
	public static final Color WORLD_MARGIN_COLOR = new Color(0x006400ff);

	public Pen pen;
	public final List<Sheep> sheeps;
	public final List<Tree> trees;
	public final List<River> rivers;

	public int timeLeft;
	public int sheepsCollected;
	public int state;

	public World() {
		this.pen = new Pen(5, 1);
		this.sheeps = new ArrayList<Sheep>();
		this.trees = new ArrayList<Tree>();
		this.rivers = new ArrayList<River>();
		generateLevel();

		this.timeLeft = 12000;
		this.sheepsCollected = 0;
		this.state = WORLD_STATE_RUNNING;
	}

	private void generateLevel () {
		// Variables
		int MIN_SHEEPS = 10;
		int MAX_SHEEPS = 10;
		int MIN_TREES = 1;
		int MAX_TREES = 3;
		int MIN_RIVERS = 1;
		int MAX_RIVERS = 1;
		int MIN_BRIDGES = 1;
		int MAX_BRIDGES = 3;
		
		//Create the pen
		pen = new Pen(20, 565);
		
		// Create some rivers
		int numRivers = rand(MIN_RIVERS, MAX_RIVERS);
		River river;
		for (int i = 0; i < numRivers; i++) {
			river = new River(0, rand(200, 400), rand(MIN_BRIDGES, MAX_BRIDGES));
			if (checkOverlapPen(river) || checkOverlapRiver(river)) i--;
			else rivers.add(river);
		}
		
		//Create some trees on random positions
		int numTrees = rand(MIN_TREES, MAX_TREES);
		Tree tree;
		for (int i = 0; i < numTrees; i++) {
			tree = new Tree(rand(0, (int) (WORLD_WIDTH - Tree.TREE_WIDTH/2)), rand(0, (int) (WORLD_HEIGHT - Tree.TREE_HEIGHT/2)));
			if (checkOverlapPen(tree) || checkOverlapRiver(tree)) i--;
			else trees.add(tree);
		}
		
		
		//Create some sheeps on random positions
		int numSheeps = rand(MIN_SHEEPS, MAX_SHEEPS);
		Sheep sheep;
		for (int i = 0; i < numSheeps; i++) {
			sheep = new Sheep(rand(50, (int) WORLD_WIDTH - 70), rand(50, 180));
			sheep.rotation = rand(0, 360);
			if (checkOverlapObject(sheep)) i--;
			else sheeps.add(sheep);
		}

	}

	private int rand(int min, int max) {
		Random generator = new Random();
		if (max == min)
			return min;
		return generator.nextInt(max - min) + min;
	}

	private boolean checkOverlapPen(GameObject object) {
		return pen.bounds.overlaps(object.bounds);
	}

	private boolean checkOverlapTree(GameObject object) {
		for (Tree tree : trees)
			if (tree.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapRiver(GameObject object) {
		for (River river : rivers)
			if (river.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapSheep(GameObject object) {
		for (Sheep sheep : sheeps)
			if (sheep.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapObject(GameObject object) {
		return (checkOverlapPen(object) || checkOverlapTree(object)
				|| checkOverlapRiver(object) || checkOverlapSheep(object));
	}

	public void update(float deltaTime) {
		updateSheeps(deltaTime);
		checkIfSheepsEscape();
		checkGameOver();
	}

	public void updateRotationSheeps(Vector3 touchPos) {
		for (Sheep sheep : sheeps) {
			if (sheep.position.dst2(touchPos.x, touchPos.y) < 10000) {
				float angle = new Vector2(touchPos.x, touchPos.y).sub(
						new Vector2(sheep.bounds.x + sheep.center.x,
								sheep.bounds.y + sheep.center.y)).angle();
				// double angle = Math.atan2(touchPos.y - sheep.position.y,
				// touchPos.x - sheep.position.x );
				// angle = angle * (180/Math.PI);
				sheep.rotation = ((int) angle + 180) % 360;
				sheep.velocity = new Vector2(100, 100);
				sheep.timeToIdle = 200;
			}
		}
	}

	public List<Rectangle> getCollisionAreas() {
		List<Rectangle> result = new LinkedList<Rectangle>();
		for (Tree tree : trees)
			result.addAll(tree.collisionAreas);
		for (River river : rivers)
			result.addAll(river.collisionAreas);
		result.addAll(pen.collisionAreas);
		return result;
	}
	
	private void updateSheeps (float deltaTime) {
		for (Sheep sheep : sheeps) {
			sheep.update(deltaTime, this);
			if (sheep.state != Sheep.SHEEP_STATE_CATCHED && pen.hasScored(sheep.bounds)) {
				sheep.state = Sheep.SHEEP_STATE_CATCHED;
				sheepsCollected++;
			} else if (sheep.state == Sheep.SHEEP_STATE_CATCHED && !pen.hasScored(sheep.bounds)) {
				sheep.state = Sheep.SHEEP_STATE_FREE;
				sheepsCollected--;
			}
		}
	}

	
	private void checkIfSheepsEscape () {
		for (Sheep sheep : sheeps) 
			if (sheep.bounds.x + sheep.bounds.width < 0 ||
					sheep.bounds.x > WORLD_WIDTH ||
					sheep.bounds.y + sheep.bounds.height < 0 ||
					sheep.bounds.y > WORLD_HEIGHT)
				sheep.state = Sheep.SHEEP_STATE_ESCAPED;
	}
	
	
	private boolean checkFreeSheepLeft () {
		for (Sheep sheep : sheeps)
			if (sheep.state == Sheep.SHEEP_STATE_FREE
					|| sheep.state == Sheep.SHEEP_STATE_DANGER)
				return true;
		return false;
	}

	private void checkGameOver() {
		if (timeLeft <= 0f || !checkFreeSheepLeft()) {
			state = WORLD_STATE_GAME_OVER;
		}

		for (Sheep sheep : sheeps)
			if (sheep.state != Sheep.SHEEP_STATE_CATCHED)
				return;
		state = WORLD_STATE_GAME_OVER;
	}
}
