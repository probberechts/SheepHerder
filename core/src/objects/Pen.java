package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Assets;


public class Pen extends GameObject {
	public static float PEN_WIDTH = 200;
	public static float PEN_HEIGHT = 225;

	private Rectangle scoreZone;
	private Array<Rectangle> collisionZones = new Array<Rectangle>();
	
	public Pen (float x, float y) {
		super(x, y, PEN_WIDTH, PEN_HEIGHT);
		bounds.height -= 30; 
		initScoreZone();
		initCollisionZones();
	}
	
	private void initScoreZone() {
		scoreZone = new Rectangle(bounds.x + 8, bounds.y + 23, bounds.getWidth() - 16, bounds.getHeight() - 34 - 8);
	}
	
	private void initCollisionZones() {
		Rectangle top = new Rectangle(bounds.x, bounds.y + 186, bounds.getWidth(), 17);
		Rectangle left = new Rectangle(bounds.x, bounds.y, 8, bounds.getHeight() - 17);
		Rectangle bottom = new Rectangle(bounds.x, bounds.y, 100, 23);
		Rectangle right = new Rectangle(bounds.x + 189, bounds.y, 8, bounds.getHeight() - 17);
		collisionZones.add(top);
		collisionZones.add(left);
		collisionZones.add(right);
		collisionZones.add(bottom);
	}
	
	public boolean canEnter(Rectangle object) {
		for (Rectangle collisionZone : collisionZones)
			if (object.overlaps(collisionZone))
				return false;
		return true;
	}
	
	public boolean hasScored(Rectangle object) {
		return scoreZone.contains(object);
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(Assets.pen, position.x, position.y, 
				center.x, center.y,
				PEN_WIDTH, PEN_HEIGHT, 
				1, 1, 0, 0, 0, Assets.pen.getWidth(), Assets.pen.getHeight(),
				false, false);
	}
}
