package de.lman.samples;

import de.lman.engine.Colors;
import de.lman.engine.Game;
import de.lman.engine.InputState;
import de.lman.engine.Keys;
import de.lman.engine.Mouse;
import de.lman.engine.math.Scalar;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.GeometryUtils;
import de.lman.engine.physics.Physics;
import de.lman.engine.physics.shapes.CircleShape;
import de.lman.engine.physics.shapes.LineSegmentShape;
import de.lman.engine.physics.shapes.PhysicsMaterial;
import de.lman.engine.physics.shapes.PlaneShape;
import de.lman.engine.physics.shapes.Shape;

public class PlaygroundSampleGame extends Game {

	public PlaygroundSampleGame() {
		super("Playground");
	}

	public static void main(String[] args) {
		PlaygroundSampleGame game = new PlaygroundSampleGame();
		game.run();
	}

	private Physics physics;
	private boolean showContacts = false;
	private boolean showAABBs = false;

	private final PhysicsMaterial MAT_STATIC = new PhysicsMaterial(0f, 0.1f);
	private final PhysicsMaterial MAT_DYNAMIC = new PhysicsMaterial(1f, 0.1f);

	protected void initGame() {
		physics = new Physics(null);

		Body body;
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.y).rotation(Scalar.PI * 0f).setMaterial(MAT_STATIC)));
		body.pos.set(-halfWidth + 0.5f, 0);
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.y).rotation(Scalar.PI * 1f).setMaterial(MAT_STATIC)));
		body.pos.set(halfWidth - 0.5f, 0);
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.x).rotation(Scalar.PI * 0.5f).setMaterial(MAT_STATIC)));
		body.pos.set(0, -halfHeight + 0.5f);
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.x).rotation(Scalar.PI * 1.5f).setMaterial(MAT_STATIC)));
		body.pos.set(0, halfHeight - 0.5f);

		physics.addBody(body = new Body().addShape(new LineSegmentShape(3f).rotation(Scalar.PI * 0.05f).setMaterial(MAT_STATIC)));
		body.pos.set(0 - 0.5f, 0 + 1.5f);
		physics.addBody(body = new Body().addShape(new LineSegmentShape(3f).rotation(Scalar.PI * -0.05f).setMaterial(MAT_STATIC)));
		body.pos.set(0 + 0.5f, 0);
		physics.addBody(body = new Body().addShape(new LineSegmentShape(1.5f).rotation(Scalar.PI * 0.05f).setMaterial(MAT_STATIC)));
		body.pos.set(0 - 2.5f, 0 - 1.5f);
	}

	private boolean dragging = false;
	private Vec2f dragStart = new Vec2f();
	private Body dragBody = null;
	private boolean placeBody = false;

	protected void updateInput(float dt, InputState inputState) {
		boolean leftMousePressed = inputState.isMouseDown(Mouse.LEFT);
		if (!dragging) {
			if (leftMousePressed) {
				dragBody = null;
				for (int i = 0; i < physics.numBodies; i++) {
					Body body = physics.bodies[i];
					if (body.invMass > 0) {
						if (GeometryUtils.isPointInAABB(inputState.mousePos.x, inputState.mousePos.y, body.aabb)) {
							dragging = true;
							dragStart.set(inputState.mousePos);
							dragBody = body;
							break;
						}
					}
				}
				if (dragBody == null) {
					placeBody = true;
				}
			} else {
				if (placeBody) {
					placeBody = false;

					final float radius = 0.1f;

					Body body;
					Shape circleShape = new CircleShape(radius).setMaterial(MAT_DYNAMIC);
					physics.addBody(body = new Body().addShape(circleShape));
					body.pos.set(inputState.mousePos.x, inputState.mousePos.y);

					/*
					final int numX = 1;
					final int numY = 1;
					final float halfDimX = (radius + radius * 0.1f) * numX;
					final float halfDimY = (radius + radius * 0.1f) * numY;

					for (int y = 0; y < numY; y++) {
						for (int x = 0; x < numX; x++) {
							Shape circleShape = new CircleShape(radius).setMaterial(dynamicMaterial).offset(-radius, 0f);
							Shape boxShape = new BoxShape(new Vec2f(radius, radius)).setMaterial(dynamicMaterial).offset(radius, 0f);
							physics.addBody(body = new Body().addShape(circleShape).addShape(boxShape));
							body.pos.set(mousePos.x - halfDimX + x * (radius + radius * 0.1f) * 2f, mousePos.y - halfDimY + y * radius * 2f);
						}
					}
					*/
				}
			}
		} else {
			if (leftMousePressed) {
				float dx = inputState.mousePos.x - dragStart.x;
				float dy = inputState.mousePos.y - dragStart.y;
				dragBody.vel.x += dx * 0.1f;
				dragBody.vel.y += dy * 0.1f;
				dragStart.set(inputState.mousePos);
			} else {
				dragging = false;
			}
		}

		// Kontakte ein/ausschalten
		if (inputState.isKeyDown(Keys.F2)) {
			showContacts = !showContacts;
			inputState.setKeyDown(Keys.F2, false);
		}

		// AABBs ein/ausschalten
		if (inputState.isKeyDown(Keys.F3)) {
			showAABBs = !showAABBs;
			inputState.setKeyDown(Keys.F3, false);
		}

		// Dynamische körper per WSAD bewegen
		for (int i = 0; i < physics.numBodies; i++) {
			Body body = physics.bodies[i];

			if (inputState.isKeyDown(Keys.W)) {
				// W gedrückt
				body.acc.y += 0.1f / dt;
			} else if (inputState.isKeyDown(Keys.S)) {
				// S gedrückt
				body.acc.y -= 0.1f / dt;
			}

			if (inputState.isKeyDown(Keys.A)) {
				// A gedrückt
				body.acc.x -= 0.1f / dt;
			} else if (inputState.isKeyDown(Keys.D)) {
				// D gedrückt
				body.acc.x += 0.1f / dt;
			}
		}
	}

	@Override
	protected String getAdditionalTitle() {
		return String.format(" [Frames: %d, Bodies: %d, Contacts: %d]", numFrames, physics.numBodies, physics.numContacts);
	}

	protected void updateGame(float dt) {
		physics.step(dt);
	}

	protected void renderGame(float dt) {
		clear(0x000000);

		drawBodies(physics.numBodies, physics.bodies);

		if (showAABBs) {
			drawAABBs(physics.numBodies, physics.bodies);
		}

		if (showContacts) {
			drawContacts(dt, physics.numPairs, physics.pairs, false, true);
		}

		drawPoint(inputState.mousePos.x, inputState.mousePos.y, DEFAULT_POINT_RADIUS, Colors.Red);
	}

}
