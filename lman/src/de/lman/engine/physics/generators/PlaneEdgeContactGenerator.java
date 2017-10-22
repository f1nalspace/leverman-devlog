package de.lman.engine.physics.generators;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.SAT;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactAcceptor;
import de.lman.engine.physics.contacts.ContactGenerator;
import de.lman.engine.physics.contacts.ContactType;
import de.lman.engine.physics.shapes.EdgeShape;
import de.lman.engine.physics.shapes.Shape;

public class PlaneEdgeContactGenerator implements ContactGenerator {

	@Override
	public int generate(Transform transformA, Transform transformB, Shape shapeA, Shape shapeB, int offset, Contact[] contacts, ContactAcceptor acceptor) {
		EdgeShape boxB = (EdgeShape) shapeB;
		Vec2f normal = transformA.q.col1;
		
		// Eckpunkte bestimmen
		Vec2f[] localVertices = boxB.getLocalVertices();
		int numVerts = boxB.getVertexCount();
		Vec2f[] vertices = new Vec2f[numVerts];
		for (int i = 0; i < numVerts; i++) {
			vertices[i] = new Vec2f(localVertices[i]).transform(transformB);
		}
		
		// Supportpunkt bestimmen
		Vec2f[] supportPoints = new Vec2f[2];
		SAT.getSupportPoints(new Vec2f(normal).invert(), vertices, numVerts, supportPoints, 0);
		
		// Distanz zur Ebene bestimmen von Eckpunkt
		Vec2f distanceToPlane = new Vec2f(supportPoints[0]).sub(transformA.p);
		float d = distanceToPlane.dot(normal);

		// Punkt auf Ebene bestimmen
		Vec2f pointOnA = new Vec2f(supportPoints[0]).addMultScalar(normal, -d);
	
		// Kontakt erstellen
		// TODO: Face vs Vertex erkennen
		Contact newContact = new Contact(normal, d, pointOnA.sub(transformA.p), ContactType.FaceFace);
		int result = 0;
		if (acceptor.accept(newContact)) {
			contacts[offset + 0] = newContact;
			result = 1;
		}
		return(result);
	}

}
