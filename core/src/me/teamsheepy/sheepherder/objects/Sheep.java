package me.teamsheepy.sheepherder.objects;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	public static final int SHEEP_STATE_CAUGHT = 1;
	public static final int SHEEP_STATE_DANGER = 2;
	public static final int SHEEP_STATE_ESCAPED = 3;
	public static final float SHEEP_WIDTH = 55;
	public static final float SHEEP_HEIGHT = 50;

	public int state;
	public int timeToIdle;
	public Sprite sprite;
	public float safeMoveX, safeMoveY;
	public Body body;
	private float animationStateTime = 0;
	public int sheepId = -1;
	public boolean touched;


	public Sheep (float x, float y, int id) {
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
		this.sheepId = id;
		touched = false;
	}
	
	
	
	public void update (float deltaTime) {
		// Introduce some random movement for idle sheep
		if(timeToIdle > 0) timeToIdle -= deltaTime;
		else { 
//			Random rand = new Random();
//			int angle = rand.nextInt()%360;
//			
//			rotation = ((int) angle + 180) % 360;
//			int rot = ((int) angle + 180) % 360; 
//			
//			Vector2 force = new Vector2((float) Math.cos(Math.toRadians(rot)) * 30000, (float) Math.sin(Math.toRadians(rot)) * 30000);
//			body.applyLinearImpulse(force.x, force.y, body.getPosition().x, body.getPosition().y, true);
//			
//			timeToIdle = 90 + rand.nextInt()%50;
		}
		
		// Check if sheep is escaping
		if (state != SHEEP_STATE_CAUGHT)
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
		if (state == SHEEP_STATE_CAUGHT)	return;
		
		int danger = 50;
		if (body.getPosition().x < danger || body.getPosition().x > SheepWorld.WORLD_WIDTH - danger) state = SHEEP_STATE_DANGER;
		else if (body.getPosition().y < danger || body.getPosition().y > SheepWorld.WORLD_HEIGHT - danger) state = SHEEP_STATE_DANGER;
		else state = SHEEP_STATE_FREE;
	}

	@Override
	public void render(SpriteBatch batch) {
		//sprite.setPosition(position.x, position.y);
		sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
		sprite.setRotation(rotation);
		
		if(body.getLinearVelocity().x > 10 || body.getLinearVelocity().y > 10)
			animationStateTime += Gdx.graphics.getDeltaTime();
		if(body.getLinearVelocity().x > 30 || body.getLinearVelocity().y > 30)
			animationStateTime += Gdx.graphics.getDeltaTime() * 4;
		TextureRegion frame = Assets.sheepAnimation.getKeyFrame(animationStateTime, true);
		
//		Sprite newSprite = new Sprite(frame);
//		newSprite.setRotation(rotation);
//		newSprite.setPosition(sprite.getX(), sprite.getY());
//		newSprite.draw(batch);
		
		batch.draw(frame,sprite.getX(), sprite.getY(),
					sprite.getWidth() / 2f , sprite.getHeight() / 2f,
					sprite.getWidth(), sprite.getHeight(),
					1f, 1f,
					sprite.getRotation() - 90, false);

		if(state == SHEEP_STATE_DANGER)
			batch.draw(Assets.alert, 
					body.getPosition().x - Assets.alert.getRegionWidth() / 2, 
					body.getPosition().y - Assets.alert.getRegionHeight() / 2);
	}

}
