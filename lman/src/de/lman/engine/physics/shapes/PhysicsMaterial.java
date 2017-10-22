package de.lman.engine.physics.shapes;

public class PhysicsMaterial {
	public final float density;
	public final float friction;
	
	public PhysicsMaterial(float density, float friction) {
		this.density = density;
		this.friction = friction;
	}
}
