package de.lman.engine.physics;

import java.util.HashMap;
import java.util.Map;

import de.lman.engine.math.Scalar;
import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactGeneratorFactory;
import de.lman.engine.physics.contacts.ContactListener;
import de.lman.engine.physics.contacts.ContactSolver;
import de.lman.engine.physics.contacts.ContactState;
import de.lman.engine.physics.shapes.Shape;
import de.lman.engine.physics.solvers.SpeculativeContactSolver;

public class Physics {

	public static final float AABB_TOLERANCE = 0.1f; // Box2D 0.1f
	public final static float BAUMGARTE_STABILIZATION = 0.2f; // Box2D 0.2f
	public final static float MIN_DISTANCE = 0.005f; // Box2D 0.005f

	private final int MAX_BODIES = 10000;
	public final Body[] bodies;
	public int numBodies = 0;
	
	private final int MAX_BODY_PAIRS = MAX_BODIES * 10;
	public final BodyShapePair[] pairs;
	public int numPairs;

	public int numContacts;
	
	private final Map<Integer, ContactStatePair> prevContactPairs = new HashMap<>();
	private final Map<Integer, ContactStatePair> currContactPairs = new HashMap<>();

	private final ContactGeneratorFactory contactGenFactory;
	
	private ContactSolver contactSolver;
	
	private final ContactListener contactListener;
	
	private boolean singleStepMode = false;
	private boolean newSingleStep = false;

	public Physics(ContactListener contactListener) {
		this.contactListener = contactListener;
		
		bodies = new Body[MAX_BODIES];
		numBodies = 0;
		
		pairs = new BodyShapePair[MAX_BODY_PAIRS];
		numPairs = 0;

		numContacts = 0;

		contactGenFactory = new ContactGeneratorFactory();
		
		contactSolver = new SpeculativeContactSolver(4, 0);
	}
	
	public void enableSingleStepMode(boolean enable) {
		singleStepMode = enable;
		newSingleStep = false;
	}
	
	public void nextStep() {
		newSingleStep = true;
	}

	public Physics addBody(Body body) {
		assert (numBodies < MAX_BODIES);
		bodies[numBodies++] = body;
		return this;
	}
	
	private void beginContact(int hash, ContactStatePair cur, ContactStatePair last) {
		if (contactListener != null) {
			contactListener.physicsBeginContact(hash, cur);
		}
	}

	private void endContact(int hash, ContactStatePair last, ContactStatePair cur) {
		if (last != null && !cur.state.equals(last.state)) {
			if (contactListener != null) {
				contactListener.physicsEndContact(hash, last);
			}
		}
	}

	public void step(float dt) {
		// Einzelschrittmodus
		if (singleStepMode) {
			if (!newSingleStep) {
				return;
			} else {
				newSingleStep = false;
			}
		}
		
		// Schwerkraft
		for (int i = 0; i < numBodies; i++) {
			Body body = bodies[i];
			if (body.invMass > 0) {
				body.acc.y += -10f;
			}
		}

		// Beschleunigung integrieren
		for (int i = 0; i < numBodies; i++) {
			Body body = bodies[i];
			if (body.invMass > 0) {
				body.vel.addMultScalar(body.acc, dt);
				Vec2f nextPos = new Vec2f(body.pos).addMultScalar(body.vel, dt);
				body.updateAABB(body.pos, nextPos, AABB_TOLERANCE);
			} else {
				body.updateAABB(body.pos, body.pos, AABB_TOLERANCE);
			}
		}
		
		// Broadphase
		// TODO: Spatiale-Optimierung
		numPairs = 0;
		for (int i = 0; i < numBodies; i++) {
			Body bodyA = bodies[i];
			for (int j = i + 1; j < numBodies; j++) {
				Body bodyB = bodies[j];
				if (bodyA.invMass > 0 || bodyB.invMass > 0) {
					if (bodyA.aabb.overlaps(bodyB.aabb)) {
						for (int shapeIndeyA = 0 ; shapeIndeyA < bodyA.numShapes; shapeIndeyA++) {
							for (int shapeIndeyB = 0 ; shapeIndeyB < bodyB.numShapes; shapeIndeyB++) {
								Shape shapeA = bodyA.shapes[shapeIndeyA];
								Shape shapeB = bodyB.shapes[shapeIndeyB];
								BodyShapePair newPair = new BodyShapePair(bodyA, bodyB, shapeA, shapeB);
								pairs[numPairs++] = newPair;
							}
						}
					}
				}
			}
		}
		
		// Kontaktgenerierung
		currContactPairs.clear();
		numContacts = 0;
		for (int bodyPairIndex = 0; bodyPairIndex < numPairs; bodyPairIndex++) {
			BodyShapePair pair = pairs[bodyPairIndex];
			Shape shapeA = pair.shapeA;
			Shape shapeB = pair.shapeB;

			Transform transformA = new Transform(shapeA.localPos, shapeA.localRotation).offset(pair.a.pos);
			Transform transformB = new Transform(shapeB.localPos, shapeB.localRotation).offset(pair.b.pos);
			
			int newContacts = contactGenFactory.generate(transformA, transformB, shapeA, shapeB, 0, pair.contacts, contactSolver);
			pair.numContacts += newContacts;

			if (newContacts > 0) {
				pair.update();
				int bestContactIndex = -1;
				float bestContactDistance = 0f;
				ContactState bestState = ContactState.None;
				for (int contactIndex = 0; contactIndex < newContacts; contactIndex++) {
					Contact contact = pair.contacts[contactIndex];
					if (ContactState.None.equals(bestState) || contact.distance < bestContactDistance) {
						bestContactIndex = contactIndex;
						bestContactDistance = contact.distance;
						if (Scalar.equals(contact.distance, 0)) {
							bestState = ContactState.Touching;
						} else if (Scalar.greater(contact.distance, 0)) {
							bestState = ContactState.Separation;
						} else {
							bestState = ContactState.Penetration;
						}
					}
				}
				
				assert(!ContactState.None.equals(bestState));
				
				// Kontaktstatus-Paar erzeugen und in aktuelle Map einf�gen
				ContactStatePair cur = new ContactStatePair(pair, bestContactIndex, bestState);
				final int hash = cur.hashCode();
				currContactPairs.put(hash, cur);
				ContactStatePair last = prevContactPairs.get(hash);
				
				// Beginn und Endkontakt erkennen
				if (!ContactState.Separation.equals(cur.state) && (last == null || last.state.equals(ContactState.Separation))) {
					beginContact(hash, cur, last);
				} else {
					endContact(hash, last, cur);
				}
				
				numContacts += pair.numContacts;
			} else {
				// Endkontakt erkennen
				ContactStatePair cur = new ContactStatePair(pair, -1, ContactState.None);
				final int hash = cur.hashCode();
				ContactStatePair last = prevContactPairs.get(hash);
				endContact(hash, last, cur);
			}
		}
		
		// Geschwindigkeiten l�sen
		contactSolver.solveVelocity(dt, numPairs, pairs);

		// Geschwindigkeit integrieren
		for (int i = 0; i < numBodies; i++) {
			Body body = bodies[i];
			if (body.invMass > 0) {
				body.pos.addMultScalar(body.vel, dt);
			}
		}

		// Positionskorrektur
		contactSolver.solvePosition(dt, numPairs, pairs);

		// Beschleunigung zur�cksetzen
		for (int i = 0; i < numBodies; i++) {
			Body body = bodies[i];
			if (body.invMass > 0) {
				body.acc.zero();
			}
		}
		
		// Vorherigen Kontaktstatus merken
		prevContactPairs.clear();
		for (int hash: currContactPairs.keySet()) {
			prevContactPairs.put(hash, currContactPairs.get(hash));
		}
	}
}
