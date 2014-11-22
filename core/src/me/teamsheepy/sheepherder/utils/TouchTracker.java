package me.teamsheepy.sheepherder.utils;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import me.teamsheepy.sheepherder.SheepHerder;
import me.teamsheepy.sheepherder.SheepWorld;

import com.badlogic.gdx.input.GestureDetector;
import me.teamsheepy.sheepherder.objects.Sheep;

import java.util.ArrayList;
import java.util.Collections;

public class TouchTracker extends InputAdapter {

	private long startTime;
	private Vector2 startPos;
	private int taps;
	private int swipes;
	private ArrayList<Long> touchTimes = new ArrayList<Long>();

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		startTime = System.currentTimeMillis();
		startPos = new Vector2(screenX, screenY);
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		try {
			Vector2 endPos = new Vector2(screenX, screenY);
			if(startPos.dst2(endPos) > 20) {
				swipes++;
				touchTimes.add(System.currentTimeMillis() - startTime);
			} else {
				taps++;
			}
		} catch (Exception e) {}
		return super.touchUp(screenX, screenY, pointer, button);
	}

	public int getMinTouchTime() {
		return Collections.min(touchTimes).intValue();
	}

	public int getMaxTouchTime() {
		return Collections.max(touchTimes).intValue();
	}

	public int getAvarageTouchTime() {
		long sum = 0;
		if(!touchTimes.isEmpty()) {
			for (long mark : touchTimes) {
				sum += mark;
			}
			return (int) sum / touchTimes.size();
		}
		return (int) sum;
	}

	public int countTaps() {
		return taps;
	}

	public int countSwipes() {
		return swipes;
	}
}
