package de.lman.engine.physics.shapes;

import de.lman.engine.math.Vec2f;


public interface EdgeShape {
	int getVertexCount();
	Vec2f[] getLocalVertices();
}
