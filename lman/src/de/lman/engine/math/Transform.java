package de.lman.engine.math;

public class Transform {
	public final Mat2f q = new Mat2f();
	public final Vec2f p = new Vec2f();
	
	public Transform(Vec2f p, float angle) {
		this.p.set(p);
		this.q.set(angle);
	}

	public Transform(Mat2f q, Vec2f p) {
		this.q.set(q);
		this.p.set(p);
	}

	public Transform offset(Vec2f v) {
		p.add(v);
		return this;
	}
}
