package de.lman.engine.physics;

import de.lman.engine.physics.contacts.ContactState;

public class ContactStatePair {
	public final BodyShapePair pair;
	public final int contactIndex;
	public final ContactState state;

	public ContactStatePair(BodyShapePair pair, int contactIndex, ContactState state) {
		this.pair = pair;
		this.contactIndex = contactIndex;
		this.state = state;
	}

	@Override
	public int hashCode() {
		int hash = pair.hashCode();
		return (hash);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ContactStatePair) {
			return ((ContactStatePair) obj).hashCode() == hashCode();
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "" + state;
	}
}
