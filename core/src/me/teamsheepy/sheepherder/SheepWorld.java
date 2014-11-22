package me.teamsheepy.sheepherder;

import java.util.ArrayList;
import java.util.List;

import me.teamsheepy.sheepherder.objects.Pen;
import me.teamsheepy.sheepherder.objects.River;
import me.teamsheepy.sheepherder.objects.Sheep;
import me.teamsheepy.sheepherder.objects.Tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Contact;
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
		for(Contact contact : world.getContactList()) {
			if(contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() != null)
            	if(contact.getFixtureA().getBody().getUserData().toString().contains("schaap")&& contact.getFixtureB().getBody().getUserData().toString().contains("schaap")) {
            		//collision between sheep detected
            		Vector2 velocity1 = contact.getFixtureA().getBody().getLinearVelocity();
            		Vector2 velocity2 = contact.getFixtureB().getBody().getLinearVelocity();
            		
        			for(Sheep s : sheeps)
        				if((s.sheepId + "").equals(contact.getFixtureA().getBody().getUserData().toString().substring(6)))
        					for(Sheep t : sheeps)
        						if((t.sheepId + "").equals(contact.getFixtureB().getBody().getUserData().toString().substring(6)))
        							if(velocity1.len2() >= velocity2.len2()) {
        								if(s.touched) {
        									t.rotation = s.rotation;
        									t.timeToIdle = 100;
        								}
        							} else {
        								if(t.touched) {
	        								s.rotation = t.rotation;
	        								s.timeToIdle = 100;
        								}
        							}
            	}
		}
		checkGameOver();
	}

	public void updateRotationSheeps(Vector3 touchPos) {
		//push sheep away from finger/mouse
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
				sheep.touched = true;
			} else sheep.touched = false;
		}
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
			sheep.state = Sheep.SHEEP_STATE_CAUGHT;
			sheepsCollected++;
			if (sheepsCollected == 0)
				SheepHerder.analytics.trackTimedEvent("gameEvent", "firstSheepInPen", SavedData.gamesPlayed + "", GAME_TIME-timeLeft);
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
		state = WORLD_STATE_GAME_OVER;
	}
}
