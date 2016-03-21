package de.lman.engine.physics.generators;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.ClosestPoints;
import de.lman.engine.physics.SAT;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactAcceptor;
import de.lman.engine.physics.contacts.ContactGenerator;
import de.lman.engine.physics.contacts.ContactType;
import de.lman.engine.physics.shapes.CircleShape;
import de.lman.engine.physics.shapes.EdgeShape;
import de.lman.engine.physics.shapes.Shape;

public class EdgeCircleContactGenerator implements ContactGenerator {

	@Override
	public int generate(Transform transformA, Transform transformB, Shape shapeA, Shape shapeB, int offset, Contact[] contacts, ContactAcceptor acceptor) {
		EdgeShape edge = (EdgeShape) shapeA;
		CircleShape circle = (CircleShape) shapeB;
		
		Vec2f posA = transformA.p;
		Vec2f posB = transformB.p;
		
		// Eckpunkte bestimmen
		Vec2f[] localVertices = edge.getLocalVertices();
		int numVerts = edge.getVertexCount();
		Vec2f[] vertices = new Vec2f[numVerts];
		for (int i = 0; i < numVerts; i++) {
			vertices[i] = new Vec2f(localVertices[i]).transform(transformA);
		}
		
		// Kürzeste Distanz ermitteln
		Vec2f normal = null;
		float distance = 0;
		for (int i = 0; i < numVerts; i++) {
			Vec2f v0 = vertices[i];
			Vec2f v1 = vertices[(i + 1) % numVerts];
			Vec2f n = new Vec2f(v1).sub(v0).perpRight().normalize();
			Vec2f distanceToCircle = new Vec2f(posB).sub(v0);
			float d = distanceToCircle.dot(n) - circle.radius;
			if (normal == null || d > distance) {
				distance = d;
				normal = n;
			}
		}
		
		// Supportpunkt bestimmen
		Vec2f[] supportPoints = new Vec2f[2];
		int numSupportPoints = SAT.getSupportPoints(normal, vertices, numVerts, supportPoints, 0);
		assert(numSupportPoints == 2);
		
		// Nächstliegenden Punkt bestimmen
		Vec2f pointOnA = new Vec2f();
		float regionA = ClosestPoints.onLineSegment(posB, supportPoints[0], supportPoints[1], pointOnA);
				
		// Wenn notwendig Richtung auf Basis der nächstliegenden Distanz bestimmen
		ContactType type = ContactType.VertexVertex;
		if (regionA < 0 || regionA > 1) {
			Vec2f closestDistance = new Vec2f(posB).sub(pointOnA);
			normal.set(closestDistance).normalize();
			distance = closestDistance.dot(normal) - circle.radius;
			type = ContactType.FaceVertex;
		}

		// Kontakt erzeugen
		Contact newContact = new Contact(normal, distance, pointOnA.sub(posA), type);
		int result = 0;
		if (acceptor.accept(newContact)) {
			contacts[offset + 0] = newContact;
			result = 1;
		}
		return(result);
	}

}
