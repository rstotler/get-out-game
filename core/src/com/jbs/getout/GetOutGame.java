package com.jbs.getout;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.utils.ScreenUtils;
import com.jbs.getout.states.GameState;
import com.jbs.getout.states.MenuState;
import com.jbs.getout.states.State;

public class GetOutGame extends ApplicationAdapter {
	public static final int SCREEN_WIDTH = 540;
	public static final int SCREEN_HEIGHT = 1049;
	public static float WIDTH_RATIO, HEIGHT_RATIO;

	private SpriteBatch spriteBatch;
	private static State state;
	
	@Override
	public void create() {
		WIDTH_RATIO = (SCREEN_WIDTH + 0.0f) / Gdx.graphics.getWidth();
		HEIGHT_RATIO = (SCREEN_HEIGHT + 0.0f) / Gdx.graphics.getHeight();

		spriteBatch = new SpriteBatch();
		state = new GameState();

		// Disable Back Key //
		Gdx.input.setCatchKey(Input.Keys.BACK, true);
	}

	@Override
	public void render() {
		state.update(Gdx.graphics.getDeltaTime());
		state.render(spriteBatch);
	}
	
	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	public static State getState() {
		return state;
	}

	public static void setState(State newState) {
		if(state != null)
			state.dispose();
		state = newState;
	}
}
