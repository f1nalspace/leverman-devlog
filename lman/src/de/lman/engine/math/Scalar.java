package de.lman.engine.math;

public class Scalar {
	public final static float PI = (float) Math.PI;
	public final static float TWOPI = PI * 2;
	public final static float FLOAT_TOLERANCE = 0.0001f;
	
	public static int roundUp(float value) {
		return (int)(value + 0.5f);
	}
	
	public static int clampInt(int value, int min, int max) {
		int result = Math.max(Math.min(value, max), min);
		return (result);
	}

	public static float clamp(float value, float min, float max) {
		float result = Math.max(Math.min(value, max), min);
		return (result);
	}

	public static float clamp(float value) {
		return clamp(value, 0f, 1f);
	}

	public static float sign(float value) {
		float result;
		if (value < 0) {
			result = -1;
		} else {
			result = 1;
		}
		return (result);
	}

	public static boolean equals(float value, float expected) {
		float v = Math.abs(value - expected);
		boolean result = v < FLOAT_TOLERANCE;
		return (result);
	}
	
	public static boolean greater(float value, float expected) {
		float v = value - expected;
		boolean result = v > FLOAT_TOLERANCE;
		return (result);
	}
	
	public static float lerp(float v0, float v1, float t) {
		float result = v0 * (1.0f - t) + v1 * t;
		return (result);
	}

	public static float fastLerp(float v0, float v1, float t) {
		float result = v0 + (v1 - v0) * t;
		return (result);
	}
	
	public static float cos(float angle) {
		return (float)Math.cos(angle);
	}

	public static float sin(float angle) {
		return (float)Math.sin(angle);
	}
}
