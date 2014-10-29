package me.teamsheepy.sheepherder;

import me.teamsheepy.sheepherder.objects.Pen;
import me.teamsheepy.sheepherder.objects.River;
import me.teamsheepy.sheepherder.objects.Sheep;
import me.teamsheepy.sheepherder.objects.Tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {
	
	static final float SCREEN_WIDTH = 480;
	static final float SCREEN_HEIGHT = 800;
	private World world;
	private SpriteBatch batch;

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
		renderRivers();
		renderSheeps();
		renderPen();
		renderTrees();
		batch.end();
	}

	private void renderSheeps () {
		int len = world.sheeps.size();
		for (int i = 0; i < len; i++) {
			Sheep sheep = world.sheeps.get(i);
			sheep.render(batch);
		}
	}

	private void renderPen () {
		Pen pen = world.pen;
		pen.render(batch);
	}

	private void renderTrees () {
		int len = world.trees.size();
		for (int i = 0; i < len; i++) {
			Tree tree = world.trees.get(i);
			tree.render(batch);
		}
	}

	private void renderRivers () {
		int len = world.rivers.size();
		for (int i = 0; i < len; i++) {
			River river = world.rivers.get(i);
			river.render(batch);
		}	
	}

}
