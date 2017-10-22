package de.lman.engine.physics.contacts;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;

public class ManifoldInput {
	public final boolean wasFaceA;
	public final Vec2f normal;
	public final Vec2f[] vertsA;
	public final  Vec2f[] vertsB;
	public final  int numVertsA;
	public final  int numVertsB;
	public final Transform transformA;
	public final Transform transformB;
	
	public ManifoldInput(boolean wasFaceA, Transform transformA, Transform transformB, Vec2f normal, Vec2f[] vertsA, Vec2f[] vertsB, int numVertsA, int numVertsB) {
		this.wasFaceA = wasFaceA;
		this.transformA = transformA;
		this.transformB = transformB;
		this.normal = normal;
		this.vertsA = vertsA;
		this.vertsB = vertsB;
		this.numVertsA = numVertsA;
		this.numVertsB = numVertsB;
	}
}
