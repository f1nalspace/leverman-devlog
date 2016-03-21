package de.lman.engine.physics.contacts;

import de.lman.engine.math.Transform;
import de.lman.engine.physics.shapes.Shape;

public interface ContactGenerator {
	int generate(Transform transformA, Transform transformB, Shape shapeA, Shape shapeB, int offset, Contact[] contacts, ContactAcceptor acceptor);
}
