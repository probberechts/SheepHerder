package objects;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Assets;
import com.mygdx.game.World;


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

	public void update (float deltaTime) {		
		//First get the direction the entity is pointed
		direction.x = (float) Math.cos(Math.toRadians(rotation));
		direction.y = (float) Math.sin(Math.toRadians(rotation));
		if (direction.len2() > 0) {
		    direction.nor();
		}
		
		//Then determine the new position based on speed, direction and deltaTime
		sprite.setRotation(rotation);
		position.add(velocity.x * direction.x * deltaTime, velocity.y * direction.y * deltaTime);
		sprite.setPosition(position.x, position.y);
		bounds = sprite.getBoundingRectangle();
		
		//reduce speed until sheep comes to a stop
		this.velocity.x = this.velocity.x * 0.98f;
		this.velocity.y = this.velocity.y * 0.98f;
		if(this.velocity.x < 1) this.velocity.x = 0;
		if(this.velocity.y < 1) this.velocity.y = 0;
		
		if(timeToIdle > 0) timeToIdle -= deltaTime;
		else {
			//random movement
			Random rand = new Random();
			this.velocity.x += rand.nextInt()%30 + 10;
			this.velocity.y += rand.nextInt()%30 + 10;
			if (rand.nextInt()%5 == 3) this.rotation = rand.nextInt()%360;
			timeToIdle = 90 + rand.nextInt()%50;
		}
		
		if (state != SHEEP_STATE_CATCHED)
			checkCloseToScreenBorder();				
	}

	public void checkCloseToScreenBorder () {
		int danger = 50;
		if (position.x < danger && direction.x < 0 || position.x > World.WORLD_WIDTH - danger && direction.x > 0) state = SHEEP_STATE_DANGER;
		else if (position.y < danger && direction.y < 0 || position.y > World.WORLD_HEIGHT - danger && direction.y > 0) state = SHEEP_STATE_DANGER;
		else state = SHEEP_STATE_FREE;
	}

	public void setCatched() {
		state = SHEEP_STATE_CATCHED;
	}

	@Override
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
		if(state == SHEEP_STATE_DANGER)
			batch.draw(Assets.alert, 
					position.x + Assets.alert.getWidth() / 2 + 10, 
					position.y + 60);
	}

}
