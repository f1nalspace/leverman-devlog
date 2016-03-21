package de.lman.engine.physics.contacts;

import de.lman.engine.physics.BodyShapePair;

public abstract class ContactSolver implements ContactAcceptor {
	protected int velocityIterations;
	protected int positionIterations;

	public ContactSolver(int velocityIterations, int positionIterations) {
		this.velocityIterations = velocityIterations;
		this.positionIterations = positionIterations;
	}
	
	public abstract void solveVelocity(float dt, int numPairs, BodyShapePair[] pairs);
	public abstract void solvePosition(float dt, int numPairs, BodyShapePair[] pairs);
}
