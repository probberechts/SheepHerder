package be.teamsheepy.sheepherder.objects;

import be.teamsheepy.sheepherder.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Tree extends GameObject {
	public static float TREE_WIDTH = 200;
	public static float TREE_HEIGHT = 140;
	private boolean playAnimation;
	private float animationStateTime = 0;

	public Tree (float x, float y) {
		super(x, y, TREE_WIDTH, TREE_HEIGHT);
	}
	
	public void startAnimation() {
		playAnimation = true;
	}
	
	public void stopAnimation() {
		playAnimation = false;
	}

	@Override
	public void render(SpriteBatch batch) {
		if (playAnimation) {
			animationStateTime += Gdx.graphics.getDeltaTime();
			batch.draw(Assets.sheepUnderTreeAnimation.getKeyFrame(animationStateTime, true), position.x, position.y);
		} else {
			batch.draw(Assets.tree, position.x, position.y);
		}
	}
}
