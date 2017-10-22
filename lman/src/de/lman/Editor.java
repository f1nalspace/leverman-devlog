package de.lman;

import de.lman.engine.InputState;
import de.lman.engine.Keys;
import de.lman.engine.Mouse;
import de.lman.engine.math.Scalar;
import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.GeometryUtils;
import de.lman.engine.physics.Physics;
import de.lman.engine.physics.shapes.BoxShape;
import de.lman.engine.physics.shapes.EdgeShape;
import de.lman.engine.physics.shapes.Shape;

public class Editor {
	public boolean active = false;
	public boolean dragging = false;
	public final Vec2f dragStart = new Vec2f();
	public Body selectedBody = null;
	public final static float GRID_SIZE = 0.25f;
	public final static float DRAGPOINT_RADIUS = 0.03f;
	public final Vec2f resizeDir = new Vec2f();
	public int resizeSideIndex = -1; 

	public enum SelectionState {
		None,
		Position,
		Resize
	}

	public SelectionState selState = SelectionState.None;

	public Editor() {
	}
	
	public class DragSide {
		public final Vec2f v0;
		public final Vec2f v1;
		public final Vec2f center;
		public final int index0;
		public final int index1;

		public DragSide(Vec2f v0, Vec2f v1, int index0, int index1) {
			this.v0 = v0;
			this.v1 = v1;
			this.index0 = index0;
			this.index1 = index1;
			center = new Vec2f(v0).addMultScalar(new Vec2f(v1).sub(v0), 0.5f);
		}
	}

	public DragSide[] getDragSides(Body body) {
		DragSide[] dragSides = new DragSide[0];
		if (body.numShapes > 0) {
			Shape shape = body.shapes[0];
			Transform t = new Transform(shape.localPos, shape.localRotation).offset(body.pos);
			if (shape instanceof EdgeShape) {
				EdgeShape edgeShape = (EdgeShape) shape;
				Vec2f[] localVertices = edgeShape.getLocalVertices();
				int vertexCount = edgeShape.getVertexCount();
				dragSides = new DragSide[vertexCount];
				for (int i = 0; i < vertexCount; i++) {
					int index0 = i;
					int index1 = (i + 1) % vertexCount;
					Vec2f v0 = new Vec2f(localVertices[index0]).transform(t);
					Vec2f v1 = new Vec2f(localVertices[index1]).transform(t);
					dragSides[i] = new DragSide(v0, v1, index0, index1);
				}
			}
		}
		return (dragSides);
	}

	private void newSelection(Body newBody) {
		selectedBody = newBody;
		selState = SelectionState.Position;
		selectedBody.updateAABB(selectedBody.pos, selectedBody.pos, 0f);
	}

	private void releaseSelection() {
		selectedBody = null;
		selState = SelectionState.None;
	}

	public void updateInput(float dt, Physics physics, InputState inputState) {
		boolean leftMousePressed = inputState.isMouseDown(Mouse.LEFT);
		if (!dragging) {
			if (leftMousePressed) {
				Body newBody = null;
				for (int i = 0; i < physics.numBodies; i++) {
					Body body = physics.bodies[i];
					if (GeometryUtils.isPointInAABB(inputState.mousePos.x, inputState.mousePos.y, body.aabb)) {
						newBody = body;
					}
				}

				if (selectedBody == null) {
					// War kein Body ausgew�hlt
					selState = SelectionState.None;

					// Haben wir einen neuen Body angeklickt?
					if (newBody != null) {
						newSelection(newBody);
					}
				} else {
					// Ziehpunkt finden
					DragSide newDragSide = null;
					int newDragSideIndex = -1;
					DragSide[] dragSides = getDragSides(selectedBody);
					for (int i = 0; i < dragSides.length; i++) {
						Vec2f dragPoint = dragSides[i].center;
						Vec2f distance = new Vec2f(inputState.mousePos).sub(dragPoint);
						if (Math.abs(distance.x) <= DRAGPOINT_RADIUS && Math.abs(distance.y) <= DRAGPOINT_RADIUS) {
							newDragSide = dragSides[i];
							newDragSideIndex = i;
							break;
						}
					}
					if (newDragSide != null) {
						selState = SelectionState.Resize;
						resizeDir.set(newDragSide.v1).sub(newDragSide.v0).perpRight().normalize();
						resizeSideIndex = newDragSideIndex;
					} else {
						if (newBody != null) {
							// Neue selektion
							newSelection(newBody);
						} else {
							// Selektion zur�cksetzen
							releaseSelection();
						}
					}
				}

				if (!selState.equals(SelectionState.None)) {
					dragging = true;
					dragStart.set(inputState.mousePos);
				}
			}
		} else {
			if (leftMousePressed) {
				float dx = inputState.mousePos.x - dragStart.x;
				float dy = inputState.mousePos.y - dragStart.y;
				switch (selState) {
					case Position:
						selectedBody.pos.x += dx;
						selectedBody.pos.y += dy;
						selectedBody.updateAABB(selectedBody.pos, selectedBody.pos, 0f);
						break;
					case Resize:
						resizeBody(selectedBody, dx, dy);
						break;
					default:
						assert(false);
				}
				dragStart.set(inputState.mousePos);
			} else {
				dragging = false;
			}
		}

		if (inputState.isKeyDown(Keys.C)) {
			Body body = new Body().addShape(new BoxShape(new Vec2f(0.2f, 0.2f)).setMaterial(Leverman.MAT_STATIC));
			body.pos.set(inputState.mousePos);
			body.updateAABB(body.pos, body.pos, 0);
			physics.addBody(body);
			newSelection(body);
			inputState.setKeyDown(Keys.C, false);
		}
		
		
		if (selectedBody != null && selState.equals(SelectionState.Position)) {
			final float ROT_AMOUNT = 0.01f;
			if (inputState.isKeyDown(Keys.Q)) {
				rotateBody(selectedBody, -Scalar.PI * ROT_AMOUNT);
			} else if (inputState.isKeyDown(Keys.E)) {
				rotateBody(selectedBody, Scalar.PI * ROT_AMOUNT);
			}
		}
	}

	private void rotateBody(Body body, float angle) {
		Shape shape = body.shapes[0];
		shape.localRotation += angle;
		body.updateAABB(body.pos, body.pos, 0);
	}

	private void resizeBody(Body body, float dx, float dy) {
		Shape shape = body.shapes[0];
		if (shape instanceof EdgeShape && resizeSideIndex > -1) {
			EdgeShape edgeShape = (EdgeShape) shape;
			Vec2f[] localVertices = edgeShape.getLocalVertices();
			int numVertices = edgeShape.getVertexCount();
			DragSide[] dragSides = getDragSides(body);
			DragSide thisSide = dragSides[resizeSideIndex];
			
			// Delta ermitteln
			Vec2f delta = new Vec2f(dx, dy);
			float distance = delta.dot(resizeDir);

			// Ziehpunkte und Lokale Richtung ermitteln
			Vec2f v0 = localVertices[thisSide.index0];
			Vec2f v1 = localVertices[thisSide.index1];
			Vec2f localNormal = new Vec2f(v1).sub(v0).perpRight().normalize();

			// Lokales Delta berechnen
			Vec2f localDelta = new Vec2f(localNormal).multScalar(distance);
		
			// Lokale Ziehpunkte verschieben
			v0.add(localDelta);
			v1.add(localDelta);
			
			// Alle Lokalen Eckpunkte entgegen des halben Delta verschieben
			for (int i = 0; i < numVertices; i++) {
				localVertices[i].addMultScalar(localDelta, -0.5f);
			}
			
			// Körper position um halbes Delta verschieben
			Vec2f bodyDelta = new Vec2f(resizeDir).multScalar(distance);
			body.pos.addMultScalar(bodyDelta, 0.5f);
			
			body.updateAABB(body.pos, body.pos, 0);
		}
	}

	public void init(Physics physics) {
		for (int i = 0; i < physics.numBodies; i++) {
			Body body = physics.bodies[i];
			body.vel.zero();
			body.acc.zero();
			body.updateAABB(body.pos, body.pos, 0);
		}
	}

}
