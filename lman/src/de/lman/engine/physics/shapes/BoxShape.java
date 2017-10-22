package de.lman.engine.physics.shapes;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.AABB;

public class BoxShape extends Shape implements EdgeShape {
	private final Vec2f[] localVertices = new Vec2f[4];
	private final int numVertices = 4;
	public final Vec2f radius = new Vec2f();

	public BoxShape(Vec2f radius) {
		super(ShapeType.Box);
		this.radius.set(radius);
		localVertices[0] = new Vec2f(+radius.x, +radius.y);
		localVertices[1] = new Vec2f(-radius.x, +radius.y);
		localVertices[2] = new Vec2f(-radius.x, -radius.y);
		localVertices[3] = new Vec2f(+radius.x, -radius.y);
	}

	public void resize(Vec2f radius) {
		this.radius.set(radius);
		localVertices[0].set(+radius.x, +radius.y);
		localVertices[1].set(-radius.x, +radius.y);
		localVertices[2].set(-radius.x, -radius.y);
		localVertices[3].set(+radius.x, -radius.y);
	}

	@Override
	public int getVertexCount() {
		return (numVertices);
	}

	@Override
	public Vec2f[] getLocalVertices() {
		return (localVertices);
	}

	@Override
	public void updateLocalAABB(AABB aabb, Transform t, float tolerance) {
		Vec2f e = new Vec2f(tolerance, tolerance);
		aabb.min.zero();
		aabb.max.zero();
		Vec2f[] transformedVerts = new Vec2f[4];
		for (int i = 0; i < numVertices; i++) {
			Vec2f v = transformedVerts[i] = new Vec2f(localVertices[i]).mult(t.q);
			aabb.min.x = Math.min(aabb.min.x, v.x);
			aabb.min.y = Math.min(aabb.min.y, v.y);
			aabb.max.x = Math.max(aabb.max.x, v.x);
			aabb.max.y = Math.max(aabb.max.y, v.y);
		}
		aabb.min.add(t.p);
		aabb.max.add(t.p);
		aabb.min.sub(e);
		aabb.max.add(e);
	}

	@Override
	public float computeMass(float density) {
		float w = radius.x * 2;
		float h = radius.y * 2;
		return (w * h) * density;
	}
}
