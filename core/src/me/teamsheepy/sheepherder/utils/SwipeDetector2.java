package me.teamsheepy.sheepherder.utils;

import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.SheepWorld;

import com.badlogic.gdx.input.GestureDetector;

public class SwipeDetector2 extends GestureDetector.GestureAdapter{

	private SheepWorld world;
	private boolean firstSwipe;
	
	public SwipeDetector2(SheepWorld world){
		this.world = world;
	}
	
	
	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		if(world.state == SheepWorld.WORLD_STATE_RUNNING && !firstSwipe){
			firstSwipe = true;
			SheepHerder.analytics.trackEvent("gameEvent", "firstSwipe", "swipeTime", SheepWorld.GAME_TIME-world.timeLeft);
		}
		world.tapCount = 0;
		return false;
	}
	
	@Override
	public boolean tap(float x, float y, int count, int button) {
		world.tapCount++;
		return false;
	}
}
