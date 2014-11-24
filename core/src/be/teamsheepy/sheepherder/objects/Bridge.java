package be.teamsheepy.sheepherder.objects;

import be.teamsheepy.sheepherder.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bridge extends GameObject {
	public static float BRIDGE_WIDTH = 51;
	public static float BRIDGE_HEIGHT = 48;

	public Bridge (float x, float y) {
		super(x, y, BRIDGE_WIDTH, BRIDGE_HEIGHT);
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(Assets.bridge, position.x, position.y);
	}
}
