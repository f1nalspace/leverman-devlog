package de.lman.engine.physics.shapes;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.AABB;

public class PolygonShape extends Shape implements EdgeShape {

	private final Vec2f[] localVertices;
	private final int numVertices;

	public PolygonShape(Vec2f[] vertices) {
		super(ShapeType.Polygon);
		numVertices = vertices.length;
		localVertices = new Vec2f[numVertices];
		for (int i = 0; i < numVertices; i++) {
			localVertices[i] = new Vec2f(vertices[i]);
		}
	}

	@Override
	public int getVertexCount() {
		return numVertices;
	}

	@Override
	public Vec2f[] getLocalVertices() {
		return localVertices;
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
		float areaTimes2 = 0;
		for (int j = 0; j < numVertices; j++) {
			Vec2f v0 = localVertices[j];
			Vec2f v1 = localVertices[(j + 1) % numVertices];
			areaTimes2 += (v0.y + v1.y) * (v0.x - v1.x);
		}
		return areaTimes2 * 0.5f * density;
	}

}
