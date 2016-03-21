package de.lman.engine.physics.contacts;

import de.lman.engine.math.Vec2f;

public class Contact {
	public static float VERTEX_EPSILON = 0.000001f;
	
	public final ContactType type;
	public final Vec2f normal = new Vec2f();
	public final float distance;
	public final Vec2f pointA = new Vec2f();
	public float normalImpulse;
	public float tangentImpulse;
	
	public Contact(Vec2f normal, float distance, Vec2f pointA, ContactType type) {
		this.type = type;
		this.normal.set(normal);
		this.distance = distance;
		this.pointA.set(pointA);
		this.normalImpulse = 0f;
		this.tangentImpulse = 0f;
	}
}
