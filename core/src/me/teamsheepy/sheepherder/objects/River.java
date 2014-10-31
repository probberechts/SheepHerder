package me.teamsheepy.sheepherder.objects;

import me.teamsheepy.sheepherder.Assets;
import me.teamsheepy.sheepherder.World;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class River extends GameObject {
	
	public static float RIVER_WIDTH = World.WORLD_WIDTH;
	public static float RIVER_HEIGHT = 38;
	private int numBridges;
    public Array<Bridge> bridges;
	
	public River (float x, float y, int numBridges) {
		super(x, y, RIVER_WIDTH, RIVER_HEIGHT);
		this.numBridges = numBridges;
		init();
	}
	
	private void init () {
		// MARGE = the space next to a bridge which isn't collidable
		int MARGE = (int) (Sheep.SHEEP_WIDTH / 2);
		bridges = new Array<Bridge>();
		int numRiverParts = numBridges + 1;
		float riverPartLength = River.RIVER_WIDTH / numRiverParts;
		for (int i = 0; i < numRiverParts; i++) {
			// construct collision area
			Rectangle part;
			if (i == 0)
				// first river part
				part = new Rectangle(this.position.x - 50, this.position.y, 
						riverPartLength - MARGE / 2 - Bridge.BRIDGE_WIDTH / 2 + 50, bounds.height);
			else if (i == numRiverParts - 1)
				// last river part
				part = new Rectangle(this.position.x + i * riverPartLength + MARGE / 2 + Bridge.BRIDGE_WIDTH  / 2, this.position.y, 
						riverPartLength + 50, bounds.height);
			else
				// other river parts
				part = new Rectangle(this.position.x + i * riverPartLength + MARGE / 2 + Bridge.BRIDGE_WIDTH / 2, this.position.y, 
						riverPartLength - MARGE - Bridge.BRIDGE_WIDTH, bounds.height);
			collisionAreas.add(part);
			// construct bridges
			if (i>0) bridges.add(new Bridge(i * riverPartLength - Bridge.BRIDGE_WIDTH/2, this.position.y));
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(Assets.river, position.x, position.y, 
				center.x, center.y,
				RIVER_WIDTH, RIVER_HEIGHT, 
				1, 1, 0, 0, 0, Assets.river.getWidth(), Assets.river.getHeight(),
				false, false);
		for (Bridge bridge : bridges)
			bridge.render(batch);
	}
	
}
