package me.teamsheepy.sheepherder.utils;

import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.World;

import com.badlogic.gdx.input.GestureDetector;

public class SwipeDetector2 extends GestureDetector.GestureAdapter{

	private World world;
	
	public SwipeDetector2(World world){
		this.world = world;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if(world.state == World.WORLD_STATE_RUNNING && world.swipeTime == 0){
			world.swipeTime = World.GAME_TIME-world.timeLeft;
			SheepHerder.analytics.trackEvent("gameEvent", "firstSwipe", "swipeTime", world.swipeTime);
		}
		return false;
	}
}
