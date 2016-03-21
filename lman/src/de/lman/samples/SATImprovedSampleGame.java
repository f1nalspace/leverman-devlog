package de.lman.samples;

import de.lman.engine.Colors;
import de.lman.engine.Game;
import de.lman.engine.InputState;
import de.lman.engine.Keys;
import de.lman.engine.Mouse;
import de.lman.engine.math.Scalar;
import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.ClosestPoints;
import de.lman.engine.physics.GeometryUtils;
import de.lman.engine.physics.Physics;
import de.lman.engine.physics.SAT;
import de.lman.engine.physics.SATResult;
import de.lman.engine.physics.contacts.ManifoldInput;
import de.lman.engine.physics.contacts.ManifoldOutput;
import de.lman.engine.physics.shapes.BoxShape;
import de.lman.engine.physics.shapes.EdgeShape;
import de.lman.engine.physics.shapes.PolygonShape;
import de.lman.engine.physics.shapes.Shape;
import de.lman.engine.physics.shapes.ShapeType;

public class SATImprovedSampleGame extends Game {

	public SATImprovedSampleGame() {
		super("SAT Improved Sample");
	}

	public static void main(String[] args) {
		SATImprovedSampleGame samples = new SATImprovedSampleGame();
		samples.run();
	}

	private final int MAX_BODIES = 2;
	public final Body[] bodies = new Body[MAX_BODIES];
	public int numBodies = 0;

	private boolean showAABBs = false;

	protected void initGame() {
		numBodies = 2;

		Vec2f[] polyVerts = new Vec2f[]{
			new Vec2f(0, 0.5f),
			new Vec2f(-0.5f, -0.5f),
			new Vec2f(0.5f, -0.5f),
		};

		bodies[0] = new Body().addShape(new BoxShape(new Vec2f(1f, 0.6f)).rotation(Scalar.PI * 0f));
		bodies[0].pos.set(0, 0 - 1f);
		bodies[1] = new Body().addShape(new PolygonShape(polyVerts));
		bodies[1].pos.set(-halfWidth + 1f, 0);
	}

	private boolean dragging = false;
	private Vec2f dragStart = new Vec2f();
	private Body dragBody = null;

	protected void updateInput(float dt, InputState inputState) {
		boolean leftMousePressed = inputState.isMouseDown(Mouse.LEFT);
		if (!dragging) {
			if (leftMousePressed) {
				dragBody = null;
				for (int i = 0; i < numBodies; i++) {
					Body body = bodies[i];
					Shape shape = body.shapes[0];
					if (ShapeType.Plane.equals(shape.type)) {
						continue;
					}
					if (GeometryUtils.isPointInAABB(inputState.mousePos.x, inputState.mousePos.y, body.aabb)) {
						dragging = true;
						dragStart.set(inputState.mousePos);
						dragBody = body;
						break;
					}
				}
			}
		} else {
			if (leftMousePressed) {
				float dx = inputState.mousePos.x - dragStart.x;
				float dy = inputState.mousePos.y - dragStart.y;
				dragBody.pos.x += dx;
				dragBody.pos.y += dy;
				dragStart.set(inputState.mousePos);
			} else {
				dragging = false;
			}
		}

		// AABBs ein/ausschalten
		if (inputState.isKeyDown(Keys.F3)) {
			showAABBs = !showAABBs;
			inputState.setKeyDown(Keys.F3, false);
		}
	}

	@Override
	protected String getAdditionalTitle() {
		return "";
	}

	protected void updateGame(float dt) {
		Body bodyA = bodies[0];
		Body bodyB = bodies[1];
		bodyA.updateAABB(bodyA.pos, bodyA.pos, Physics.AABB_TOLERANCE);
		bodyB.updateAABB(bodyB.pos, bodyB.pos, Physics.AABB_TOLERANCE);
	}
	
	protected void renderGame(float dt) {
		clear(0x000000);

		drawBodies(numBodies, bodies);

		if (showAABBs) {
			drawAABBs(numBodies, bodies);
		}

		Body bodyA = bodies[0];
		Body bodyB = bodies[1];
		Shape shapeA = bodyA.shapes[0];
		Shape shapeB = bodyB.shapes[0];

		Transform transformA = new Transform(shapeA.localPos, shapeA.localRotation).offset(bodyA.pos);
		Transform transformB = new Transform(shapeB.localPos, shapeB.localRotation).offset(bodyB.pos);
		
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
		for (int i = 0; i < numSupportPointsA; i++) {
			drawPoint(supportPointsA[i], DEFAULT_POINT_RADIUS, Colors.Red);
		}
		for (int i = 0; i < numSupportPointsB; i++) {
			drawPoint(supportPointsB[i], DEFAULT_POINT_RADIUS, Colors.Green);
		}
		
		drawNormal(input.transformA.p, input.normal);
		
		Vec2f closestA = new Vec2f();
		Vec2f closestB = new Vec2f();
		float regionA = ClosestPoints.onLineSegment(supportPointsB[0], supportPointsA[0], supportPointsA[1], closestA);
		float regionB = ClosestPoints.onLineSegment(closestA, supportPointsB[0], supportPointsB[1], closestB);
		
		Vec2f closestDistance = new Vec2f(closestB).sub(closestA);
		output.normal.set(input.normal);
		if ((regionA < 0 || regionA > 1) || (regionB < 0 || regionB > 1)) {
			output.normal.set(closestDistance).normalize();
		}
		output.distance = closestDistance.dot(output.normal);
		output.numPoints = 1;
		output.points[0] = closestA;
		
		if (!input.wasFaceA) {
			output.points[0].addMultScalar(output.normal, output.distance);
			output.normal.invert();
		}
		
		drawPoint(output.points[0], DEFAULT_POINT_RADIUS, Colors.Yellow);
		drawNormal(output.points[0], output.normal);
	}
}
