package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Assets;

public class River extends GameObject {
	
	public static float RIVER_WIDTH = 600;
	public static float RIVER_HEIGHT = 38;
	private int numBridges;
	private Array<TextureRegion> regRiverParts;
    private Array<RiverPart> riverParts;
    private Array<Bridge> bridges;
	
	private class RiverPart extends GameObject {

		private TextureRegion regRiverPart;
		
		public RiverPart(float x, float y, float width, float height, int rotation, TextureRegion reg) {
			super(x, y, width, height);
			this.rotation = rotation;
			this.regRiverPart = reg;
		}
		
		@Override
		public void render (SpriteBatch batch) {
			TextureRegion reg = regRiverPart;
			batch.draw(reg.getTexture(), 
						position.x, position.y,
	                    center.x, center.y,
	                    bounds.width, bounds.height,
	                    1, 1,
	                    rotation,
	                    reg.getRegionX(), reg.getRegionY(),
	                    reg.getRegionWidth(), reg.getRegionHeight(),
	                    false, false);
		}
	}

	public River (float x, float y, int rotation, int numBridges) {
		super(x, y, RIVER_WIDTH, RIVER_HEIGHT);
		this.rotation = rotation;
		this.numBridges = numBridges;
		init();
	}
	
	private void init () {
		riverParts = new Array<RiverPart>();
		regRiverParts = new Array<TextureRegion>();
		bridges = new Array<Bridge>();
		int numRiverParts = numBridges + 1;
		int riverPartLength = (int) (River.RIVER_WIDTH / numRiverParts);
		for (int i = 0; i < numRiverParts; i++) {
			TextureRegion reg = new TextureRegion(Assets.river, 
					(int) (i * riverPartLength), 0,
					riverPartLength, Assets.river.getHeight());
			regRiverParts.add(reg);
			RiverPart part = new RiverPart((float) (this.position.x + i * riverPartLength), 
					(float) (this.position.y + i * riverPartLength * Math.sin(Math.toRadians(rotation))), 
					riverPartLength, bounds.height, rotation, reg);
			riverParts.add(part);
			if (i>0) bridges.add(new Bridge((float) (i * riverPartLength - i * Bridge.BRIDGE_WIDTH), (float) (this.position.y + i * riverPartLength * Math.sin(Math.toRadians(rotation)))));
		}
	}

	@Override
	public void render(SpriteBatch batch) {
		for (RiverPart part : riverParts)
			part.render(batch);
		for (Bridge bridge : bridges)
			bridge.render(batch);
	}

	public boolean canPass(Sheep sheep) {
		System.out.println(bounds);
		for (Bridge bridge : bridges) {
			if (Math.abs(sheep.bounds.x - bridge.bounds.x) < sheep.bounds.width / 3f ||
					Math.abs(sheep.bounds.x+sheep.bounds.width - bridge.bounds.x + bridge.bounds.width) < sheep.bounds.width / 3f)
				return true;
		}
		return false;
	}
}
