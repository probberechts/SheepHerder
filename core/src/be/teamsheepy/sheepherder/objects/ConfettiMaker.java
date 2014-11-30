package be.teamsheepy.sheepherder.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import be.teamsheepy.sheepherder.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class ConfettiMaker {
	private Vector2 position;
	private Boolean fire;
	private float timeLeft;
	private List<Confetti> confettis;

	public ConfettiMaker() {
		position = new Vector2(-500, -500);
		timeLeft = 0;
		confettis = new ArrayList<Confetti>();
	}
	
	/**
	 * fires a shower of confetti at the given position
	 * @param pos
	 * 			-origin point of the confetti
	 */
	public void fire(Vector2 pos) {
		position = pos;
		timeLeft = 0.5f; 
		
		//create between 5 and 10 random confettis
		Random rand = new Random();
		int amount = rand.nextInt()%50+20;
		for(int i=0;i<amount;++i) {
			Color c = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 1);
			Vector2 direction = new Vector2(rand.nextInt()%7, rand.nextInt()%7);
			confettis.add(new Confetti(new Vector2(position.x, position.y),c,direction));
		}
	}
	
	public void render(SpriteBatch batch, float deltaTime) {
		if(timeLeft > 0) {
			timeLeft -= deltaTime;
			for(Confetti c : confettis) {
				batch.setColor(c.color);
				batch.draw(Assets.confettiWhite, c.position.x, c.position.y);
				c.position.add(c.direction);
			}
		} else confettis.clear();
		batch.setColor(Color.WHITE);
	}
	
	private class Confetti {
		public Vector2 position;
		public Color color;
		public Vector2 direction;
		public Confetti(Vector2 position, Color color, Vector2 direction) {
			this.position = position;
			this.color = color;
			this.direction = direction;
		}
	}
}


