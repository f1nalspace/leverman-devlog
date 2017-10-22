package de.lman.engine.physics.contacts;

import de.lman.engine.physics.ContactStatePair;

public interface ContactListener {
	void physicsBeginContact(int hash, ContactStatePair pair);
	void physicsEndContact(int hash, ContactStatePair pair);
}
