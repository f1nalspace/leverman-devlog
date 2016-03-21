package de.lman.engine.physics.generators;

import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.ClosestPoints;
import de.lman.engine.physics.SAT;
import de.lman.engine.physics.SATResult;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactAcceptor;
import de.lman.engine.physics.contacts.ContactGenerator;
import de.lman.engine.physics.contacts.ContactType;
import de.lman.engine.physics.contacts.ManifoldInput;
import de.lman.engine.physics.contacts.ManifoldOutput;
import de.lman.engine.physics.shapes.EdgeShape;
import de.lman.engine.physics.shapes.Shape;

public class EdgeEdgeContactGenerator implements ContactGenerator {

	@Override
	public int generate(Transform transformA, Transform transformB, Shape shapeA, Shape shapeB, int offset, Contact[] contacts, ContactAcceptor acceptor) {
		EdgeShape edgeA = (EdgeShape) shapeA;
		EdgeShape edgeB = (EdgeShape) shapeB;

		// Eckpunkte von A und B bestimmen
		int numVertsA = edgeA.getVertexCount();
		int numVertsB = edgeB.getVertexCount();
		Vec2f[] localVertsA = edgeA.getLocalVertices();
		Vec2f[] localVertsB = edgeB.getLocalVertices();

		// Eckpunkte für A und B transformieren
		Vec2f[] vertsA = new Vec2f[localVertsA.length];
		Vec2f[] vertsB = new Vec2f[localVertsB.length];
		for (int i = 0; i < localVertsA.length; i++) {
			vertsA[i] = new Vec2f(localVertsA[i]).transform(transformA);
		}
		for (int i = 0; i < localVertsB.length; i++) {
			vertsB[i] = new Vec2f(localVertsB[i]).transform(transformB);
		}
		
		// SAT-Query für A und B
		SATResult resultA = SAT.query(vertsA, vertsB, numVertsA, numVertsB);
		SATResult resultB = SAT.query(vertsB, vertsA, numVertsB, numVertsA);
		
		// Eingabe-Zusammenfassung bauen
		ManifoldInput input;
		if (resultA.distance > resultB.distance) {
			input = new ManifoldInput(true, transformA, transformB, resultA.normal, vertsA, vertsB, numVertsA, numVertsB);
		} else {
			input = new ManifoldInput(false, transformB, transformA, resultB.normal, vertsB, vertsA, numVertsB, numVertsA);
		}
		
		// Ausgabe initialisieren
		ManifoldOutput output = new ManifoldOutput();

		// Supportpunkte bestimmen
		Vec2f[] supportPointsA = new Vec2f[2];
		Vec2f[] supportPointsB = new Vec2f[2];
		int numSupportPointsA = SAT.getSupportPoints(input.normal, input.vertsA, input.numVertsA, supportPointsA, 0);
		int numSupportPointsB = SAT.getSupportPoints(new Vec2f(input.normal).multScalar(-1f), input.vertsB, input.numVertsB, supportPointsB, 0);
		assert(numSupportPointsA == 2);
		assert(numSupportPointsB == 2);
		
		// Nächstliegende Punkte bestimmen
		Vec2f closestA = new Vec2f();
		Vec2f closestB = new Vec2f();
		float regionA = ClosestPoints.onLineSegment(supportPointsB[0], supportPointsA[0], supportPointsA[1], closestA);
		float regionB = ClosestPoints.onLineSegment(closestA, supportPointsB[0], supportPointsB[1], closestB);
		
		// Nächstliegende Distanz bestimmen
		Vec2f closestDistance = new Vec2f(closestB).sub(closestA);
		output.normal.set(input.normal);
		
		// Wenn notwendig Richtung auf Basis der nächstliegenden Distanz bestimmen
		ContactType type = ContactType.FaceFace;
		if ((regionA < 0 || regionA > 1) && (regionB < 0 || regionB > 1)) {
			output.normal.set(closestDistance).normalize();
			// TODO: Ist das korrekt
			if ((regionA >= 0 && regionA <= 1) || (regionA >= 0 && regionA <= 1)) {
				type = ContactType.FaceVertex;
			}
		} else {
			if ((regionA < Contact.VERTEX_EPSILON || regionA > 1f - Contact.VERTEX_EPSILON) && (regionB < Contact.VERTEX_EPSILON || regionB > 1 - Contact.VERTEX_EPSILON)) {
				type = ContactType.VertexVertex;
			}
		}
		output.distance = closestDistance.dot(output.normal);
		output.numPoints = 1;
		output.points[0] = closestA;
				
		// Kontakte erzeugen
		int result = 0;
		for (int i = 0; i < output.numPoints; i++) {
			Contact newContact = new Contact(output.normal, output.distance, output.points[i].sub(transformA.p), type);
			if (!input.wasFaceA) {
				newContact.pointA.addMultScalar(newContact.normal, newContact.distance);
				newContact.normal.invert();
			}
			if (acceptor.accept(newContact)) {
				contacts[offset + result++] = newContact;
			}
		}
		return (result);
	}

}
