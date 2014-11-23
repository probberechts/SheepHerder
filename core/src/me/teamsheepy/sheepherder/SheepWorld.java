package me.teamsheepy.sheepherder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.teamsheepy.sheepherder.objects.Bridge;
import me.teamsheepy.sheepherder.objects.GameObject;
import me.teamsheepy.sheepherder.objects.Pen;
import me.teamsheepy.sheepherder.objects.River;
import me.teamsheepy.sheepherder.objects.Sheep;
import me.teamsheepy.sheepherder.objects.Tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

public class SheepWorld {

	public static final float WORLD_WIDTH = 480;
	public static final float WORLD_HEIGHT = 800;
	public static final int WORLD_STATE_RUNNING = 0;
	public static final int WORLD_STATE_GAME_OVER = 1;
	public static final int WORLD_STATE_SWIPE_SUGGESTION = 2;
	public static int WORLD_MARGIN = 20;
	public static final Color WORLD_MARGIN_COLOR = new Color(0x006400ff);
	public static final int GAME_TIME = 6000;
	
	public static final int SHEEP_SOUND_AFFECTED_THRESHOLD = 5;

	public Pen pen;
	public final List<Sheep> sheeps;
	public final List<Tree> trees;
	public final List<River> rivers;

	public int timeLeft;
	public int sheepsCollected;
	public int state;
	public int tapCount = 0;
	
	public World world;

	public SheepWorld() {
		//this.pen = new Pen(5, 1, null);
		this.sheeps = new ArrayList<Sheep>();
		this.trees = new ArrayList<Tree>();
		this.rivers = new ArrayList<River>();

		this.timeLeft = 6000;
		this.sheepsCollected = 0;
		this.state = WORLD_STATE_RUNNING;
		
		//initialize box2D
		Box2D.init();
		world = new World(new Vector2(0,0), true);
	}

	public void update(float deltaTime) {
		for (Sheep sheep : sheeps) 
			if (Gdx.input.isKeyPressed(Keys.A)) {          
				sheep.body.applyLinearImpulse(-500.0f, 0,  sheep.body.getPosition().x, sheep.body.getPosition().y, true);
			}
		world.step(deltaTime, 6, 2);
		updateSheeps(deltaTime);
		updateTrees();
		checkGameOver();
	}

	public void updateRotationSheeps(Vector3 touchPos) {
		//push sheep away from finger/mouse
		int sheepAffected = 0;
		for (Sheep sheep : sheeps) {
			if (sheep.body.getPosition().dst2(touchPos.x, touchPos.y) < 10000) {
				sheepAffected++;
			}
		}
		for (Sheep sheep : sheeps) {
			if (sheep.body.getPosition().dst2(touchPos.x, touchPos.y) < 10000) {
				float angle;
				if (touchPos.x < SheepWorld.WORLD_MARGIN
						|| touchPos.x > SheepWorld.WORLD_WIDTH - SheepWorld.WORLD_MARGIN
						|| touchPos.y < SheepWorld.WORLD_MARGIN
						|| touchPos.y > SheepWorld.WORLD_HEIGHT - SheepWorld.WORLD_MARGIN) {
					angle = new Vector2(touchPos.x, touchPos.y).sub(
							new Vector2(SheepWorld.WORLD_WIDTH / 2,
									SheepWorld.WORLD_HEIGHT / 2)).angle();
				} else {
					angle = new Vector2(touchPos.x, touchPos.y).sub(
							new Vector2(sheep.body.getPosition().x,
									sheep.body.getPosition().y)).angle();
				}
				sheep.rotation = ((int) angle + 180) % 360;
				int rot = ((int) angle + 180) % 360; 
				sheep.velocity = new Vector2(100, 100);
				sheep.timeToIdle = 200;
				
				Vector2 force = new Vector2((float) Math.cos(Math.toRadians(rot)) * 30000, (float) Math.sin(Math.toRadians(rot)) * 30000);
				sheep.body.applyLinearImpulse(force.x, force.y, sheep.body.getPosition().x, sheep.body.getPosition().y, true);
				if(sheepAffected <= SHEEP_SOUND_AFFECTED_THRESHOLD && (sheep.currentSound == null || !sheep.currentSound.isPlaying())){
					sheep.currentSound = Assets.sheepSounds.get((int)(Math.random()*Assets.sheepSounds.size()));
					sheep.currentSound.play();
				}
				
			}
		}
		if(sheepAffected > SHEEP_SOUND_AFFECTED_THRESHOLD)
			Assets.manySheepSound.play();
		
//		for (Sheep sheep : sheeps) {
//			if (sheep.position.dst2(touchPos.x, touchPos.y) < 10000) {
//				float angle;
//				if (touchPos.x < SheepWorld.WORLD_MARGIN
//						|| touchPos.x > SheepWorld.WORLD_WIDTH - SheepWorld.WORLD_MARGIN
//						|| touchPos.y < SheepWorld.WORLD_MARGIN
//						|| touchPos.y > SheepWorld.WORLD_HEIGHT - SheepWorld.WORLD_MARGIN) {
//					angle = new Vector2(touchPos.x, touchPos.y).sub(
//							new Vector2(SheepWorld.WORLD_WIDTH / 2,
//									SheepWorld.WORLD_HEIGHT / 2)).angle();
//				} else {
//					angle = new Vector2(touchPos.x, touchPos.y).sub(
//							new Vector2(sheep.bounds.x + sheep.center.x,
//									sheep.bounds.y + sheep.center.y)).angle();
//				}
//				sheep.rotation = ((int) angle + 180) % 360;
//				sheep.velocity = new Vector2(100, 100);
//				sheep.timeToIdle = 200;
//			}
//		}
	}

	public List<GameObject> getWorldObjects() {
		List<GameObject> result = new LinkedList<GameObject>();
		result.addAll(sheeps);
		result.addAll(trees);
		result.addAll(rivers);
		result.add(pen);
		return result;
	}
	
	private void updateSheeps (float deltaTime) {
		for (Sheep sheep : sheeps) {
			// check is sheep collide and give the speed from one sheep to the sheep it's pushing
			//sheep.updateSheepSpeed(deltaTime, this);
			// check collision between a sheep and world objects
			//sheep.updateStaticCollisions(deltaTime, this);
			// update sheep
			sheep.update(deltaTime);
			// check if a sheep has escape
			checkIfSheepHasEscaped(sheep);
			// check if a sheep has entered or left the pen
			checkIfSheepsIsInPen(sheep);
			
			sheep.sprite.setPosition(sheep.body.getPosition().x, sheep.body.getPosition().y);
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
		Vector2 pos = sheep.body.getPosition();
		if (pos.x + sheep.bounds.width < 0 
				|| pos.x > WORLD_WIDTH 
				|| pos.y + sheep.bounds.height < 0 
				|| pos.y > WORLD_HEIGHT) {
			sheep.state = Sheep.SHEEP_STATE_ESCAPED;
			sheep.collisionAreas.clear();
		}
	}
	
	private void checkIfSheepsIsInPen(Sheep sheep) {
		Rectangle sheepBounds = new Rectangle(sheep.body.getPosition().x, sheep.body.getPosition().y, sheep.bounds.width, sheep.bounds.height);
		if (sheep.state != Sheep.SHEEP_STATE_CAUGHT && pen.hasScored(sheepBounds)) {
			Assets.sheepInPen.play();
			sheep.state = Sheep.SHEEP_STATE_CAUGHT;
			sheepsCollected++;
		} else if (sheep.state == Sheep.SHEEP_STATE_CAUGHT && !pen.hasScored(sheepBounds)) {
			sheep.state = Sheep.SHEEP_STATE_FREE;
			sheepsCollected--;
		}
	}
	
	private void checkGameOver() {
		if (timeLeft <= 0f || sheeps.isEmpty()) {
			state = WORLD_STATE_GAME_OVER;
		}

		for (Sheep sheep : sheeps)
			if (sheep.state != Sheep.SHEEP_STATE_CAUGHT
					&& sheep.state != Sheep.SHEEP_STATE_ESCAPED)  
				return;
		if(timeLeft > 0f)
			Assets.allSheepInPen.play();
		state = WORLD_STATE_GAME_OVER;
	}
}
