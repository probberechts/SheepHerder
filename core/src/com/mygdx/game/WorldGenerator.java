package com.mygdx.game;

import java.util.Random;

import objects.GameObject;
import objects.Pen;
import objects.River;
import objects.Sheep;
import objects.Tree;

public class WorldGenerator {
	
	private World world;

	
	public WorldGenerator() {
		this.world =  new World();
	}
	
	public World createWorld () {
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
		world.pen = new Pen(20, 565);
		
		// Create some rivers
		int numRivers = rand(MIN_RIVERS, MAX_RIVERS);
		River river;
		for (int i = 0; i < numRivers; i++) {
			river = new River(0, rand(200, 400), rand(MIN_BRIDGES, MAX_BRIDGES));
			if (checkOverlapPen(river) || checkOverlapRiver(river)) i--;
			else world.rivers.add(river);
		}
		
		//Create some trees on random positions
		int numTrees = rand(MIN_TREES, MAX_TREES);
		Tree tree;
		for (int i = 0; i < numTrees; i++) {
			tree = new Tree(rand(0, (int) (World.WORLD_WIDTH - Tree.TREE_WIDTH/2)), rand(0, (int) (World.WORLD_HEIGHT - Tree.TREE_HEIGHT/2)));
			if (checkOverlapPen(tree) || checkOverlapRiver(tree)) i--;
			else world.trees.add(tree);
		}
		
		
		//Create some sheeps on random positions
		int numSheeps = rand(MIN_SHEEPS, MAX_SHEEPS);
		Sheep sheep;
		for (int i = 0; i < numSheeps; i++) {
			sheep = new Sheep(rand(50, (int) World.WORLD_WIDTH - 70), rand(50, 180));
			sheep.rotation = rand(0, 360);
			if (checkOverlapObject(sheep)) i--;
			else world.sheeps.add(sheep);
		}
		
		return world;
	}

	private int rand(int min, int max) {
		Random generator = new Random();
		if (max == min)
			return min;
		return generator.nextInt(max - min) + min;
	}

	private boolean checkOverlapPen(GameObject object) {
		return world.pen.bounds.overlaps(object.bounds);
	}

	private boolean checkOverlapTree(GameObject object) {
		for (Tree tree : world.trees)
			if (tree.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapRiver(GameObject object) {
		for (River river : world.rivers)
			if (river.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapSheep(GameObject object) {
		for (Sheep sheep : world.sheeps)
			if (sheep.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapObject(GameObject object) {
		return (checkOverlapPen(object) || checkOverlapTree(object)
				|| checkOverlapRiver(object) || checkOverlapSheep(object));
	}

}
