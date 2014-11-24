package be.teamsheepy.sheepherder.objects;

import java.util.ArrayList;
import java.util.List;

import be.teamsheepy.sheepherder.Assets;
import be.teamsheepy.sheepherder.SheepWorld;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class River extends GameObject {
	
	public static float RIVER_WIDTH = SheepWorld.WORLD_WIDTH;
	public static float RIVER_HEIGHT = 38;
	private int numBridges;
    public Array<Bridge> bridges;
    
    public List<Body> bodies;
	
	public River (float x, float y, int numBridges, World world) {
		super(x, y, RIVER_WIDTH, RIVER_HEIGHT);
		this.numBridges = numBridges;
		bodies = new ArrayList<Body>();
		init(world);
	}
	
	private void init (World world) {
		// MARGE = the space next to a bridge which isn't collidable
		int MARGE = (int) (Sheep.SHEEP_WIDTH / 2);
		bridges = new Array<Bridge>();
		int numRiverParts = numBridges + 1;
		float riverPartLength = River.RIVER_WIDTH / numRiverParts;
		for (int i = 0; i < numRiverParts; i++) {
			// construct collision area
			Rectangle part;
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.StaticBody;
			Body body;
			int bridgeXpos = 0;
			if (i == 0) {
				// first river part
				float width = riverPartLength - MARGE / 2 - Bridge.BRIDGE_WIDTH / 2 + 50;
				bodyDef.position.set(this.position.x - 50 + width / 2, this.position.y + bounds.height / 2);
				body = world.createBody(bodyDef);
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(width / 2, bounds.height / 2);
				FixtureDef fixtureDef = new FixtureDef();
			    fixtureDef.shape = shape;
			    fixtureDef.density = 1f;

			    Fixture fixture = body.createFixture(fixtureDef);

			    shape.dispose();
			    
				part = new Rectangle(this.position.x - 50, this.position.y, 
						riverPartLength - MARGE / 2 - Bridge.BRIDGE_WIDTH / 2 + 50, bounds.height);
			} else if (i == numRiverParts - 1) {
				// last river part
				float width = riverPartLength + 50;
				bodyDef.position.set(this.position.x + i * riverPartLength + MARGE / 2 + Bridge.BRIDGE_WIDTH  / 2 + width / 2, this.position.y + bounds.height / 2);
				body = world.createBody(bodyDef);
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(width / 2, bounds.height / 2);
				FixtureDef fixtureDef = new FixtureDef();
			    fixtureDef.shape = shape;
			    fixtureDef.density = 1f;

			    Fixture fixture = body.createFixture(fixtureDef);

			    shape.dispose();
			    
				part = new Rectangle(this.position.x + i * riverPartLength + MARGE / 2 + Bridge.BRIDGE_WIDTH  / 2, this.position.y, 
						riverPartLength + 50, bounds.height);
			} else {
				// other river parts
				float width = riverPartLength - MARGE - Bridge.BRIDGE_WIDTH;
				bodyDef.position.set(this.position.x + i * riverPartLength + MARGE / 2 + Bridge.BRIDGE_WIDTH / 2 + width / 2, this.position.y + bounds.height / 2);
				body = world.createBody(bodyDef);
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(width / 2, bounds.height / 2);
				FixtureDef fixtureDef = new FixtureDef();
			    fixtureDef.shape = shape;
			    fixtureDef.density = 1f;

			    Fixture fixture = body.createFixture(fixtureDef);

			    shape.dispose();
			    
				part = new Rectangle(this.position.x + i * riverPartLength + MARGE / 2 + Bridge.BRIDGE_WIDTH / 2, this.position.y, 
						riverPartLength - MARGE - Bridge.BRIDGE_WIDTH, bounds.height);
			}
			collisionAreas.add(part);
			bodies.add(body);
			// construct bridges
			if (i>0) bridges.add(new Bridge(i * riverPartLength - Bridge.BRIDGE_WIDTH/2, this.position.y));
		}
	}
	
	
	

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(Assets.river, position.x, position.y);
		for (Bridge bridge : bridges)
			bridge.render(batch);
	}
	
}
