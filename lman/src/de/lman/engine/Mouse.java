package de.lman.engine;

public enum Mouse {
	LEFT(1);
	
	public final int btn;
	
	private Mouse(int btn) {
		this.btn = btn;
	}

}
