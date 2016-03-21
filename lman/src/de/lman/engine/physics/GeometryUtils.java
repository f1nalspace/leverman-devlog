package de.lman.engine.physics;

public class GeometryUtils {
	public static boolean isPointInCircle(float x, float y, float cx, float cy, float radius) {
		float dx = x - cx;
		float dy = y - cy;
		float lenSquared = dx * dx + dy * dy;
		return lenSquared <= radius * radius;
	}

	public static boolean isPointInAABB(float x, float y, AABB aabb) {
		if (x < aabb.min.x || x > aabb.max.x) {
			return false;
		}
		if (y < aabb.min.y || y > aabb.max.y) {
			return false;
		}
		return true;
	}

}
