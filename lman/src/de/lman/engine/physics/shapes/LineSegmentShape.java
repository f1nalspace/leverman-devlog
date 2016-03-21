package de.lman.engine.physics.shapes;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.AABB;

public class LineSegmentShape extends Shape implements EdgeShape {
	private final Vec2f[] vertices = new Vec2f[2];
	private final int numVertices = 2;
	
	public LineSegmentShape(float halfDistance) {
		super(ShapeType.LineSegment);
		vertices[0] = new Vec2f(halfDistance, 0);
		vertices[1] = new Vec2f(-halfDistance, 0);
	}
	
	@Override
	public int getVertexCount() {
		return numVertices;
	}
	
	@Override
	public Vec2f[] getLocalVertices() {
		return (vertices);
	}

	@Override
	public void updateLocalAABB(AABB aabb, Transform t, float tolerance) {
		Vec2f e = new Vec2f(tolerance, tolerance);
		aabb.min.zero();
		aabb.max.zero();
		Vec2f[] transformedVerts = new Vec2f[numVertices];
		for (int i = 0; i < numVertices; i++) {
			Vec2f v = transformedVerts[i] = new Vec2f(vertices[i]).mult(t.q);
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
		return 0;
	}

}
