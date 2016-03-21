package de.lman.engine.math;

public class Mat2f {
	public final Vec2f col1 = new Vec2f(1, 0);
	public final Vec2f col2 = new Vec2f(0, 1);
	
	public Mat2f() {}
	
	public Mat2f(float angle) {
		col1.set(Scalar.cos(angle), Scalar.sin(angle));
		col2.set(-Scalar.sin(angle), Scalar.cos(angle));
	}
	
	public Mat2f(Vec2f a, Vec2f b) {
		col1.set(a);
		col2.set(b);
	}

	public Mat2f(Mat2f m) {
		col1.set(m.col1);
		col2.set(m.col2);
	}
	
	@Override
	public String toString() {
		return String.format("%s , %s", col1, col2);
	}

	public Mat2f set(float angle) {
		col1.set(Scalar.cos(angle), Scalar.sin(angle));
		col2.set(-Scalar.sin(angle), Scalar.cos(angle));
		return this;
	}

	public Mat2f set(Mat2f m) {
		col1.set(m.col1);
		col2.set(m.col2);
		return this;
	}

	public Mat2f transpose() {
		float col1x = col1.x;
		float col1y = col1.y;
		float col2x = col2.x;
		float col2y = col2.y;
		col1.x = col1x;
		col1.y = col2x;
		col2.x = col1y;
		col2.y = col2y;
		return this;
	}
}
