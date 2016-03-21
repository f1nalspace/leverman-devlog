package de.lman.engine.physics;

import de.lman.engine.math.Scalar;
import de.lman.engine.math.Vec2f;

public class SAT {
	public static int getSupportPoints(Vec2f normal, Vec2f[] vertices, int vertexCount, Vec2f[] output, int offset) {
		int firstIndex = 0;
		Vec2f first = vertices[firstIndex];
		float firstDistance = first.dot(normal);
		for (int i = 1; i < vertexCount; i++) {
			Vec2f v = vertices[i];
			float p = v.dot(normal);
			if (Scalar.greater(p, firstDistance)) {
				first = v;
				firstDistance = p;
				firstIndex = i;
			}
		}

		int secondIndex = -1;
		Vec2f second = null;
		float secondDistance = 0;
		for (int i = 0; i < vertexCount; i++) {
			if (i != firstIndex) {
				Vec2f v = vertices[i];
				float p = v.dot(normal);
				if (secondIndex == -1 || Scalar.greater(p, secondDistance)) {
					second = v;
					secondDistance = p;
					secondIndex = i;
				}
			}
		}

		int result = 0;
		output[offset + result++] = first;
		if (second != null) {
			output[offset + result++] = second;
		}

		return (result);
	}

	public static SATResult query(Vec2f[] vertsA, Vec2f[] vertsB, int numVertsA, int numVertsB) {
		SATResult result = new SATResult();
		for (int i = 0; i < vertsA.length; i++) {
			Vec2f v0 = vertsA[i];
			Vec2f v1 = vertsA[(i + 1) % vertsA.length];
			Vec2f n = new Vec2f(v1).sub(v0).perpRight().normalize();
			
			Vec2f[] supportPointsB = new Vec2f[2];
			getSupportPoints(new Vec2f(n).invert(), vertsB, numVertsB, supportPointsB, 0);
			
			Vec2f distanceToLine = new Vec2f(supportPointsB[0]).sub(v0);
			float d = distanceToLine.dot(n);
			
			if (i == 0 || d > result.distance) {
				result.distance = d;
				result.normal.set(n);
			}
		}
		return(result);
	}
}
