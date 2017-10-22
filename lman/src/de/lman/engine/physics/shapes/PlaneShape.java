package de.lman.engine.physics.shapes;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.AABB;

public class PlaneShape extends Shape {
	public final float len;

	public PlaneShape(float len) {
		super(ShapeType.Plane);
		this.len = len;
	}
	
	@Override
	public void updateLocalAABB(AABB aabb, Transform t, float tolerance) {
		Vec2f e = new Vec2f(tolerance, tolerance);
		Vec2f center = new Vec2f(t.p);
		Vec2f normal = t.q.col1;
		Vec2f perp = new Vec2f(normal).perpRight();
		
		Vec2f startPoint = new Vec2f(center).addMultScalar(perp, len * 0.5f);
		Vec2f endPoint = new Vec2f(center).addMultScalar(perp, -len * 0.5f);
		aabb.min.min(startPoint, endPoint);
		aabb.max.max(startPoint, endPoint);
		aabb.min.sub(e);
		aabb.max.add(e);
		
		// TODO: Intelligenter lösen!
		Vec2f depth = new Vec2f(tolerance * 10f, tolerance * 10f);
		if (normal.x > 0.99f || normal.y > 0.99f) {
			aabb.min.addMultScalar(normal, -normal.dot(depth));
		} else if (normal.x < 0.99f || normal.y < 0.99f) {
			aabb.max.addMultScalar(normal, normal.dot(depth));
		}
	}

	@Override
	public float computeMass(float density) {
		return 0;
	}
}
