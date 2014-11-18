package me.teamsheepy.sheepherder.utils;

import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.SheepWorld;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;

public class SwipeDetector implements InputProcessor{

	private SheepWorld world;
	
	public SwipeDetector(SheepWorld world){
		this.world = world;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(world.swipeTime == 0){
			System.out.println("swiped "+screenX+" "+screenY+" "+pointer);
			world.swipeTime = SheepWorld.GAME_TIME-world.timeLeft;
			SheepHerder.analytics.trackEvent("gameEvent", "firstSwipe", "swipeTime", world.swipeTime);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
