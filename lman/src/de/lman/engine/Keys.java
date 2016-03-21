package de.lman.engine;

public enum Keys {
	SPACE(32),
	A(65),
	C(67),
	D(68),
	E(69),
	Q(81),
	W(87),
	S(83),
	F2(113),
	F3(114),
	F4(115),
	F5(116),
	F6(117);;

	public final int key;

	private Keys(int key) {
		this.key = key;
	}
}
