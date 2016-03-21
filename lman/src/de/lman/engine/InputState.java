package de.lman.engine;

import de.lman.engine.math.Vec2f;

public class InputState {
	public final boolean[] keyState = new boolean[128];
	public final boolean[] mouseState = new boolean[16];
	public final Vec2f mousePos = new Vec2f();

	public InputState() {
		
	}

	public boolean isKeyDown(int keyCode) {
		return keyState[keyCode];
	}

	public boolean isKeyDown(Keys key) {
		return isKeyDown(key.key);
	}

	public void setKeyDown(int keyCode, boolean value) {
		keyState[keyCode] = value;
	}

	public void setKeyDown(Keys key, boolean value) {
		setKeyDown(key.key, value);
	}

	public boolean isMouseDown(int button) {
		return mouseState[button];
	}

	public boolean isMouseDown(Mouse button) {
		return isMouseDown(button.btn);
	}
}
