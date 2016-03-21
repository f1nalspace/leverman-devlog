package de.lman.engine.physics;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.shapes.PhysicsMaterial;
import de.lman.engine.physics.shapes.Shape;

public class Body {
	private static int ID_COUNTER = 0;
	public final int id = ++ID_COUNTER;

	public final Vec2f pos = new Vec2f();
	public final Vec2f vel = new Vec2f();
	public final Vec2f acc = new Vec2f();
	public float invMass = 0;
	public final AABB aabb = new AABB();

	private static final int MAX_SHAPES = 8;
	public final Shape[] shapes = new Shape[MAX_SHAPES];
	public int numShapes = 0;

	public Body() {
		this.invMass = 0;
	}

	public Body addShape(Shape shape) {
		assert (numShapes < MAX_SHAPES);
		shapes[numShapes++] = shape;
		updateMass();
		return this;
	}

	public void updateMass() {
		float totalMass = 0f;
		for (int i = 0; i < numShapes; i++) {
			Shape shape = shapes[i];
			PhysicsMaterial mat = shape.material;
			if (mat != null && mat.density > 0) {
				float mass = shape.computeMass(mat.density);
				totalMass += mass;
			}
		}
		invMass = totalMass > 0.0f ? 1.0f / totalMass : 0.0f;
	}

	public void updateAABB(Vec2f prevPos, Vec2f nextPos, float tolerance) {
		aabb.min.set(Float.MAX_VALUE);
		aabb.max.set(Float.MIN_VALUE);
		Vec2f diff = new Vec2f(nextPos).sub(prevPos);
		for (int i = 0; i < numShapes; i++) {
			Shape shape = shapes[i];
			Transform t = new Transform(shape.localPos, shape.localRotation);
			AABB shapeAABB = new AABB();
			shape.updateLocalAABB(shapeAABB, t, tolerance);
			aabb.min.x = Math.min(aabb.min.x, shapeAABB.min.x); 
			aabb.min.y = Math.min(aabb.min.y, shapeAABB.min.y);
			aabb.max.x = Math.max(aabb.max.x, shapeAABB.max.x); 
			aabb.max.y = Math.max(aabb.max.y, shapeAABB.max.y);
		}
		Vec2f diffAbs = new Vec2f(diff).multScalar(0.5f).abs();
		aabb.min.add(prevPos).addMultScalar(diff, 0.5f).sub(diffAbs);
		aabb.max.add(prevPos).addMultScalar(diff, 0.5f).add(diffAbs);
	}
}
