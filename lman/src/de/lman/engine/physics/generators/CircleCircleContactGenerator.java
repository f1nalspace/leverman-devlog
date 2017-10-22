package de.lman.engine.physics.generators;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactAcceptor;
import de.lman.engine.physics.contacts.ContactGenerator;
import de.lman.engine.physics.contacts.ContactType;
import de.lman.engine.physics.shapes.CircleShape;
import de.lman.engine.physics.shapes.Shape;

public class CircleCircleContactGenerator implements ContactGenerator {

	@Override
	public int generate(Transform transformA, Transform transformB, Shape shapeA, Shape shapeB, int offset, Contact[] contacts, ContactAcceptor acceptor) {
		CircleShape circleA = (CircleShape) shapeA;
		CircleShape circleB = (CircleShape) shapeB;

		Vec2f posA = new Vec2f(transformA.p);
		Vec2f posB = new Vec2f(transformB.p);
		
		Vec2f distanceBetween = new Vec2f(posB).sub(posA);
		Vec2f normal = new Vec2f();
		if (distanceBetween.lengthSquared() > 0) {
			normal.set(distanceBetween).normalize();
		} else {
			normal.set(1, 0);
		}
		float projDistance = distanceBetween.dot(normal);
		float bothRadius = circleA.radius + circleB.radius;
		float d = -(bothRadius - projDistance);
		Vec2f pointOnA = new Vec2f(posA).addMultScalar(normal, circleA.radius);
		Contact newContact = new Contact(normal, d, pointOnA.sub(posA), ContactType.FaceFace);
		int result = 0;
		if (acceptor.accept(newContact)) {
			contacts[offset + 0] = newContact;
			result = 1;
		}
		return(result);
	}

}
