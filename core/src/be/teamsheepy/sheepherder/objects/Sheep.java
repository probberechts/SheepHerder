package be.teamsheepy.sheepherder.objects;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SheepWorld;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

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
	public Music currentSound;

	public int sheepId = -1;
	public boolean touched;
	public int timeToDeath;


	public Sheep (float x, float y, int id) {
		super(x, y, SHEEP_WIDTH, SHEEP_HEIGHT);
		this.velocity.x = 0;
		this.velocity.y = 0;
		// Sprites make it easy to calculate the bounding box after rotation
		this.sprite = new Sprite(Assets.sheep);
		this.sprite.setRotation(rotation);
		this.sprite.setPosition(x, y);
		timeToIdle = 0;
		safeMoveX = 0;
		safeMoveY = 0;
		this.sheepId = id;
		touched = false;
		timeToDeath = 150;
	}

	public void update(float deltaTime) {
		//check if sheep is offscreen
		if(state == SHEEP_STATE_ESCAPED) {
			if(timeToDeath > 0)
				timeToDeath -= deltaTime;
			else {
				//sheep has been outside of the screen for too long
				body.setTransform(new Vector2(-500,-500), rotation);
			}
		} else 
			timeToDeath = 150;
		
		// Introduce some random movement for idle sheep
		if (timeToIdle > 0)
			timeToIdle -= deltaTime;
		else {
			Random rand = new Random();
			if (rand.nextInt()%100 == 0)
				rotation = (rotation + (rand.nextInt(41) - 20)) % 360;

			int speed = 0;
			if (rand.nextInt()%100 < 98)
				speed = 600 + rand.nextInt(300);
			else
				timeToIdle = rand.nextInt(500);

			Vector2 force = new Vector2((float) Math.cos(Math.toRadians(rotation)) * speed, (float) Math.sin(Math.toRadians(rotation)) * speed);
			body.applyLinearImpulse(force.x, force.y, body.getPosition().x, body.getPosition().y, true);
		}

		// Check if sheep is escaping
		if (state != SHEEP_STATE_CAUGHT)
			checkCloseToScreenBorder();
		
	}
		
	public void checkCloseToScreenBorder () {
		if (state == SHEEP_STATE_CAUGHT)	return;
		
		int danger = 50;
		if (body.getPosition().x < danger
				|| body.getPosition().x > SheepWorld.WORLD_WIDTH - danger){
			state = SHEEP_STATE_DANGER;
		}else if (body.getPosition().y < danger
				|| body.getPosition().y > SheepWorld.WORLD_HEIGHT - danger){
			state = SHEEP_STATE_DANGER;
		}else
			state = SHEEP_STATE_FREE;
	}

	
	@Override
	public void render(SpriteBatch batch) {
		//not used
	}
	
	public void render(SpriteBatch batch, boolean sleeping) {
		if(body == null) return;
		
		// sprite.setPosition(position.x, position.y);
		sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2,
				body.getPosition().y - sprite.getHeight() / 2);
		sprite.setRotation(rotation);

		if(body.getLinearVelocity().len2() > 20)
			animationStateTime += Gdx.graphics.getDeltaTime() / 2;
		if(body.getLinearVelocity().len2() > 1000)
			animationStateTime += Gdx.graphics.getDeltaTime() * 4;
		TextureRegion frame = Assets.sheepAnimation.getKeyFrame(
				animationStateTime, true);

		// Sprite newSprite = new Sprite(frame);
		// newSprite.setRotation(rotation);
		// newSprite.setPosition(sprite.getX(), sprite.getY());
		// newSprite.draw(batch);

		if(sleeping) {
			batch.draw(Assets.sleepingsheep, sprite.getX(), sprite.getY(), sprite.getWidth() / 2f,
					sprite.getHeight() / 2f, sprite.getWidth(), sprite.getHeight(),
					1f, 1f, sprite.getRotation() - 90, false);
		} else {
			batch.draw(frame, sprite.getX(), sprite.getY(), sprite.getWidth() / 2f,
				sprite.getHeight() / 2f, sprite.getWidth(), sprite.getHeight(),
				1f, 1f, sprite.getRotation() - 90, false);
		}
		
		Vector2 alertPos = new Vector2(body.getPosition().x - Assets.alert.getRegionWidth() / 2, body.getPosition().y - Assets.alert.getRegionHeight() / 2);
		if(alertPos.x < 0)
			alertPos.x = 0;
		if(alertPos.x > SheepWorld.WORLD_WIDTH - Assets.alert.getRegionWidth())
			alertPos.x = SheepWorld.WORLD_WIDTH - Assets.alert.getRegionWidth();
		if(alertPos.y < 0)
			alertPos.y = 0;
		if(alertPos.y > SheepWorld.WORLD_HEIGHT - Assets.alert.getRegionHeight())
			alertPos.y = SheepWorld.WORLD_HEIGHT - Assets.alert.getRegionHeight();
		
		if (state == SHEEP_STATE_DANGER || (state == SHEEP_STATE_ESCAPED && body.getPosition().dst(alertPos) < 100)) {
			drawAlert(batch, alertPos);
		}
	}
	
	private void drawAlert(SpriteBatch batch, Vector2 alertPos) {
		batch.draw(Assets.alert, alertPos.x, alertPos.y);
	}

}
