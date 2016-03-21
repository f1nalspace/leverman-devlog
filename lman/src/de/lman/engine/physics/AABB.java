package de.lman.engine.physics;

import de.lman.engine.math.Vec2f;

public class AABB {
	public final Vec2f min = new Vec2f();
	public final Vec2f max = new Vec2f();
	
	public AABB setFrom(AABB a) {
		min.set(a.min);
		max.set(a.max);
		return this;
	}
	
	public AABB extend(float r) {
		min.x -= r;
		min.y -= r;
		max.x += r;
		max.y += r;
		return this;
	}
	
	public static AABB createFromCenter(Vec2f center, Vec2f radius) {
		AABB aabb = new AABB();
		aabb.min.set(center).sub(radius);
		aabb.max.set(center).add(radius);
		return(aabb);
	}
	
	public boolean overlaps(AABB other) {
		float distanceX = max.x - min.x;
		float distanceY = max.y - min.y;
		float otherDistanceX = other.max.x - other.min.x;
		float otherDistanceY = other.max.y - other.min.y;
		float bothRadiusX = (Math.abs(distanceX) + Math.abs(otherDistanceX)) * 0.5f;
		float bothRadiusY = (Math.abs(distanceY) + Math.abs(otherDistanceY)) * 0.5f;
		float otherCenterX = other.min.x + otherDistanceX * 0.5f;
		float otherCenterY = other.min.y + otherDistanceY * 0.5f;
		float centerX = min.x + distanceX * 0.5f;
		float centerY = min.y + distanceY * 0.5f;
		float diffX = Math.abs(centerX - otherCenterX);
		float diffY = Math.abs(centerY - otherCenterY);
		float overlapX = diffX - bothRadiusX;
		float overlapY = diffY - bothRadiusY;
		return !(overlapX > 0 || overlapY > 0);
	}

	
}
