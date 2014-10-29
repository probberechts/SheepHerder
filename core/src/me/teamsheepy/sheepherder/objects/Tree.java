package me.teamsheepy.sheepherder.objects;

import me.teamsheepy.sheepherder.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Tree extends GameObject {
	public static float TREE_WIDTH = 200;
	public static float TREE_HEIGHT = 140;

	public Tree (float x, float y) {
		super(x, y, TREE_WIDTH, TREE_HEIGHT);
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(Assets.tree, position.x, position.y);
	}
}
