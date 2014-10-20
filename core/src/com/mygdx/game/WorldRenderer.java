package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {
	static final float SCREEN_WIDTH = 480;
	static final float SCREEN_HEIGHT = 800;
	World world;
	SpriteBatch batch;

	public WorldRenderer (SpriteBatch batch, World world) {
		this.world = world;
		this.batch = batch;
	}

	public void render () {
		renderBackground();
		renderObjects();
	}

	public void renderBackground () {
		Gdx.gl.glClearColor(0.172f, 0.690f, 0.212f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

	public void renderObjects () {
		batch.enableBlending();
		batch.begin();
		renderSheeps();
		renderPen();
		renderTrees();
		renderRivers();
		renderBridges();
		batch.end();
	}

	private void renderSheeps () {
		int len = world.sheeps.size();
		for (int i = 0; i < len; i++) {
			Sheep sheep = world.sheeps.get(i);
			batch.draw(Assets.sheep, 
					sheep.position.x - Sheep.SHEEP_WIDTH / 2f, sheep.position.y - Sheep.SHEEP_HEIGHT / 2f, 
					Sheep.SHEEP_WIDTH / 2f, Sheep.SHEEP_HEIGHT / 2f, 
					Sheep.SHEEP_WIDTH, Sheep.SHEEP_HEIGHT, 
					1, 1, sheep.rotation, 0, 0, Assets.sheep.getWidth(), Assets.sheep.getHeight(),
					false, false);
			if(sheep.state == Sheep.SHEEP_STATE_DANGER)
				batch.draw(Assets.alert, sheep.position.x - Assets.alert.getWidth() / 2, sheep.position.y - Assets.alert.getHeight() / 2);
		}
	}

	private void renderPen () {
		Pen pen = world.pen;
		batch.draw(Assets.pen, pen.position.x - Pen.PEN_WIDTH / 2f, pen.position.y - Pen.PEN_HEIGHT / 2f, 
				Pen.PEN_WIDTH / 2f, Pen.PEN_HEIGHT / 2f,
				Pen.PEN_WIDTH, Pen.PEN_HEIGHT, 
				1, 1, 0, 0, 0, Assets.pen.getWidth(), Assets.pen.getHeight(),
				false, false);
	}

	private void renderTrees () {
		int len = world.trees.size();
		for (int i = 0; i < len; i++) {
			Tree tree = world.trees.get(i);
			batch.draw(Assets.tree, tree.position.x - Tree.TREE_WIDTH / 2f, tree.position.y - Tree.TREE_HEIGHT / 2f);
		}
	}

	private void renderRivers () {
		int len = world.rivers.size();
		for (int i = 0; i < len; i++) {
			River river = world.rivers.get(i);
			batch.draw(Assets.river, river.position.x, river.position.y);
		}	
	}
	
	private void renderBridges () {
		int len = world.bridges.size();
		for (int i = 0; i < len; i++) {
			Bridge bridge = world.bridges.get(i);
			batch.draw(Assets.bridge, bridge.position.x, bridge.position.y);
		}	
	}

}
