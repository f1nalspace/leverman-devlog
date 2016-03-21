package de.lman.engine.math;

import de.lman.engine.math.Scalar;
import junit.framework.TestCase;

public class ScalarTest extends TestCase {
	
	public void testLerp() {
		assertEquals(10f, Scalar.lerp(10f, 110f, 0f));
		assertEquals(110f, Scalar.lerp(10f, 110f, 1f));
		assertEquals(60f, Scalar.lerp(10f, 110f, 0.5f));
		assertEquals(35f, Scalar.lerp(10f, 110f, 0.25f));
		assertEquals(85f, Scalar.lerp(10f, 110f, 0.75f));

		assertEquals(110f, Scalar.lerp(110f, 10f, 0f));
		assertEquals(10f, Scalar.lerp(110f, 10f, 1f));
		assertEquals(60f, Scalar.lerp(110f, 10f, 0.5f));
		assertEquals(85f, Scalar.lerp(110f, 10f, 0.25f));
		assertEquals(35f, Scalar.lerp(110f, 10f, 0.75f));
	}
	
}