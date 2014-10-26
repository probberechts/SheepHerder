package objects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.World;

public abstract class DynamicGameObject extends GameObject {
	public Vector2 velocity;
	public final Vector2 direction;
	public final Vector2 accel;

	public DynamicGameObject (float x, float y, float width, float height) {
		super(x, y, width, height);
		velocity = new Vector2();
		direction = new Vector2();
		accel = new Vector2();
	}
	
	public abstract void update (float deltaTime, World world);
}
