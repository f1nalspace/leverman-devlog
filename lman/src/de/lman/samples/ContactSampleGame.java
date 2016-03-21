package de.lman.samples;

import java.util.ArrayList;
import java.util.List;

import de.lman.engine.Game;
import de.lman.engine.InputState;
import de.lman.engine.Keys;
import de.lman.engine.Mouse;
import de.lman.engine.math.Scalar;
import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.BodyShapePair;
import de.lman.engine.physics.GeometryUtils;
import de.lman.engine.physics.Physics;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactAcceptor;
import de.lman.engine.physics.contacts.ContactGeneratorFactory;
import de.lman.engine.physics.shapes.BoxShape;
import de.lman.engine.physics.shapes.CircleShape;
import de.lman.engine.physics.shapes.LineSegmentShape;
import de.lman.engine.physics.shapes.PlaneShape;
import de.lman.engine.physics.shapes.Shape;
import de.lman.engine.physics.shapes.ShapeType;

public class ContactSampleGame extends Game {
	
	public ContactSampleGame() {
		super("Contact Samples");
	}

	public static void main(String[] args) {
		ContactSampleGame samples = new ContactSampleGame();
		samples.run();
	}
	
	private final boolean showContacts = true;
	private boolean showAABBs = true;

	private final int MAX_BODIES = 2;
	public final Body[] bodies = new Body[MAX_BODIES];
	public int numBodies = 0;
	
	private final int MAX_BODY_PAIRS = 1;
	public final BodyShapePair[] pairs = new BodyShapePair[MAX_BODY_PAIRS];
	public int numPairs = 0;

	public int numContacts;

	private final ContactGeneratorFactory contactGenFactory = new ContactGeneratorFactory();
	
	class Scenario {
		public final Body a;
		public final Body b;

		public Scenario(Body a, Body b) {
			this.a = a;
			this.b = b;
		}
		
		@Override
		public String toString() {
			final int l = "Shape".length();
			String nA = a.shapes[0].getClass().getSimpleName();
			String nB = b.shapes[0].getClass().getSimpleName();
			return nA.substring(0, nA.length() - l) + " vs " + nB.substring(0, nB.length() - l);
		}
	}
	
	private final List<Scenario> scenarios = new ArrayList<>();
	private int activeScenarioIndex = -1;
	private Scenario activeScenario;

	protected void initGame() {
		numBodies = 0;
		numPairs = 0;
		
		// Plane vs Circle
		{
			Body a, b;
			a = new Body().addShape(new PlaneShape(viewport.y));
			a.pos.set(-halfWidth + 1, 0);
			b = new Body().addShape(new CircleShape(1));
			b.pos.set(0, 0);
			scenarios.add(new Scenario(a, b));
		}

		// Circle vs Circle
		{
			Body a, b;
			a = new Body().addShape(new CircleShape(1f));
			a.pos.set(0, 0);
			b = new Body().addShape(new CircleShape(0.5f));
			b.pos.set(0, 0);
			scenarios.add(new Scenario(a, b));
		}

		// Line-Segment vs Circle
		{
			Body a, b;
			a = new Body().addShape(new LineSegmentShape(1f).rotation(Scalar.PI * 0.1f));
			a.pos.set(0, 0 - 1f);
			b = new Body().addShape(new CircleShape(1f));
			b.pos.set(0, 0);
			scenarios.add(new Scenario(a, b));
		}
		
		// Box vs Box
		{
			Body a, b;
			a = new Body().addShape(new BoxShape(new Vec2f(1f, 0.6f)));
			a.pos.set(0, 0);
			b = new Body().addShape(new BoxShape(new Vec2f(0.5f, 1.2f)));
			b.pos.set(0, 0);
			scenarios.add(new Scenario(a, b));
		}

		// Circle vs Box
		{
			Body a, b;
			b = new Body().addShape(new BoxShape(new Vec2f(1, 0.6f)));
			b.pos.set(-halfWidth + 3f, 0 - 1.5f);
			a = new Body().addShape(new CircleShape(1f));
			a.pos.set(halfWidth - 2f, 0 + 0.5f);
			scenarios.add(new Scenario(a, b));
		}

		// Nicht-Senkrechte Ebene vs Box
		{
			float angle = Scalar.PI * 0.6f;
			Body a, b;
			a = new Body().addShape(new PlaneShape(viewport.x).rotation(angle));
			a.pos.set(0, 0 - 1.5f);
			b = new Body().addShape(new BoxShape(new Vec2f(1, 0.6f)));
			b.pos.set(halfWidth - 2f, 0 + 0.5f);
			scenarios.add(new Scenario(a, b));
		}

		// Linien-Segment vs Box
		{
			Body a, b;
			a = new Body().addShape(new LineSegmentShape(1f).rotation(Scalar.PI * 0.1f));
			a.pos.set(0 - 1f, 0 - 1f);
			b = new Body().addShape(new BoxShape(new Vec2f(1f, 0.6f)));
			b.pos.set(1f, 0);
			scenarios.add(new Scenario(a, b));
		}

		// Ebene-Rechts vs Box
		{
			float angle = Scalar.PI * 0f;
			Body a, b;
			a = new Body().addShape(new PlaneShape(viewport.y).rotation(angle));
			a.pos.set(-halfWidth + 1f, 0);
			b = new Body().addShape(new BoxShape(new Vec2f(1f, 0.6f)));
			b.pos.set(halfWidth - 1f, 0 + 0.5f);
			scenarios.add(new Scenario(a, b));
		}

		// Ebene-Links vs Box
		{
			float angle = Scalar.PI * 1f;
			Body a, b;
			a = new Body().addShape(new PlaneShape(viewport.y).rotation(angle));
			a.pos.set(halfWidth - 1f, 0);
			b = new Body().addShape(new BoxShape(new Vec2f(1f, 0.6f)));
			b.pos.set(halfWidth - 2f, 0 + 0.5f);
			scenarios.add(new Scenario(a, b));
		}

		// Box vs Box (Internal-Edge-Test)
		{
			Body a, b;
			a = new Body().addShape(new BoxShape(new Vec2f(0.5f, 0.5f)));
			a.pos.set(0 + 1f, 0);
			b = new Body().addShape(new BoxShape(new Vec2f(0.5f, 0.5f)));
			b.pos.set(0, 0 + 1f);
			scenarios.add(new Scenario(a, b));
		}

		loadScenario(scenarios.size()-1);
		//loadScenario(0);
	}

	private void loadScenario(int index) {
		assert(index < scenarios.size());
		Scenario scenario = scenarios.get(index);
		
		numPairs = 1;
		numBodies = 2;
		BodyShapePair pair = new BodyShapePair(scenario.a, scenario.b, scenario.a.shapes[0], scenario.b.shapes[0]);
		pairs[0] = pair;
		bodies[0] = pair.a;
		bodies[1] = pair.b;
		
		activeScenarioIndex = index;
		activeScenario = scenario;
	}

	private void toggleScenario() {
		activeScenarioIndex++;
		if (activeScenarioIndex > scenarios.size() - 1) {
			activeScenarioIndex = 0;
		}
		loadScenario(activeScenarioIndex);
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

		// AABBs ein/ausschalten
		if (inputState.isKeyDown(Keys.SPACE)) {
			toggleScenario();
			inputState.setKeyDown(Keys.SPACE, false);
		}
	}
	
	@Override
	protected String getAdditionalTitle() {
		return String.format(" [Scenario: %d/%d - %s, Frames: %d, Bodies: %d, Contacts: %d]", (activeScenarioIndex + 1), scenarios.size(), activeScenario, numFrames, numBodies, numContacts);
	}

	protected void updateGame(float dt) {
		for (int i = 0; i < numPairs; i++) {
			BodyShapePair pair = pairs[i];
			pair.a.updateAABB(pair.a.pos, pair.a.pos, Physics.AABB_TOLERANCE);
			pair.b.updateAABB(pair.b.pos, pair.b.pos, Physics.AABB_TOLERANCE);
		}
		
		assert(numPairs > 0);
		BodyShapePair pair = pairs[0];
		Shape shapeA = pair.a.shapes[0];
		Shape shapeB = pair.b.shapes[0];
		Transform transformA = new Transform(shapeA.localPos, shapeA.localRotation).offset(pair.a.pos);
		Transform transformB = new Transform(shapeB.localPos, shapeB.localRotation).offset(pair.b.pos);
		pair.numContacts = contactGenFactory.generate(transformA, transformB, shapeA, shapeB, 0, pair.contacts, new ContactAcceptor() {
			@Override
			public boolean accept(Contact contact) {
				return true;
			}
		});
		numContacts = pair.numContacts;
	}
	
	protected void renderGame(float dt) {
		clear(0x000000);

		drawBodies(numBodies, bodies);
		
		if (showAABBs) {
			drawAABBs(numBodies, bodies);
		}

		if (showContacts) {
			drawContacts(dt, numPairs, pairs, true, false);
		}

		// Visualiserungs-Template
//		assert(numPairs > 0);
//		BodyPair pair = pairs[0];
//		assert(ShapeType.PLANE.equals(pair.a.shapes[0].type) && ShapeType.BOX.equals(pair.b.shapes[0].type));
//		PlaneShape planeA = (PlaneShape) pair.a.shapes[0];
//		BoxShape boxB = (BoxShape) pair.b.shapes[0];
//		Body bodyA = pair.a;
//		Body bodyB = pair.b;
	}

}
