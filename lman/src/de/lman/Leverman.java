package de.lman;

import de.lman.engine.Colors;
import de.lman.engine.Game;
import de.lman.engine.InputState;
import de.lman.engine.Keys;
import de.lman.engine.Mouse;
import de.lman.engine.math.Mat2f;
import de.lman.engine.math.Scalar;
import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.ContactStatePair;
import de.lman.engine.physics.GeometryUtils;
import de.lman.engine.physics.Physics;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.contacts.ContactListener;
import de.lman.engine.physics.shapes.BoxShape;
import de.lman.engine.physics.shapes.EdgeShape;
import de.lman.engine.physics.shapes.PhysicsMaterial;
import de.lman.engine.physics.shapes.PlaneShape;
import de.lman.engine.physics.shapes.PolygonShape;
import de.lman.engine.physics.shapes.Shape;

/*

Probleme lösen:

- Linien-Zeichnen korrigieren

Aufgaben:

- Tastatureingaben robuster machen (ist gedrückt, war gedrückt) 

- Bitmap laden, konvertieren und zeichnen
- Bitmap transformiert zeichnen (Rotation)
- Bitmap fonts generieren
- Bitmap fonts zeichnen
- Bitmap Bilinär filtern

- Debug Informationen
  - Fps / Framedauer anzeigen
  - Zeitmessungen
  - Speichern für mehrere Frames
  - Visualisierung (Bar, Line)
  
- UI
	- Panel
	- Button
	- Label
	- Checkbox
	- Radiobutton

- Sensor-Flag für Shape

- ECS

- Integrierter-Level-Editor
	- Skalierung
		- Propertionales vergrößern von Seiten
		- Verschiebung von Eckpunkten
	- Löschen
	- Kopieren / Einfügen
	- Gitter-Snap
	- Mehrere Shapetypen + Auswahlmöglichkeit:
	    - Kreis
	    - Linien-Segment
	    - Polygone
	    - Boxen
	    - Ebenen
	- Laden / Speichern
		- Körper & Formen serialisieren und deserialisieren (JSON)
  
- Rotationsdynamik:
	- Kreuzprodukt
	- updateAABB in Body robuster machen
	- getSupportPoints vereinfachen / robuster machen
	- Massenzentrum (COM)
	- Traegheitsmoment (Inertia, AngularVelocity)
	- Kontaktausschnitt

- Asset-Management
	- Preloading
	
- Erstellung von Kontakt-Szenarien vereinfachen -> offset()

*/
public class Leverman extends Game implements ContactListener {

	public Leverman() {
		super("Leverman");
	}

	public static void main(String[] args) {
		Leverman game = new Leverman();
		game.run();
	}

	private Physics physics;
	private boolean showContacts = true;
	private boolean showAABBs = false;
	private boolean physicsSingleStep = false;

	public final static PhysicsMaterial MAT_STATIC = new PhysicsMaterial(0f, 0.1f);
	public final static PhysicsMaterial MAT_DYNAMIC = new PhysicsMaterial(1f, 0.1f);

	private Body playerBody;
	private boolean playerOnGround = false;
	private boolean playerJumping = false;
	private int playerGroundHash = 0;
	private final Vec2f groundNormal = new Vec2f(0, 1);

	@Override
	public void physicsBeginContact(int hash, ContactStatePair pair) {
		Contact contact = pair.pair.contacts[pair.contactIndex];
		Vec2f normal = contact.normal;
		Body player = null;
		if (pair.pair.a.id == playerBody.id) {
			player = pair.pair.a;
			normal = new Vec2f(contact.normal).invert();
		} else if (pair.pair.b.id == playerBody.id) {
			player = pair.pair.b;
		}
		float d = normal.dot(groundNormal);
		if (d > 0) {
			if (player != null && (!playerOnGround)) {
				playerOnGround = true;
				playerGroundHash = hash;
			}
		}
	}

	@Override
	public void physicsEndContact(int hash, ContactStatePair pair) {
		Contact contact = pair.pair.contacts[pair.contactIndex];
		Vec2f normal = contact.normal;
		Body player = null;
		if (pair.pair.a.id == playerBody.id) {
			player = pair.pair.a;
			normal = new Vec2f(contact.normal).invert();
		} else if (pair.pair.b.id == playerBody.id) {
			player = pair.pair.b;
		}
		float d = normal.dot(groundNormal);
		if (d > 0) {
			if (player != null && playerOnGround && (playerGroundHash == hash)) {
				playerOnGround = false;
				playerGroundHash = 0;
			}
		}
	}

	private void addPlatform(float x, float y, float rx, float ry) {
		Body body;
		physics.addBody(body = new Body().addShape(new BoxShape(new Vec2f(rx, ry)).setMaterial(MAT_STATIC)));
		body.pos.set(x, y);
	}

	private void addBox(float x, float y, float rx, float ry) {
		Body body;
		physics.addBody(body = new Body().addShape(new BoxShape(new Vec2f(rx, ry)).setMaterial(MAT_DYNAMIC)));
		body.pos.set(x, y);
	}
	
	protected void initGame() {
		physics = new Physics(this);
		physics.enableSingleStepMode(physicsSingleStep);
		
		Body body;
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.y).rotation(Scalar.PI * 0f).setMaterial(MAT_STATIC)));
		body.pos.set(-halfWidth + 0.5f, 0);
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.y).rotation(Scalar.PI * 1f).setMaterial(MAT_STATIC)));
		body.pos.set(halfWidth - 0.5f, 0);
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.x).rotation(Scalar.PI * 0.5f).setMaterial(MAT_STATIC)));
		body.pos.set(0, -halfHeight + 0.5f);
		physics.addBody(body = new Body().addShape(new PlaneShape(viewport.x).rotation(Scalar.PI * 1.5f).setMaterial(MAT_STATIC)));
		body.pos.set(0, halfHeight - 0.5f);

		addPlatform(0 - 2.9f, 0 - 2.0f, 0.6f, 0.1f);
		addPlatform(0, 0 - 1.3f, 0.7f, 0.1f);
		addPlatform(0 + 2.9f, 0 - 0.8f, 0.6f, 0.1f);
		//addBox(0, -0.5f, 0.2f, 0.2f);
		addPlatform(0 + 2.0f, -halfHeight + 0.2f + 0.5f, 0.2f, 0.2f);
		addPlatform(0 + 2.4f, -halfHeight + 0.2f + 0.5f, 0.2f, 0.2f);
		addPlatform(0 + 2.8f, -halfHeight + 0.2f + 0.5f, 0.2f, 0.2f);
		addPlatform(0 + 3.2f, -halfHeight + 0.2f + 0.5f, 0.2f, 0.2f);
		
		playerBody = new Body();
		BoxShape playerBox = (BoxShape) new BoxShape(new Vec2f(0.2f, 0.4f)).setMaterial(MAT_DYNAMIC);
		playerBody.addShape(playerBox);
		playerBody.pos.set(0, -halfHeight + playerBox.radius.y + 0.5f);
		physics.addBody(playerBody);

		Vec2f[] polyVerts = new Vec2f[]{
			new Vec2f(0, 0.5f),
			new Vec2f(-0.5f, -0.5f),
			new Vec2f(0.5f, -0.5f),
		};
		physics.addBody(body = new Body().addShape(new PolygonShape(polyVerts).setMaterial(MAT_STATIC)));
		body.pos.set(0, 0);
	}

	private boolean dragging = false;
	private Vec2f dragStart = new Vec2f();
	private Body dragBody = null;
	
	private void updateGameInput(float dt, InputState inputState) {
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

		// Einzelschritt-Physik-Modus ein/ausschalten
		if (inputState.isKeyDown(Keys.F5)) {
			physicsSingleStep = !physicsSingleStep;
			physics.enableSingleStepMode(physicsSingleStep);
			inputState.setKeyDown(Keys.F5, false);
		}
		if (inputState.isKeyDown(Keys.F6)) {
			if (physicsSingleStep) {
				physics.nextStep();
			}
			inputState.setKeyDown(Keys.F6, false);
		}

		// Player bewegen
		if (inputState.isKeyDown(Keys.W)) {
			if (!playerJumping && playerOnGround) {
				playerBody.acc.y += 4f / dt;
				playerJumping = true;
			}
		} else {
			if (playerJumping && playerOnGround) {
				playerJumping = false;
			}
		}
		if (inputState.isKeyDown(Keys.A)) {
			playerBody.acc.x -= 0.1f / dt;
		} else if (inputState.isKeyDown(Keys.D)) {
			playerBody.acc.x += 0.1f / dt;
		}
	}

	private final Editor editor = new Editor();

	private boolean editorWasShownAABB = false;

	protected void updateInput(float dt, InputState input) {
		// Editormodus ein/ausschalten
		if (input.isKeyDown(Keys.F4)) {
			editor.active = !editor.active;
			if (editor.active) {
				editorWasShownAABB = showAABBs;
				showAABBs = true;
				editor.init(physics);
			} else {
				showAABBs = editorWasShownAABB;
			}
			input.setKeyDown(Keys.F4, false);
		}
		
		if (editor.active) {
			editor.updateInput(dt, physics, input);
		} else {
			updateGameInput(dt, input);
		}
	}

	@Override
	protected String getAdditionalTitle() {
		return String.format(" [Frames: %d, Bodies: %d, Contacts: %d]", numFrames, physics.numBodies, physics.numContacts);
	}

	protected void updateGame(float dt) {
		if (!editor.active) {
			physics.step(dt);
		}
	}
	
	private void renderEditor(float dt) {
		// TODO: Move to editor class
		clear(0x000000);
				
		for (int i = 0; i < viewport.x / Editor.GRID_SIZE; i++) {
			drawLine(-halfWidth + i * Editor.GRID_SIZE, -halfHeight, -halfWidth + i * Editor.GRID_SIZE, halfHeight, Colors.DarkSlateGray);
		}
		for (int i = 0; i < viewport.y / Editor.GRID_SIZE; i++) {
			drawLine(-halfWidth, -halfHeight + i * Editor.GRID_SIZE, halfWidth, -halfHeight + i * Editor.GRID_SIZE, Colors.DarkSlateGray);
		}
		
		drawBodies(physics.numBodies, physics.bodies);

		if (editor.selectedBody != null) {
			Editor.DragSide[] dragSides = editor.getDragSides(editor.selectedBody);
			Shape shape = editor.selectedBody.shapes[0];
			if (dragSides.length > 0 && shape instanceof EdgeShape) {
				EdgeShape edgeShape = (EdgeShape) shape;
				Transform t = new Transform(shape.localPos, shape.localRotation).offset(editor.selectedBody.pos);
				Vec2f[] localVertices = edgeShape.getLocalVertices();
				for (int i = 0; i < dragSides.length; i++) {
					Vec2f dragPoint = dragSides[i].center;
					drawPoint(dragPoint, Editor.DRAGPOINT_RADIUS, Colors.White);
					if (editor.resizeSideIndex == i) {
						Vec2f v0 = new Vec2f(localVertices[dragSides[i].index0]).transform(t);
						Vec2f v1 = new Vec2f(localVertices[dragSides[i].index1]).transform(t);
						drawPoint(v0, Editor.DRAGPOINT_RADIUS, Colors.GoldenRod);
						drawPoint(v1, Editor.DRAGPOINT_RADIUS, Colors.GoldenRod);
					}
				}
			}
			
			Mat2f mat = new Mat2f(shape.localRotation).transpose();
			drawNormal(editor.selectedBody.pos, mat.col1, DEFAULT_ARROW_RADIUS, DEFAULT_ARROW_LENGTH, Colors.Red);
		}

		drawPoint(inputState.mousePos.x, inputState.mousePos.y, DEFAULT_POINT_RADIUS, 0x0000FF);
	}
	
	private void renderInternalGame(float dt) {
		clear(0x000000);
		
		drawBodies(physics.numBodies, physics.bodies);
		if (showAABBs) {
			drawAABBs(physics.numBodies, physics.bodies);
		}
		if (showContacts) {
			drawContacts(dt, physics.numPairs, physics.pairs, false, true);
		}
	}

	protected void renderGame(float dt) {
		if (editor.active) {
			renderEditor(dt);
		} else {
			renderInternalGame(dt);
		}
	}

}
