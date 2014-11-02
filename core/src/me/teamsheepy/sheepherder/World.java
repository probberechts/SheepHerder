package me.teamsheepy.sheepherder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.teamsheepy.sheepherder.objects.Pen;
import me.teamsheepy.sheepherder.objects.River;
import me.teamsheepy.sheepherder.objects.Sheep;
import me.teamsheepy.sheepherder.objects.Tree;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
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

		this.timeLeft = 6000;
		this.sheepsCollected = 0;
		this.state = WORLD_STATE_RUNNING;
	}

	
	public void update(float deltaTime) {
		updateSheeps(deltaTime);
		updateTrees();
		checkGameOver();
	}

	public void updateRotationSheeps(Vector3 touchPos) {
		for (Sheep sheep : sheeps) {
			if (sheep.position.dst2(touchPos.x, touchPos.y) < 10000) {
				float angle = new Vector2(touchPos.x, touchPos.y).sub(
						new Vector2(sheep.bounds.x + sheep.center.x,
								sheep.bounds.y + sheep.center.y)).angle();
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
			// update sheep's position
			sheep.update(deltaTime, this);
			// check if a sheep has escape
			checkIfSheepHasEscaped(sheep);
			// check if a sheep has entered or left the pen
			checkIfSheepsIsInPen(sheep);
		}
	}
	
	public void updateTrees() {
		for (Tree tree : trees) {
			boolean shouldBeAnimated = false;
			for (Sheep sheep : sheeps) {
				if (tree.bounds.contains(sheep.bounds)) {
					shouldBeAnimated = true;
					break;
				}
			}
			if (shouldBeAnimated) 	tree.startAnimation();
			else 					tree.stopAnimation();
		}
	}
	
	private void checkIfSheepHasEscaped (Sheep sheep) {
		if (sheep.bounds.x + sheep.bounds.width < 0 
				|| sheep.bounds.x > WORLD_WIDTH 
				|| sheep.bounds.y + sheep.bounds.height < 0 
				|| sheep.bounds.y > WORLD_HEIGHT)
			sheep.state = Sheep.SHEEP_STATE_ESCAPED;
	}
	
	private void checkIfSheepsIsInPen(Sheep sheep) {
		if (sheep.state != Sheep.SHEEP_STATE_CATCHED && pen.hasScored(sheep.bounds)) {
			sheep.state = Sheep.SHEEP_STATE_CATCHED;
			sheepsCollected++;
		} else if (sheep.state == Sheep.SHEEP_STATE_CATCHED && !pen.hasScored(sheep.bounds)) {
			sheep.state = Sheep.SHEEP_STATE_FREE;
			sheepsCollected--;
		}
	}
	
	private void checkGameOver() {
		if (timeLeft <= 0f || sheeps.isEmpty()) {
			state = WORLD_STATE_GAME_OVER;
		}

		for (Sheep sheep : sheeps)
			if (sheep.state != Sheep.SHEEP_STATE_CATCHED
					&& sheep.state != Sheep.SHEEP_STATE_ESCAPED)  
				return;
		state = WORLD_STATE_GAME_OVER;
	}
}
