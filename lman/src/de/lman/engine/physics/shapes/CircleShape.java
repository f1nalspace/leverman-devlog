package de.lman.engine.physics.shapes;

import de.lman.engine.math.Scalar;
import de.lman.engine.math.Transform;
import de.lman.engine.physics.AABB;

public class CircleShape extends Shape {
	public final float radius;
	
	public CircleShape(float radius) {
		super(ShapeType.Circle);
		this.radius = radius;
	}

	@Override
	public void updateLocalAABB(AABB aabb, Transform t, float tolerance) {
		final float r = radius + tolerance;
		aabb.min.set(t.p.x - r, t.p.y - r);
		aabb.max.set(t.p.x + r, t.p.y + r);
	}

	@Override
	public float computeMass(float density) {
		return Scalar.PI * radius * radius * density;
	}
}
