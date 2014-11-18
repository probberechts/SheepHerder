package me.teamsheepy.sheepherder.utils;

import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.SheepWorld;

import com.badlogic.gdx.input.GestureDetector;

public class SwipeDetector2 extends GestureDetector.GestureAdapter{

	private SheepWorld world;
	
	public SwipeDetector2(SheepWorld world){
		this.world = world;
	}
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if(world.state == SheepWorld.WORLD_STATE_RUNNING && world.swipeTime == 0){
			world.swipeTime = SheepWorld.GAME_TIME-world.timeLeft;
			SheepHerder.analytics.trackEvent("gameEvent", "firstSwipe", "swipeTime", world.swipeTime);
		}
		return false;
	}
}
