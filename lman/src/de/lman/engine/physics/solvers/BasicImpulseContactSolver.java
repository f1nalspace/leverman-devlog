package de.lman.engine.physics.solvers;

import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.BodyShapePair;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactSolver;

public class BasicImpulseContactSolver extends ContactSolver {

	public BasicImpulseContactSolver(int velocityIterations, int positionIterations) {
		super(velocityIterations, positionIterations);
	}

	@Override
	public void solveVelocity(float dt, int numPairs, BodyShapePair[] pairs) {
		// TODO: Material-Eigenschaften
		final float restitution = 0.0f;

		for (int j = 0; j < velocityIterations; j++) {
			for (int i = 0; i < numPairs; i++) {
				BodyShapePair pair = pairs[i];
				Body bodyA = pair.a;
				Body bodyB = pair.b;
				Vec2f vA = bodyA.vel;
				Vec2f vB = bodyB.vel;
				float impulseWeightA = bodyA.invMass;
				float impulseWeightB = bodyB.invMass;
				float impulseRatio = 1.0f / (impulseWeightA + impulseWeightB);
				for (int k = 0; k < pair.numContacts; k++) {
					Contact contact = pair.contacts[k];
					Vec2f normal = contact.normal;
					Vec2f vAB = new Vec2f(vB).sub(vA);
					float projRelVel = vAB.dot(normal);
					float impulse = Math.min((1.0f + restitution) * projRelVel * impulseRatio, 0.0f);
					vA.addMultScalar(normal, impulse * impulseWeightA);
					vB.addMultScalar(normal, -impulse * impulseWeightB);
				}
			}
		}
	}

	@Override
	public void solvePosition(float dt, int numPairs, BodyShapePair[] pairs) {
		final float minDistance = 0.01f;
		final float maxCorrection = 0.5f;
		for (int i = 0; i < numPairs; i++) {
			BodyShapePair pair = pairs[i];
			Body bodyA = pair.a;
			Body bodyB = pair.b;
			float impulseWeightA = bodyA.invMass;
			float impulseWeightB = bodyB.invMass;
			float impulseRatio = 1.0f / (impulseWeightA + impulseWeightB);
			for (int k = 0; k < pair.numContacts; k++) {
				Contact contact = pair.contacts[k];
				Vec2f normal = contact.normal;
				float correction = Math.min((contact.distance + minDistance) * maxCorrection * impulseRatio, 0.0f);
				bodyA.pos.addMultScalar(normal, correction * impulseWeightA);
				bodyB.pos.addMultScalar(normal, -correction * impulseWeightB);
			}
		}
	}

	@Override
	public boolean accept(Contact contact) {
		return contact.distance < 0;
	}

}
