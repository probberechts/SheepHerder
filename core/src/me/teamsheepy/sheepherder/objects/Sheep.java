package me.teamsheepy.sheepherder.objects;

import java.util.Random;

import me.teamsheepy.sheepherder.Assets;
import me.teamsheepy.sheepherder.World;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
	private Sprite sprite;


	public Sheep (float x, float y) {
		super(x, y, SHEEP_WIDTH, SHEEP_HEIGHT);
		this.velocity.x = 0;
		this.velocity.y = 0;
		// Sprites make it easy to calculate the bounding box after rotation
		this.sprite = new Sprite(Assets.sheep);
		this.sprite.setRotation(rotation);
		this.sprite.setPosition(position.x, position.y);
		timeToIdle = 0;
	}

	public void update (float deltaTime, World world) {
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
		Array<Rectangle> collisionObjects = new Array<Rectangle>();
		for(Rectangle object : world.getCollisionAreas())
			if(object.overlaps(movementBounds))
				collisionObjects.add(object);

		// Iterate over each object whose bounding box intersects with the player's bounding box
		// until a collision is found
		float safeMoveX = nextMoveX, safeMoveY = nextMoveY;
		float safeVecLen = (float) Math.sqrt(safeMoveX * safeMoveX + safeMoveY * safeMoveY);
		Rectangle firstCollisonObject = null;
		for (Rectangle object : collisionObjects) {
			// ================================================================================
			// Speculative contacts section
			//
			// We will traverse along the movement vector of the sheep from its current
			// position until the final position for the frame to check if any object lies
			// in the way. If so, the vector is adjusted to end at the object's intersection
			// with the sheep's movement vector. This solves the so-called 'bullet through
			// paper' problem.
			// ================================================================================
			Rectangle intersection = intersect(movementBounds, object);
			float newSafeMoveX = Math.max(Math.max(intersection.x - (movementBounds.x + movementBounds.width), movementBounds.x - (intersection.x + intersection.width)), 0);
			float newSafeMoveY = Math.max(Math.max(intersection.y + intersection.height - (movementBounds.y + movementBounds.height), movementBounds.y - intersection.y), 0);
			float newSafeVecLen = (float) Math.sqrt(newSafeMoveX * newSafeMoveX + newSafeMoveY * newSafeMoveY);
			if (newSafeVecLen < safeVecLen) {
				safeMoveX = newSafeMoveX;
				safeMoveY = newSafeMoveY;
				firstCollisonObject = object;
			}

		}	
		
		// If the sheep is pushed to a collision, let it move along the colliding rectangle
		// TODO: Dit stuk is niet optimaal en werkt bijvoorbeeld enkel als er met slechts één object een collison is. 
		// Maar na enkele uren erop te zitten zoeken heb ik niets beter kunnen vinden. Iemand een betere oplossing?
		// Bovendien bewegen de schaapjes zijdelings, maar als je hun richting gaat omzetten in graden en je ze draait, 
		// zorgen afrondingsfouten ervoor dat de schaapjes beginnen trillen.
		if (collisionObjects.size == 1 && firstCollisonObject != null && safeMoveX == 0 && safeMoveY == 0) {
			Vector2 nextMove = new Vector2(nextMoveX, nextMoveY);
			if (getCollisionSide(this.bounds, firstCollisonObject) == 0 || getCollisionSide(this.bounds, firstCollisonObject) == 2)
				safeMoveX = (float) (nextMove.len() * Math.cos(Math.toRadians(nextMove.angle())));
			if (getCollisionSide(this.bounds, firstCollisonObject) == 1 || getCollisionSide(this.bounds, firstCollisonObject) == 3)
				safeMoveY = (float) (nextMove.len() * Math.sin(Math.toRadians(nextMove.angle())));
		}
		
		// Set the new position, rotation and bounds
		position.add(safeMoveX, safeMoveY);
		sprite.setRotation(rotation);
		sprite.setPosition(position.x, position.y);
		bounds = sprite.getBoundingRectangle();
		
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
		int danger = 50;
		if (position.x < danger && direction.x < 0 || position.x > World.WORLD_WIDTH - danger && direction.x > 0) state = SHEEP_STATE_DANGER;
		else if (position.y < danger && direction.y < 0 || position.y > World.WORLD_HEIGHT - danger && direction.y > 0) state = SHEEP_STATE_DANGER;
		else state = SHEEP_STATE_FREE;
	}

	@Override
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
		if(state == SHEEP_STATE_DANGER)
			batch.draw(Assets.alert, 
					position.x + Assets.alert.getWidth() / 2 + 10, 
					position.y + 55);
	}

}
