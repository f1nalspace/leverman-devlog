package de.lman.engine.physics.solvers;

import de.lman.engine.math.Scalar;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.BodyShapePair;
import de.lman.engine.physics.Physics;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactSolver;

public class SequentialImpulseContactSolver extends ContactSolver {

	public SequentialImpulseContactSolver(int velocityIterations, int positionIterations) {
		super(velocityIterations, positionIterations);
	}

	@Override
	public void solveVelocity(float dt, int numPairs, BodyShapePair[] pairs) {
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
					float projVel = vAB.dot(normal);
					float bias = Physics.BAUMGARTE_STABILIZATION / dt * Math.min((contact.distance + Physics.MIN_DISTANCE), 0);
					float normalImpulse = (projVel + bias) * impulseRatio;

					// Normaler Impulse
					float newNormalImpulse = Math.min(normalImpulse + contact.normalImpulse, 0.0f);
					normalImpulse = newNormalImpulse - contact.normalImpulse;
					contact.normalImpulse = newNormalImpulse;
					vA.addMultScalar(normal, normalImpulse * impulseWeightA);
					vB.addMultScalar(normal, -normalImpulse * impulseWeightB);
					
					// Relative Geschwindigkeit neuberechnen
					vAB = new Vec2f(vB).sub(vA);
					Vec2f tangent = new Vec2f(normal).perpRight();
					final float maxFriction = contact.normalImpulse * pair.friction;
					float projTangentVel = vAB.dot(tangent);
					float tangentImpulse = projTangentVel * impulseRatio;
					float newTangentImpulse = Scalar.clamp(tangentImpulse + contact.tangentImpulse, maxFriction, -maxFriction);
					tangentImpulse = newTangentImpulse - contact.tangentImpulse;
					contact.tangentImpulse = newTangentImpulse;
					vA.addMultScalar(tangent, tangentImpulse * impulseWeightA);
					vB.addMultScalar(tangent, -tangentImpulse * impulseWeightB);
					
				}
			}
		}
	}

	@Override
	public void solvePosition(float dt, int numPairs, BodyShapePair[] pairs) {}

	@Override
	public boolean accept(Contact contact) {
		return contact.distance < 0;
	}

}
