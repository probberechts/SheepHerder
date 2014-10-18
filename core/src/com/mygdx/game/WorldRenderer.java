package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {
	static final float SCREEN_WIDTH = 480;
	static final float SCREEN_HEIGHT = 800;
	World world;
	OrthographicCamera cam;
	SpriteBatch batch;

	public WorldRenderer (SpriteBatch batch, World world) {
		this.world = world;
		this.cam = new OrthographicCamera(SCREEN_WIDTH, SCREEN_HEIGHT);
		this.cam.setToOrtho(false, SCREEN_WIDTH, SCREEN_WIDTH);
		this.batch = batch;
	}

	public void render () {
		batch.setProjectionMatrix(cam.combined);
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
		renderRiver();
		batch.end();
	}

	private void renderSheeps () {
		int len = world.sheeps.size();
		for (int i = 0; i < len; i++) {
			Sheep sheep = world.sheeps.get(i);
			batch.draw(Assets.sheep, sheep.position.x, sheep.position.y);
		}
	}

	private void renderPen () {
		Pen pen = world.pen;
		batch.draw(Assets.pen, pen.position.x - 1, pen.position.y - 0.25f);
	}

	private void renderTrees () {
		int len = world.trees.size();
		for (int i = 0; i < len; i++) {
			Tree tree = world.trees.get(i);
			batch.draw(Assets.tree, tree.position.x - 0.5f, tree.position.y - 0.5f);
		}
	}

	private void renderRiver () {
		//TODO
	}

}
