package me.teamsheepy.sheepherder;

import java.util.Random;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import me.teamsheepy.sheepherder.objects.GameObject;
import me.teamsheepy.sheepherder.objects.Pen;
import me.teamsheepy.sheepherder.objects.River;
import me.teamsheepy.sheepherder.objects.Sheep;
import me.teamsheepy.sheepherder.objects.Tree;

public class WorldGenerator {
	
	private SheepWorld shWorld;

	
	public WorldGenerator() {
		this.shWorld =  new SheepWorld();
	}
	
	public SheepWorld createWorld () {
		// Variables
		int MIN_SHEEPS = 3;
		int MAX_SHEEPS = 3;
		int MIN_TREES = 1;
		int MAX_TREES = 3;
		int MIN_RIVERS = 1;
		int MAX_RIVERS = 1;
		int MIN_BRIDGES = 1;
		int MAX_BRIDGES = 3;
		
		//Create the pen
		shWorld.pen = new Pen(20, 565, shWorld.world);
		
		// Create some rivers
		int numRivers = rand(MIN_RIVERS, MAX_RIVERS);
		River river;
		for (int i = 0; i < numRivers; i++) {
			river = new River(0, rand(200, 400), rand(MIN_BRIDGES, MAX_BRIDGES), shWorld.world);
			if (checkOverlapPen(river) || checkOverlapRiver(river)) i--;
			else shWorld.rivers.add(river);
		}
		
		//Create some trees on random positions
		int numTrees = rand(MIN_TREES, MAX_TREES);
		Tree tree;
		for (int i = 0; i < numTrees; i++) {
			tree = new Tree(rand(0, (int) (SheepWorld.WORLD_WIDTH - Tree.TREE_WIDTH/2)), rand(0, (int) (SheepWorld.WORLD_HEIGHT - Tree.TREE_HEIGHT/2)));
			if (checkOverlapPen(tree) || checkOverlapRiver(tree)) i--;
			else shWorld.trees.add(tree);
		}
		
		
		//Create some sheep on random positions
		int numSheeps = rand(MIN_SHEEPS, MAX_SHEEPS);
		Sheep sheep;
		int trys = 0;
		for (int i = 0; i < numSheeps; i++) {
			sheep = new Sheep(rand(50, (int) SheepWorld.WORLD_WIDTH - 70), rand(50, 180));
			sheep.rotation = rand(0, 360);
			if (trys < 200 && checkOverlapObject(sheep)) {
				i--;
				trys++;
			} else {
				shWorld.sheeps.add(sheep);
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyDef.BodyType.DynamicBody;
				bodyDef.position.set(sheep.position.x, sheep.position.y);
				bodyDef.linearDamping = 2.0f;
				sheep.body = shWorld.world.createBody(bodyDef);
				sheep.body.setFixedRotation(true);
				
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(sheep.sprite.getWidth()/2, sheep.sprite.getHeight()/2);
				FixtureDef fixtureDef = new FixtureDef();
		        fixtureDef.shape = shape;
		        fixtureDef.density = 1f;

		        Fixture fixture = sheep.body.createFixture(fixtureDef);

		        shape.dispose();
			}
		}
		
		return shWorld;
	}

	private int rand(int min, int max) {
		Random generator = new Random();
		if (max == min)
			return min;
		return generator.nextInt(max - min) + min;
	}

	private boolean checkOverlapPen(GameObject object) {
		return shWorld.pen.bounds.overlaps(object.bounds);
	}

	private boolean checkOverlapRiver(GameObject object) {
		for (River river : shWorld.rivers)
			if (river.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapSheep(GameObject object) {
		for (Sheep sheep : shWorld.sheeps)
			if (sheep.bounds.overlaps(object.bounds))
				return true;
		return false;
	}

	private boolean checkOverlapObject(GameObject object) {
		return (checkOverlapPen(object) 
				|| checkOverlapRiver(object) 
				|| checkOverlapSheep(object));
	}

}
