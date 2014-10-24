package com.mygdx.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import objects.Pen;
import objects.River;
import objects.Sheep;
import objects.Tree;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class World {

	public static final float WORLD_WIDTH = 480;
	public static final float WORLD_HEIGHT = 800;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_GAME_OVER = 1;

	public Pen pen;
	public final List<Sheep> sheeps;
	public final List<Tree> trees;
	public final List<River> rivers;

	public int timeLeft;
	public int sheepsCollected;
	public int state;

	public World () {
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
		//TODO: genereer random level
		pen = new Pen(20, 565);
		Random rand = new Random();
		for(int i=0;i<3;++i) {
			for(int j=0;j<3;++j) {
				int randX = rand.nextInt()%40 - 20;
				int randY = rand.nextInt()%40 - 20;
				Sheep sheep1 = new Sheep(200 + i*50 + randX, 350 - j*50 + randY);
				sheeps.add(sheep1);
			}
		}
		Tree tree = new Tree(200,100);
		trees.add(tree);
		//TODO: rivier in een bepaalde draaihoek platsen werkt nog niet
		River river = new River(0,400,0,2);
		rivers.add(river);
	}

	public void update (float deltaTime) {
		updateSheeps(deltaTime);
		checkCollisions();
		checkGameOver();
	}
	
	public void updateRotationSheeps(Vector3 touchPos) {
		for (Sheep sheep : sheeps) {
			if (sheep.position.dst2(touchPos.x, touchPos.y) < 10000) {
				double angle = Math.atan2(touchPos.y - sheep.position.y, touchPos.x - sheep.position.x );
				angle = angle * (180/Math.PI);
				sheep.rotation = ((int) angle + 180)  % 360; 
				sheep.velocity = new Vector2(50, 50);
				sheep.timeToIdle = 200;
			}
		}
	}


	private void updateSheeps (float deltaTime) {
		for (Sheep sheep : sheeps)
			sheep.update(deltaTime);
	}

	private void checkCollisions () {
		checkCollisionWorld();
		checkCollisionSheep();
		checkCollisionPen();
		checkCollisionRiver();
		//checkCollisionTree();
	}
	
	private void checkCollisionWorld () {
		for (Sheep sheep : sheeps) 
			if (sheep.bounds.x + sheep.bounds.width < 0 ||
					sheep.bounds.x > WORLD_WIDTH ||
					sheep.bounds.y + sheep.bounds.height < 0 ||
					sheep.bounds.y > WORLD_HEIGHT)
				sheep.state = Sheep.SHEEP_STATE_ESCAPED;
	}
	
	private void checkCollisionSheep () {
		//TODO: schapen kunnen niet onder of boven elkaar lopen
	}
	
	private void checkCollisionPen () {
		for (Sheep sheep : sheeps) {
			if (sheep.state != Sheep.SHEEP_STATE_CATCHED) {
				if (sheep.bounds.overlaps(pen.bounds) && 
						!pen.canEnter(sheep.bounds)) {
						sheep.rotation += 180;
						sheep.position.add(-sheep.direction.x - 10, -sheep.direction.y - 10);
				}
				else if (pen.hasScored(sheep.bounds)) {
					sheep.state = Sheep.SHEEP_STATE_CATCHED;
					sheepsCollected++;
				}
			} else if (sheep.state == Sheep.SHEEP_STATE_CATCHED && !pen.hasScored(sheep.bounds)) {
				if (pen.canEnter(sheep.bounds)) {
					sheep.state = Sheep.SHEEP_STATE_FREE;
					sheepsCollected--;
				} else {
					sheep.rotation += 180;
					sheep.position.add(-sheep.direction.x - 10, -sheep.direction.y - 10);
				}
			}
		}
	}
	
	private void checkCollisionRiver () {
		for (Sheep sheep : sheeps) {
			for(River river : rivers) {
				if(sheep.bounds.overlaps(river.bounds) && !river.canPass(sheep)) {
					sheep.rotation += 180;
					sheep.position.add(-sheep.direction.x - 10, -sheep.direction.y - 10);
				}
			}
		}
	}
	
	private void checkCollisionTree () {
		for (Sheep sheep : sheeps) {
			for(Tree tree : trees) {
				if(sheep.bounds.overlaps(tree.bounds)){
					sheep.rotation += 180;
					sheep.position.add(-sheep.direction.x - 10, -sheep.direction.y - 10);
				}
			}
		}
	}
	
	private boolean checkFreeSheepLeft () {
		for (Sheep sheep : sheeps)
			if (sheep.state == Sheep.SHEEP_STATE_FREE || sheep.state == Sheep.SHEEP_STATE_DANGER)
				return true;
		return false;
	}

	private void checkGameOver () {
		if (timeLeft <= 0f || !checkFreeSheepLeft()) {
			state = WORLD_STATE_GAME_OVER;
		}
		
		for (Sheep sheep: sheeps)
			if (sheep.state != Sheep.SHEEP_STATE_CATCHED)
				return;
		state = WORLD_STATE_GAME_OVER;
	}
}
