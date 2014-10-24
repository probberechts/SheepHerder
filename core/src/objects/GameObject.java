package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
	public final Vector2 position;
	public final Vector2 center;
	public int rotation;
	public final Rectangle bounds;

	public GameObject (float x, float y, float width, float height) {
		// lower left point
		this.position = new Vector2(x, y);
		// center of the object
		this.center = new Vector2(width/2f, height/2f);
		this.rotation = 0;
		this.bounds = new Rectangle(x, y, width, height);
	}
	
    public abstract void render (SpriteBatch batch);
    
}