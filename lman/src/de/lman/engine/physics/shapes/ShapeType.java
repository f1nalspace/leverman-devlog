package de.lman.engine.physics.shapes;

public enum ShapeType {
	Plane(1),
	LineSegment(2),
	Box(3),
	Polygon(4),
	Circle(5),
	COUNT(6);
	
	public final int id;
	
	private ShapeType(int id) {
		this.id = id;
	}
}
