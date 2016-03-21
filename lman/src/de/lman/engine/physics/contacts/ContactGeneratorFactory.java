package de.lman.engine.physics.contacts;

import de.lman.engine.math.Transform;
import de.lman.engine.physics.generators.CircleCircleContactGenerator;
import de.lman.engine.physics.generators.EdgeCircleContactGenerator;
import de.lman.engine.physics.generators.EdgeEdgeContactGenerator;
import de.lman.engine.physics.generators.PlaneCircleContactGenerator;
import de.lman.engine.physics.generators.PlaneEdgeContactGenerator;
import de.lman.engine.physics.shapes.Shape;
import de.lman.engine.physics.shapes.ShapeType;

public final class ContactGeneratorFactory {
	private final int MAX_GENERATORS_PER_SHAPE = ShapeType.COUNT.id;
	private final ContactGenerator[][] generators = new ContactGenerator[MAX_GENERATORS_PER_SHAPE][MAX_GENERATORS_PER_SHAPE];
	
	public ContactGeneratorFactory() {
		generators[ShapeType.Plane.id][ShapeType.Circle.id] = new PlaneCircleContactGenerator();
		generators[ShapeType.Plane.id][ShapeType.Box.id] = new PlaneEdgeContactGenerator();
		generators[ShapeType.Plane.id][ShapeType.Polygon.id] = new PlaneEdgeContactGenerator();
		generators[ShapeType.LineSegment.id][ShapeType.Circle.id] = new EdgeCircleContactGenerator();
		generators[ShapeType.LineSegment.id][ShapeType.Box.id] = new EdgeEdgeContactGenerator();
		generators[ShapeType.LineSegment.id][ShapeType.Polygon.id] = new EdgeEdgeContactGenerator();
		generators[ShapeType.Box.id][ShapeType.Circle.id] = new EdgeCircleContactGenerator();
		generators[ShapeType.Box.id][ShapeType.Box.id] = new EdgeEdgeContactGenerator();
		generators[ShapeType.Box.id][ShapeType.Polygon.id] = new EdgeEdgeContactGenerator();
		generators[ShapeType.Polygon.id][ShapeType.Circle.id] = new EdgeCircleContactGenerator();
		generators[ShapeType.Polygon.id][ShapeType.Polygon.id] = new EdgeEdgeContactGenerator();
		generators[ShapeType.Circle.id][ShapeType.Circle.id] = new CircleCircleContactGenerator();
	}
	
	public int generate(Transform transformA, Transform transformB, Shape shapeA, Shape shapeB, int offset, Contact[] contacts, ContactAcceptor acceptor) {
		int result = 0;
		
		// Shape types sortieren
		boolean flip = false;
		ShapeType typeA = shapeA.type;
		ShapeType typeB = shapeB.type;
		if (typeA.id > typeB.id) {
			ShapeType temp = typeA;
			typeA = typeB;
			typeB = temp;
			flip = true;
		}
		
		// Kontaktgenerator finden
		ContactGenerator generator = generators[typeA.id][typeB.id];
		if (generator != null) {
			// Kontakte erzeugen und anzahl an erzeugte Kontakte zur�ckliefern
			if (flip) {
				result = generator.generate(transformB, transformA, shapeB, shapeA, offset, contacts, acceptor);
				for (int i = 0; i < result; i++) {
					Contact contact = contacts[offset + i];
					// TODO: Rotation mit berücksichtigen (Inverse Matrix)
					contact.pointA.add(transformB.p).sub(transformA.p);
					contact.pointA.addMultScalar(contact.normal, contact.distance);
					contact.normal.multScalar(-1f);
				}
			} else {
				result = generator.generate(transformA, transformB, shapeA, shapeB, offset, contacts, acceptor);
			}
		}
		return(result);
	}
}
