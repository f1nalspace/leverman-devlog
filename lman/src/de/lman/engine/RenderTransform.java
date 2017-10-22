package de.lman.engine;

import de.lman.engine.math.Vec2f;

public class RenderTransform {
	public final Vec2f p = new Vec2f();
	public final Vec2f s = new Vec2f();
	
	public RenderTransform(Vec2f p, Vec2f s) {
		this.p.set(p);
		this.s.set(s);
	}
}
