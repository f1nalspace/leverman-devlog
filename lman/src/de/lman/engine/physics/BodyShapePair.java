package de.lman.engine.physics;

import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.shapes.PhysicsMaterial;
import de.lman.engine.physics.shapes.Shape;

public class BodyShapePair {
	public static final int MAX_CONTACTS_PER_PAIR = 2;
	
	public final Body a;
	public final Body b;
	public final Shape shapeA;
	public final Shape shapeB;
	public final Contact[] contacts;
	public int numContacts;
	
	public float friction;

	public BodyShapePair(Body a, Body b, Shape shapeA, Shape shapeB) {
		this.a = a;
		this.b = b;
		this.shapeA = shapeA;
		this.shapeB = shapeB;
		this.contacts = new Contact[MAX_CONTACTS_PER_PAIR];
		this.numContacts = 0;
		this.friction = 0f;
	}
	
	public void update() {
		PhysicsMaterial matA = shapeA.material;
		PhysicsMaterial matB = shapeB.material;
		if (matA != null && matB != null) {
			// Reibung mischen aus beiden Materialen
			friction = (float) Math.sqrt(matA.friction * matB.friction);
		} else {
			// Keine Reibung
			friction = 0f;
		}
	}
	
	@Override
	public int hashCode() {
		int hash = a.id * 3 + b.id * 5 + shapeA.id * 7 + shapeB.id * 11;
		return (hash);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof BodyShapePair) {
			return ((BodyShapePair)obj).hashCode() == hashCode();
		}
		return false;
	}
}
