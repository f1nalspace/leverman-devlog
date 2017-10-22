package de.lman.engine.physics.shapes;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.AABB;

public abstract class Shape {
	private static int ID_COUNTER = 0;
	public final int id = ++ID_COUNTER;
	
	public final ShapeType type;
	public final Vec2f localPos = new Vec2f();
	public float localRotation = 0;
	public PhysicsMaterial material;

	public Shape(ShapeType type) {
		this.type = type;
	}
	
	public Shape setMaterial(PhysicsMaterial material) {
		this.material = material;
		return this;
	}
	
	public Shape offset(float x, float y) {
		localPos.set(x, y);
		return this;
	}
	
	public Shape offset(Vec2f v) {
		return offset(v.x, v.y);
	}
	
	public Shape rotation(float rotation) {
		localRotation = rotation;
		return this;
	}
	
	public abstract void updateLocalAABB(AABB aabb, Transform t, float tolerance);

	public abstract float computeMass(float density);
}
