package de.lman.engine.physics.generators;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactAcceptor;
import de.lman.engine.physics.contacts.ContactGenerator;
import de.lman.engine.physics.contacts.ContactType;
import de.lman.engine.physics.shapes.CircleShape;
import de.lman.engine.physics.shapes.Shape;

public class PlaneCircleContactGenerator implements ContactGenerator {

	@Override
	public int generate(Transform transformA, Transform transformB, Shape shapeA, Shape shapeB, int offset, Contact[] contacts, ContactAcceptor acceptor) {
		CircleShape circleB = (CircleShape) shapeB;
		Vec2f normal = transformA.q.col1;

		Vec2f posA = new Vec2f(transformA.p);
		Vec2f posB = new Vec2f(transformB.p);
		
		Vec2f distanceToPlane = new Vec2f(posA).sub(posB);
		float projDistance = distanceToPlane.dot(normal);
		float projRadius = -circleB.radius;
		float d = projRadius - projDistance;
		Vec2f pointOnA = new Vec2f(posB).addMultScalar(normal, projDistance);
		Contact newContact = new Contact(normal, d, pointOnA.sub(posA), ContactType.FaceFace);
		int result = 0;
		if (acceptor.accept(newContact)) {
			contacts[offset + 0] = newContact;
			result = 1;
		}
		return(result);
	}

}
