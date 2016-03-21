package de.lman.engine.math;

public class Vec2f {
	public float x;
	public float y;
	
	public Vec2f() {
		x = y = 0f;
	}
	
	public Vec2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2f(Vec2f v) {
		x = v.x;
		y = v.y;
	}
	
	public Vec2f set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public Vec2f set(float s) {
		this.x = s;
		this.y = s;
		return this;
	}

	public Vec2f set(Vec2f v) {
		this.x = v.x;
		this.y = v.y;
		return this;
	}
	
	public Vec2f zero() {
		x = y = 0f;
		return this;
	}
	
	public Vec2f min(Vec2f a, Vec2f b) {
		x = Math.min(a.x, b.x);
		y = Math.min(a.y, b.y);
		return this;
	}

	public Vec2f max(Vec2f a, Vec2f b) {
		x = Math.max(a.x, b.x);
		y = Math.max(a.y, b.y);
		return this;
	}

	public Vec2f add(Vec2f v) {
		x += v.x;
		y += v.y;
		return this;
	}
	
	public Vec2f sub(Vec2f v) {
		x -= v.x;
		y -= v.y;
		return this;
	}

	public Vec2f hadamard(Vec2f v) {
		x *= v.x;
		y *= v.y;
		return this;
	}
	
	public Vec2f multScalar(float s) {
		x *= s;
		y *= s;
		return this;
	}
	
	public Vec2f addMultScalar(Vec2f v, float s) {
		x += v.x * s;
		y += v.y * s;
		return this;
	}

	public Vec2f addHadamard(Vec2f a, Vec2f b) {
		x += a.x * b.x;
		y += a.y * b.y;
		return this;
	}

	public float dot(Vec2f v) {
		return x * v.x + y * v.y;
	}
	
	public float lengthSquared() {
		return dot(this);
	}
	
	public float length() {
		return (float)Math.sqrt(lengthSquared());
	}
	
	public Vec2f normalize() {
		float l = length();
		if (l == 0) {
			l = 1;
		}
		float invLen = 1.0f / l;
		x *= invLen;
		y *= invLen;
		return this;
	}
	
	public Vec2f perpLeft() {
		float tmp = x;
		this.x = -y;
		this.y = tmp;
		return this;
	}

	public Vec2f perpRight() {
		float tmp = x;
		this.x = y;
		this.y = -tmp;
		return this;
	}

	@Override
	public String toString() {
		return String.format("(%f, %f)", x, y);
	}

	public Vec2f invert() {
		x = -x;
		y = -y;
		return this;
	}

	public Vec2f abs() {
		x = Math.abs(x);
		y = Math.abs(y);
		return this;
	}

	public Vec2f transform(Transform t) {
		return mult(t.q).add(t.p);
	}

	public Vec2f mult(Mat2f q) {
		float nx = dot(q.col1);
		float ny = dot(q.col2);
		x = nx;
		y = ny;
		return this;
	}
	
}
