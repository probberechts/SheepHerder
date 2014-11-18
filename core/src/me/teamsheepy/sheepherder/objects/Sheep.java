package me.teamsheepy.sheepherder.objects;

import java.util.Random;

import me.teamsheepy.sheepherder.Assets;
import me.teamsheepy.sheepherder.SheepWorld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;


public class Sheep extends DynamicGameObject {
	public static final int SHEEP_STATE_FREE = 0;
	public static final int SHEEP_STATE_CATCHED = 1;
	public static final int SHEEP_STATE_DANGER = 2;
	public static final int SHEEP_STATE_ESCAPED = 3;
	public static final float SHEEP_MOVE_VELOCITY = 20;
	public static final float SHEEP_WIDTH = 55;
	public static final float SHEEP_HEIGHT = 50;

	public int state;
	public int timeToIdle;
	public Sprite sprite;
	public float safeMoveX, safeMoveY;
	public Body body;


	public Sheep (float x, float y) {
		super(x, y, SHEEP_WIDTH, SHEEP_HEIGHT);
		this.velocity.x = 0;
		this.velocity.y = 0;
		// Sprites make it easy to calculate the bounding box after rotation
		this.sprite = new Sprite(Assets.sheep);
		this.sprite.setRotation(rotation);
		this.sprite.setPosition(x, y);
		collisionAreas.add(new Rectangle(this.bounds.x + 20, this.bounds.y + 20, this.bounds.width - 40, this.bounds.height - 40));
		timeToIdle = 0;
		safeMoveX = 0;
		safeMoveY = 0; 
	}
	
	public void updateSheepSpeed (float deltaTime, SheepWorld world) {
		if (state == SHEEP_STATE_ESCAPED) return;
		
		// First get the direction the entity is pointed
		direction.x = (float) Math.cos(Math.toRadians(rotation));
		direction.y = (float) Math.sin(Math.toRadians(rotation));
		if (direction.len2() > 0) {
		    direction.nor();
		}
		
		// Calculate the amount of X and Y movement expected by the sheep this frame
		float nextMoveX = velocity.x * direction.x * deltaTime;
		float nextMoveY = velocity.y * direction.y * deltaTime;
		// Calculate the total bounding box of the sheep's current position
		// and its expected movement this frame
		Rectangle movementBounds = new Rectangle(
					Math.min(bounds.x, bounds.x + nextMoveX), 
					Math.min(bounds.y, bounds.y + nextMoveY), 
					Math.max(bounds.width, bounds.width + nextMoveX), 
					Math.max(bounds.height, bounds.height + nextMoveY)
				);
		
		// Check each object to see whether it intersects with the sheep's bounding box
		for (GameObject object : world.getWorldObjects()) {
			for (Rectangle collisionArea : object.collisionAreas) {
				if (object instanceof Sheep) {
					int shrinkAmount = 25;
					//make collisionArea smaller
					Rectangle smaller = new Rectangle(collisionArea.x + shrinkAmount, collisionArea.y + shrinkAmount, collisionArea.width - shrinkAmount, collisionArea.height - shrinkAmount);
					if (smaller.overlaps(movementBounds) && !object.equals(this)) {
						if((((Sheep) object).velocity.x + ((Sheep) object).velocity.y) < (this.velocity.x + this.velocity.y)) {
							((Sheep) object).velocity.x = this.velocity.x;
							((Sheep) object).velocity.y = this.velocity.y;
							((Sheep) object).direction.x = this.direction.x;
							((Sheep) object).direction.y = this.direction.y;
						} 
					}
				}
			}
		}
	}
	
	public void updateStaticCollisions(float deltaTime, SheepWorld world) {
		if (state == SHEEP_STATE_ESCAPED) return;
		
		// First get the direction the entity is pointed
		direction.x = (float) Math.cos(Math.toRadians(rotation));
		direction.y = (float) Math.sin(Math.toRadians(rotation));
		if (direction.len2() > 0) {
		    direction.nor();
		}
				
		// Calculate the amount of X and Y movement expected by the sheep this frame
		float nextMoveX = velocity.x * direction.x * deltaTime;
		float nextMoveY = velocity.y * direction.y * deltaTime;
		// Calculate the total bounding box of the sheep's current position
		// and its expected movement this frame
		Rectangle movementBounds = new Rectangle(
					Math.min(bounds.x, bounds.x + nextMoveX), 
					Math.min(bounds.y, bounds.y + nextMoveY), 
					Math.max(bounds.width, bounds.width + nextMoveX), 
					Math.max(bounds.height, bounds.height + nextMoveY)
				);
		
		// Check each static object to see whether it intersects with the sheep's bounding box
		Array<Rectangle> collisions = new Array<Rectangle>();
		for (GameObject object : world.getWorldObjects()) {
			for (Rectangle collisionArea : object.collisionAreas) {
				if (!object.equals(this)) {
					if (!(object instanceof Sheep)) {
						if (collisionArea.overlaps(movementBounds)) {
							collisions.add(collisionArea);
						}
					}
				}
			}
		}
				
		// Iterate over each object whose bounding box intersects with the player's bounding box
		// until a collision is found
		safeMoveX = nextMoveX;
		safeMoveY = nextMoveY;
		float safeVecLen = (float) Math.sqrt(safeMoveX * safeMoveX + safeMoveY * safeMoveY);
		Rectangle firstCollisonObject = null;
		for (Rectangle area : collisions) {
			// ================================================================================
			// Speculative contacts section
			//
			// We will traverse along the movement vector of the sheep from its current
			// position until the final position for the frame to check if any object lies
			// in the way. If so, the vector is adjusted to end at the object's intersection
			// with the sheep's movement vector. This solves the so-called 'bullet through
			// paper' problem.
			// ================================================================================
			Rectangle intersection = intersect(movementBounds, area);
			float newSafeMoveX = Math.max(Math.max(intersection.x - (movementBounds.x + movementBounds.width), movementBounds.x - (intersection.x + intersection.width)), 0);
			float newSafeMoveY = Math.max(Math.max(intersection.y + intersection.height - (movementBounds.y + movementBounds.height), movementBounds.y - intersection.y), 0);
			float newSafeVecLen = (float) Math.sqrt(newSafeMoveX * newSafeMoveX + newSafeMoveY * newSafeMoveY);
			if (newSafeVecLen < safeVecLen) {
				safeMoveX = newSafeMoveX;
				safeMoveY = newSafeMoveY;
				firstCollisonObject = area;
			}
		}
		
		// If the sheep is pushed to a collision, let it move along the colliding rectangle
		// TODO: Dit stuk is niet optimaal en werkt bijvoorbeeld enkel als er met slechts één object een collison is. 
		// Maar na enkele uren erop te zitten zoeken heb ik niets beter kunnen vinden. Iemand een betere oplossing?
		// Bovendien bewegen de schaapjes zijdelings, maar als je hun richting gaat omzetten in graden en je ze draait, 
		// zorgen afrondingsfouten ervoor dat de schaapjes beginnen trillen.
		if (collisions.size == 1 && firstCollisonObject != null && safeMoveX == 0 && safeMoveY == 0) {
			Vector2 nextMove = new Vector2(nextMoveX, nextMoveY);
			if (getCollisionSide(this.bounds, firstCollisonObject) == 0 || getCollisionSide(this.bounds, firstCollisonObject) == 2)
				safeMoveX = (float) (nextMove.len() * Math.cos(Math.toRadians(nextMove.angle())));
			if (getCollisionSide(this.bounds, firstCollisonObject) == 1 || getCollisionSide(this.bounds, firstCollisonObject) == 3)
				safeMoveY = (float) (nextMove.len() * Math.sin(Math.toRadians(nextMove.angle())));
		}
				
	}

	public void updateOld (float deltaTime, SheepWorld world) {
		if (state == SHEEP_STATE_ESCAPED) return;
		
		// Calculate the total bounding box of the sheep's current position
		// and its expected movement this frame
		Rectangle movementBounds = new Rectangle(
					Math.min(bounds.x, bounds.x + safeMoveX), 
					Math.min(bounds.y, bounds.y + safeMoveY), 
					Math.max(bounds.width, bounds.width + safeMoveX), 
					Math.max(bounds.height, bounds.height + safeMoveY)
				);
		
		// Check each object to see whether it intersects with the sheep's bounding box
		Array<Rectangle> collisions = new Array<Rectangle>();
		for (GameObject object : world.getWorldObjects()) {
			for (Rectangle collisionArea : object.collisionAreas) {
				if (!object.equals(this)) {
					if (object instanceof Sheep) {
						//create collisionarea using predicted movement
						Sprite predictedSprite = new Sprite();
						predictedSprite = new Sprite(Assets.sheep);
						predictedSprite.setRotation(((Sheep) object).rotation);
						Vector2 predictedPos = new Vector2(((Sheep) object).body.getPosition().x, ((Sheep) object).body.getPosition().y);
						predictedPos.add(((Sheep) object).safeMoveX, ((Sheep) object).safeMoveY);
						predictedSprite.setPosition(predictedPos.x, predictedPos.y);
						
						Rectangle predictedBounds = predictedSprite.getBoundingRectangle();
						Rectangle collArea = new Rectangle(predictedBounds.x + 20, predictedBounds.y + 20, predictedBounds.width - 40, predictedBounds.height - 40);
						
						int shrinkAmount = 25;
						//make collisionArea smaller
						Rectangle smaller = new Rectangle(collArea.x + shrinkAmount, collArea.y + shrinkAmount, collArea.width - shrinkAmount, collArea.height - shrinkAmount);
						if (smaller.overlaps(movementBounds)) {
							//sheepcollision
							//collisions.add(smaller);
						}
					} 
				}
			}
		}
		
		if(Gdx.input.isKeyPressed(Keys.DPAD_LEFT)) 
			System.out.println("testing");
		
		// Iterate over each object whose bounding box intersects with the player's bounding box
		// until a collision is found
		float safeVecLen = (float) Math.sqrt(safeMoveX * safeMoveX + safeMoveY * safeMoveY);
		for (Rectangle area : collisions) {
			// ================================================================================
			// Speculative contacts section
			//
			// We will traverse along the movement vector of the sheep from its current
			// position until the final position for the frame to check if any object lies
			// in the way. If so, the vector is adjusted to end at the object's intersection
			// with the sheep's movement vector. This solves the so-called 'bullet through
			// paper' problem.
			// ================================================================================
			Rectangle intersection = intersect(movementBounds, area);
			float newSafeMoveX = Math.max(Math.max(intersection.x - (movementBounds.x + movementBounds.width), movementBounds.x - (intersection.x + intersection.width)), 0);
			float newSafeMoveY = Math.max(Math.max(intersection.y + intersection.height - (movementBounds.y + movementBounds.height), movementBounds.y - intersection.y), 0);
			float newSafeVecLen = (float) Math.sqrt(newSafeMoveX * newSafeMoveX + newSafeMoveY * newSafeMoveY);
			if (newSafeVecLen < safeVecLen) {
				safeMoveX = newSafeMoveX;
				safeMoveY = newSafeMoveY;
			}
		}
				
		// Set the new position, rotation and bounds
		//position.add(safeMoveX, safeMoveY);
		sprite.setRotation(rotation);
		//sprite.setPosition(position.x, position.y);
		bounds = sprite.getBoundingRectangle();
		collisionAreas.clear();
		collisionAreas.add(new Rectangle(this.bounds.x + 20, this.bounds.y + 20, this.bounds.width - 40, this.bounds.height - 40));
		
		// Reduce speed until sheep comes to a stop
		this.velocity.x = this.velocity.x * 0.98f;
		this.velocity.y = this.velocity.y * 0.98f;
		if(this.velocity.x < 1) this.velocity.x = 0;
		if(this.velocity.y < 1) this.velocity.y = 0;
		
		// Introduce some random movement for idle sheep
		if(timeToIdle > 0) timeToIdle -= deltaTime;
		else {
			Random rand = new Random();
			this.velocity.x += rand.nextInt()%30 + 10;
			this.velocity.y += rand.nextInt()%30 + 10;
			if (rand.nextInt()%5 == 3) this.rotation = rand.nextInt()%360;
			timeToIdle = 90 + rand.nextInt()%50;
		}
		
		// Check if sheep is escaping
		if (state != SHEEP_STATE_CATCHED)
			checkCloseToScreenBorder();				
	}
	
	public void update (float deltaTime) {
		// Introduce some random movement for idle sheep
		if(timeToIdle > 0) timeToIdle -= deltaTime;
		else { 
			Random rand = new Random();
			int angle = rand.nextInt()%360;
			
			rotation = ((int) angle + 180) % 360;
			int rot = ((int) angle + 180) % 360; 
			
			Vector2 force = new Vector2((float) Math.cos(Math.toRadians(rot)) * 30000, (float) Math.sin(Math.toRadians(rot)) * 30000);
			body.applyLinearImpulse(force.x, force.y, body.getPosition().x, body.getPosition().y, true);
			
			timeToIdle = 90 + rand.nextInt()%50;
		}
		
		// Check if sheep is escaping
		if (state != SHEEP_STATE_CATCHED)
			checkCloseToScreenBorder();
	}

	private Rectangle intersect(Rectangle rectangle1, Rectangle rectangle2) {
	    if (rectangle1.overlaps(rectangle2)) {
	    	Rectangle intersection = new Rectangle();
	        intersection.x = Math.max(rectangle1.x, rectangle2.x);
	        intersection.width = Math.min(rectangle1.x + rectangle1.width, rectangle2.x + rectangle2.width) - intersection.x;
	        intersection.y = Math.max(rectangle1.y, rectangle2.y);
	        intersection.height = Math.min(rectangle1.y + rectangle1.height, rectangle2.y + rectangle2.height) - intersection.y;
	        return intersection;
	    }
	    return null;
	}
	
	private int getCollisionSide(Rectangle A, Rectangle B) {
		float w = 0.5f * (A.getWidth() + B.getWidth());
		float h = 0.5f * (A.getHeight() + B.getHeight());
		Vector2 centerA = new Vector2(), centerB = new Vector2();
		A.getCenter(centerA);
		B.getCenter(centerB);
		float dx = centerA.x - centerB.x;
		float dy = centerA.y - centerB.y;

		if (Math.abs(dx) <= w && Math.abs(dy) <= h)
		{
		    /* collision! */
		    float wy = w * dy;
		    float hx = h * dx;
		    
		    if (wy > hx)
		        if (wy > -hx)
		            /* collision at the top */
		        	return 0;
		        else
		            /* on the left */
		        	return 3;
		    else
		        if (wy > -hx)
		            /* on the right */
		        	return 1;
		        else
		            /* at the bottom */
		        	return 2;
		}
		return -1;
	}
		
	public void checkCloseToScreenBorder () {
		if (state == SHEEP_STATE_CATCHED)	return;
		
		int danger = 50;
		if (body.getPosition().x < danger || body.getPosition().x > SheepWorld.WORLD_WIDTH - danger) state = SHEEP_STATE_DANGER;
		else if (body.getPosition().y < danger || body.getPosition().y > SheepWorld.WORLD_HEIGHT - danger) state = SHEEP_STATE_DANGER;
		else state = SHEEP_STATE_FREE;
	}

	@Override
	public void render(SpriteBatch batch) {
		//sprite.setPosition(position.x, position.y);
		sprite.setPosition(body.getPosition().x - sprite.getWidth()/2, body.getPosition().y - sprite.getHeight()/2);
		sprite.setRotation(rotation);
		sprite.draw(batch);
//		batch.draw(Assets.sheep, 
//				body.getPosition().x, // x position
//				body.getPosition().y, // y position
//				0, 0, //origin
//				Assets.sheep.getRegionWidth(), Assets.sheep.getRegionHeight(), // width + height
//				1f, 1f,  //scale
//				rotation); //rotation
		if(state == SHEEP_STATE_DANGER)
			batch.draw(Assets.alert, 
					body.getPosition().x - Assets.alert.getRegionWidth() / 2, 
					body.getPosition().y - Assets.alert.getRegionHeight() / 2);
	}

}
