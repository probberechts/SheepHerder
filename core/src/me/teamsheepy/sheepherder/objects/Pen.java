package me.teamsheepy.sheepherder.objects;

import java.util.ArrayList;
import java.util.List;

import me.teamsheepy.sheepherder.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


public class Pen extends GameObject {
	public static float PEN_WIDTH = 200;
	public static float PEN_HEIGHT = 225;

	private Rectangle scoreZone;
	private List<Body> bodies;
	
	public Pen (float x, float y, World world) {
		super(x, y, PEN_WIDTH, PEN_HEIGHT);
		bodies = new ArrayList<Body>();
		bounds.height -= 30; 
		initScoreZone();
		initCollisionZones(world);
	}
	
	private void initScoreZone() {
		scoreZone = new Rectangle(bounds.x-10, bounds.y-10, bounds.getWidth()+30, bounds.getHeight()+30);
	}
	
	private void initCollisionZones(World world) {
		AddNewBody(bounds.x + bounds.getWidth() / 2, bounds.y + 186 + 17 / 2, bounds.getWidth(), 17, world);
		AddNewBody(bounds.x + 8 / 2, bounds.y + (bounds.getHeight() - 10) / 2, 8, bounds.getHeight() - 10, world);
		AddNewBody(bounds.x + 100 / 2, bounds.y + 23 / 2, 100, 23, world);
		AddNewBody(bounds.x + 189 + 10 / 2, bounds.y + (bounds.getHeight() - 10) / 2, 10, bounds.getHeight() - 10, world);
		
		Rectangle top = new Rectangle(bounds.x, bounds.y + 186, bounds.getWidth(), 17);
		Rectangle left = new Rectangle(bounds.x, bounds.y, 8, bounds.getHeight() - 10);
		Rectangle bottom = new Rectangle(bounds.x, bounds.y, 100, 23);
		Rectangle right = new Rectangle(bounds.x + 189, bounds.y, 10, bounds.getHeight() - 10);
		collisionAreas.add(top);
		collisionAreas.add(left);
		collisionAreas.add(right);
		collisionAreas.add(bottom);
	}
	
	private void AddNewBody(float xPos, float yPos, float width, float height, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(xPos, yPos);
		Body body = world.createBody(bodyDef);
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(width / 2, height / 2);
		FixtureDef fixtureDef = new FixtureDef();
	    fixtureDef.shape = shape;
	    fixtureDef.density = 1f;

	    Fixture fixture = body.createFixture(fixtureDef);

	    shape.dispose();
	    bodies.add(body);
	}
	
	public boolean hasScored(Rectangle object) {
		return scoreZone.contains(object);
	}

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(Assets.pen, position.x, position.y);
	}
}
