package de.lman.engine.physics;

import de.lman.engine.math.Scalar;
import de.lman.engine.math.Vec2f;

public class ClosestPoints {
	
	public static float onLineSegment(Vec2f p, Vec2f a, Vec2f b, Vec2f out) {
		Vec2f lineDistance = new Vec2f(b).sub(a);
		Vec2f pointToLine = new Vec2f(p).sub(a);
		float region = pointToLine.dot(lineDistance) / lineDistance.lengthSquared();
		float percentage = Scalar.clamp(region);
		Vec2f pointOnLine = new Vec2f(a).addMultScalar(lineDistance, percentage);
		out.set(pointOnLine);
		return region;
	}
	
}
