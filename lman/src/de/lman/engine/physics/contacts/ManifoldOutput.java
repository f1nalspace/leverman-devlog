package de.lman.engine.physics.contacts;

import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.BodyShapePair;

public class ManifoldOutput {
	public final Vec2f[] points = new Vec2f[BodyShapePair.MAX_CONTACTS_PER_PAIR];
	public int numPoints = 0;
	public float distance = 0;
	public final Vec2f normal = new Vec2f();
}
