package me.teamsheepy.sheepherder;

import me.teamsheepy.sheepherder.objects.Pen;
import me.teamsheepy.sheepherder.objects.River;
import me.teamsheepy.sheepherder.objects.Sheep;
import me.teamsheepy.sheepherder.objects.Tree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class WorldRenderer {
	
	static final float SCREEN_WIDTH = 480;
	static final float SCREEN_HEIGHT = 800;
	private World world;
	private SpriteBatch batch;
	ImmediateModeRenderer immediateRenderer;

	public WorldRenderer (SpriteBatch batch, World world) {
		this.world = world;
		this.batch = batch;
		this.immediateRenderer = new ImmediateModeRenderer20(false, true, 0);
	}

	public void render (Vector3 touchPos) {
		renderBackground();
		renderObjects(touchPos);
	}

	public void renderBackground () {
		Gdx.gl.glClearColor(0.172f, 0.690f, 0.212f, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    renderRect(new Vector2(0, 0), new Vector2(World.WORLD_MARGIN, World.WORLD_HEIGHT), World.WORLD_MARGIN_COLOR);
	    renderRect(new Vector2(World.WORLD_WIDTH-World.WORLD_MARGIN, 0), new Vector2(World.WORLD_WIDTH, World.WORLD_HEIGHT), World.WORLD_MARGIN_COLOR);
	    renderRect(new Vector2(0, 0), new Vector2(World.WORLD_WIDTH, World.WORLD_MARGIN), World.WORLD_MARGIN_COLOR);
	    renderRect(new Vector2(0, World.WORLD_HEIGHT-World.WORLD_MARGIN), new Vector2(World.WORLD_WIDTH, World.WORLD_HEIGHT), World.WORLD_MARGIN_COLOR);
	}

	//bottom left & top right coords
	private void renderRect(Vector2 p1, Vector2 p2, Color color){
		immediateRenderer.begin(batch.getProjectionMatrix(), GL20.GL_TRIANGLE_STRIP);
		immediateRenderer.color(color);
	    immediateRenderer.vertex(p1.x, p1.y, 0);
	    immediateRenderer.color(color);
	    immediateRenderer.vertex(p2.x, p1.y, 0);
	    immediateRenderer.color(color);
	    immediateRenderer.vertex(p2.x, p2.y, 0);
	    immediateRenderer.color(color);
	    immediateRenderer.vertex(p2.x, p2.y, 0);
	    immediateRenderer.color(color);
	    immediateRenderer.vertex(p1.x, p2.y, 0);
	    immediateRenderer.color(color);
	    immediateRenderer.vertex(p1.x, p1.y, 0);
	    immediateRenderer.end();
	}
	
	public void renderObjects (Vector3 touchPos) {
		batch.enableBlending();
		batch.begin();
		renderRivers();
		renderSheeps();
		renderPen();
		renderTrees();
		renderTouchMarker(touchPos);
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
	
	private void renderTouchMarker(Vector3 touchPos) {
		if (Gdx.input.isTouched()) {
			batch.draw(Assets.touchmarker, 
					touchPos.x - Assets.touchmarker.getRegionWidth()/2/10*7, // x position
					touchPos.y - Assets.touchmarker.getRegionHeight()/2/10*7, // y position
					0, 0, //origin
					Assets.touchmarker.getRegionWidth(), Assets.touchmarker.getRegionHeight(), // width + height
					0.7f, 0.7f,  //scale
					0); //rotation
		}
	}

}
