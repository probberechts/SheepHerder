package me.teamsheepy.sheepherder.utils;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

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

	public long getMinTouchTime() {
		try {
			return Collections.min(touchTimes);
		} catch (Exception e) {
			return 0;
		}
	}

	public long getMaxTouchTime() {
		try {
			return Collections.max(touchTimes);
		} catch (Exception e) {
			return 0;
		}
	}

	public long getAverageTouchTime() {
		long sum = 0;
		if(!touchTimes.isEmpty()) {
			for (long mark : touchTimes) {
				sum += mark;
			}
			return sum / touchTimes.size();
		}
		return sum;
	}

	public int countTaps() {
		return taps;
	}

	public int countSwipes() {
		return swipes;
	}
}
