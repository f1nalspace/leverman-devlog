package de.lman.engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import de.lman.engine.math.Scalar;
import de.lman.engine.math.Transform;
import de.lman.engine.math.Vec2f;
import de.lman.engine.math.Vec2i;
import de.lman.engine.physics.AABB;
import de.lman.engine.physics.Body;
import de.lman.engine.physics.BodyShapePair;
import de.lman.engine.physics.contacts.Contact;
import de.lman.engine.physics.shapes.CircleShape;
import de.lman.engine.physics.shapes.EdgeShape;
import de.lman.engine.physics.shapes.PlaneShape;
import de.lman.engine.physics.shapes.Shape;

public abstract class Game implements KeyListener, WindowListener, MouseListener, MouseMotionListener {
	private final String TITLE;
	private final Vec2i display = new Vec2i();
	private float metersToPixel;
	protected final Vec2f viewport = new Vec2f();
	protected float halfWidth;
	protected float halfHeight;

	private final JFrame frame;
	private final Canvas canvas;
	private final BufferedImage frameBuffer;
	private final int[] frameBufferData;
	protected final InputState inputState = new InputState();
	private final RenderTransform renderTransform;
	
	protected int numFrames;

	protected final float DEFAULT_POINT_RADIUS = 0.03f;
	protected final float DEFAULT_ARROW_LENGTH = 0.2f;
	protected final float DEFAULT_ARROW_RADIUS = 0.1f;
	
	public Game(String title, Vec2i display, float viewportWidth) {
		TITLE = title;
		
		this.display.set(display);
		metersToPixel = (float)display.x / viewportWidth;
		viewport.set(viewportWidth, (float)display.y / metersToPixel);
		halfWidth = viewport.x * 0.5f;
		halfHeight = viewport.y * 0.5f;

		renderTransform = new RenderTransform(new Vec2f(display.x * 0.5f, display.y * 0.5f), new Vec2f(metersToPixel, -metersToPixel));

		frameBuffer = new BufferedImage(display.x, display.y, BufferedImage.TYPE_INT_RGB);
		frameBufferData = ((DataBufferInt) frameBuffer.getRaster().getDataBuffer()).getData();
		
		frame = new JFrame();
		frame.setSize(display.x, display.y);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle(TITLE);
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);
		frame.addWindowListener(this);

		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(display.x, display.y));
		canvas.setIgnoreRepaint(true);
		canvas.addKeyListener(this);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		frame.add(canvas);

		frame.pack();

		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		canvas.setFocusable(true);
		canvas.requestFocusInWindow();

		Font font = new Font("Arial", 0, 200);
		
		BufferedImage fontImage = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
		Graphics g = fontImage.createGraphics();
		g.setFont(font);
		g.setColor(Color.green);
		g.fillRect(0, 0, 256, 256);
		g.setColor(Color.blue);
		g.drawString("A", 0, 255);
		g.dispose();
	}
	
	public Game(String title) {
		this(title, new Vec2i(1280, 720), 10f);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		inputState.keyState[e.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		inputState.keyState[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		for (int i = 0; i < inputState.keyState.length; i++) {
			inputState.keyState[i] = false;
		}
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		inputState.mousePos.set(unProjectX(e.getX()), unProjectY(e.getY()));
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		inputState.mousePos.set(unProjectX(e.getX()), unProjectY(e.getY()));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		inputState.mouseState[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		inputState.mouseState[e.getButton()] = false;
	}

	protected abstract void initGame();

	protected abstract void updateInput(float dt, InputState inputState);

	protected abstract void updateGame(float dt);

	protected abstract void renderGame(float dt);

	protected String getAdditionalTitle() {
		return "";
	}

	public void run() {
		initGame();

		final long TARGET_FPS = 60;
		final long NANO_SECOND = 1000000000;
		final long NANO_SECOND_FPS = NANO_SECOND / TARGET_FPS;
		final float DT = 1.0f / (float) TARGET_FPS;
		long startFPSTime = System.currentTimeMillis();
		long lastFrameTime = System.nanoTime();
		float accumulatedTime = 0.0f;
		boolean isRunning = true;
		numFrames = 0;
		while (isRunning) {
			long frameStartTime = System.nanoTime();
			float frameTime = Math.min((frameStartTime - lastFrameTime) / (float) NANO_SECOND, 0.25f);
			lastFrameTime = frameStartTime;

			// Eingaben verarbeiten
			updateInput(DT, inputState);

			// Updates einholen
			accumulatedTime += frameTime;
			while (accumulatedTime >= DT) {
				updateGame(DT);
				accumulatedTime -= DT;
			}

			// TODO: Alpha interpolation
			renderGame(DT);
			
			// Frame buffer auf den Bildschirm bringen
			Graphics graphics = canvas.getGraphics();
			graphics.drawImage(frameBuffer, 0, 0, null);
			graphics.dispose();
			numFrames++;
			
			// Anzahl an Frames jede Sekunde ausgeben und zurücksetzen
			if (System.currentTimeMillis() - startFPSTime >= 1000) {
				String addonTitle = getAdditionalTitle();
				frame.setTitle(String.format("%s%s", TITLE, addonTitle));
				startFPSTime = System.currentTimeMillis();
				numFrames = 0;
			}

			// Prozessor schlafen lassen, wenn wir schneller als 60 Bilder pro Sekunde laufen
			long sleepDuration = NANO_SECOND_FPS - (System.nanoTime() - frameStartTime);
			if (sleepDuration > 0) {
				long sleepStart = System.nanoTime();
				while (System.nanoTime() - sleepStart < sleepDuration) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {}
				}
			}
		}
	}

	private void setPixel(int x, int y, int color) {
		int index = y * display.x + x;
		frameBufferData[index] = color;
	}

	private void setPixelSafe(int x, int y, int color) {
		// TODO: Diese Methode später entfernen, weil langsam!
		if (!(x < 0 || x > display.x - 1 || y < 0 || y > display.y - 1)) {
			setPixel(x, y, color);
		}
	}

	private int projectX(float x) {
		return Scalar.roundUp(x * renderTransform.s.x + renderTransform.p.x);
	}

	private int projectY(float y) {
		return Scalar.roundUp(y * renderTransform.s.y + renderTransform.p.y);
	}
	
	private int projectR(float r) {
		return Scalar.roundUp(r * Math.abs(Math.min(renderTransform.s.x, renderTransform.s.y)));
	}

	private float unProjectX(int x) {
		return ((float) x - renderTransform.p.x) / renderTransform.s.x;
	}

	private float unProjectY(int y) {
		return ((float) y - renderTransform.p.y) / renderTransform.s.y;
	}

	protected void clear(int color) {
		for (int i = 0; i < display.x * display.y; i++) {
			frameBufferData[i] = color;
		}
	}

	protected void drawRect(int x0, int y0, int x1, int y1, int color) {
		// Richtiges min/max aus argumenten extrahieren
		int minX = Math.min(x0, x1);
		int minY = Math.min(y0, y1);
		int maxX = Math.max(x0, x1);
		int maxY = Math.max(y0, y1);

		// Nur rechtecke zeichnen die unser framebuffer schneiden
		if (!(maxX < 0 || minX > display.x - 1 || maxY < 0 || minY > display.y - 1)) {
			// Rechteck auf den sichtbaren Bereich einschr�nken
			minX = Math.max(Math.min(minX, display.x - 1), 0);
			minY = Math.max(Math.min(minY, display.y - 1), 0);
			maxX = Math.max(Math.min(maxX, display.x - 1), 0);
			maxY = Math.max(Math.min(maxY, display.y - 1), 0);

			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					setPixel(x, y, color);
				}
			}
		}
	}

	protected void drawRect(float x0, float y0, float x1, float y1, int color) {
		drawRect(projectX(x0), projectY(y0), projectX(x1), projectY(y1), color);
	}

	protected void drawLine(int x0, int y0, int x1, int y1, int color) {
		int minX = x0;
		int minY = y0;
		int maxX = x1;
		int maxY = y1;

		int dx = maxX - minX;
		int dy = maxY - minY;

		int signX = dx < 0 ? -1 : 1;
		int signY = dy < 0 ? -1 : 1;

		dx = Math.abs(dx);
		dy = Math.abs(dy);
		
		// Ersten und Letzten Pixel immer setzten
		setPixelSafe(x0, y0, color);
		setPixelSafe(x1, y1, color);

		if (dx > dy) {
			// Schnelle richtung = X
			int err = dx / 2;
			int y = 0;
			for (int x = 0; x < dx; x++) {
				err = err - dy;
				if (err < 0) {
					y++;
					err = err + dx;
				}
				setPixelSafe(minX + x * signX, minY + y * signY, color);
			}
		} else {
			// Schnelle richtung = Y
			int err = dy / 2;
			int x = 0;
			for (int y = 0; y < dy; y++) {
				err = err - dx;
				if (err < 0) {
					x++;
					err = err + dy;
				}
				setPixelSafe(minX + x * signX, minY + y * signY, color);
			}
		}
	}

	protected void drawLine(float x0, float y0, float x1, float y1, int color) {
		drawLine(projectX(x0), projectY(y0), projectX(x1), projectY(y1), color);
	}

	protected void drawRect(float x0, float y0, float x1, float y1, int color, boolean filled) {
		if (filled) {
			drawRect(x0, y0, x1, y1, color);
		} else {
			drawLine(x0, y0, x0, y1, color);
			drawLine(x0, y1, x1, y1, color);
			drawLine(x1, y1, x1, y0, color);
			drawLine(x1, y0, x0, y0, color);
		}
	}

	protected void drawPoint(float x, float y, float radius, int color) {
		drawCircle(x, y, radius, color, true);
	}

	protected void drawPoint(Vec2f v, float radius, int color) {
		drawPoint(v.x, v.y, radius, color);
	}

	protected void drawCircle(int cx, int cy, int radius, int color, boolean filled) {
		int f = (5 - radius) / 4;
		int x = 0;
		int y = radius;
		do {
			if (!filled) {
				setPixelSafe(cx + x, cy + y, color);
				setPixelSafe(cx + -x, cy + y, color);

				setPixelSafe(cx + -x, cy + -y, color);
				setPixelSafe(cx + x, cy + -y, color);

				setPixelSafe(cx + y, cy + x, color);
				setPixelSafe(cx + -y, cy + x, color);

				setPixelSafe(cx + -y, cy + -x, color);
				setPixelSafe(cx + y, cy + -x, color);
			} else {
				drawLine(cx + x, cy + y, cx + -x, cy + y, color);
				drawLine(cx + -x, cy - y, cx + x, cy - y, color);

				drawLine(cx + y, cy + x, cx + -y, cy + x, color);
				drawLine(cx + -y, cy + -x, cx + y, cy + -x, color);
			}
			if (f < 0) {
				f += 2 * x + 1;
			} else {
				f += 2 * (x - y) + 1;
				y--;
			}
			x++;
		} while (x <= y);
	}

	protected void drawCircle(float cx, float cy, float radius, int color, boolean filled) {
		drawCircle(projectX(cx), projectY(cy), projectR(radius), color, filled);
	}
	
	protected void drawNormal(Vec2f center, Vec2f normal, float arrowRadius, float arrowLen, int color) {
		Vec2f perp = new Vec2f(normal).perpRight();
		Vec2f arrowTip = new Vec2f(center).addMultScalar(normal, arrowLen);
		drawLine(center.x, center.y, arrowTip.x, arrowTip.y, color);
		drawLine(arrowTip.x, arrowTip.y, arrowTip.x + perp.x * arrowRadius + normal.x * -arrowRadius, arrowTip.y + perp.y * arrowRadius + normal.y * -arrowRadius, color);
		drawLine(arrowTip.x, arrowTip.y, arrowTip.x + -perp.x * arrowRadius + normal.x * -arrowRadius, arrowTip.y + -perp.y * arrowRadius + normal.y * -arrowRadius, color);
	}

	protected void drawNormal(Vec2f center, Vec2f normal) {
		drawNormal(center, normal, DEFAULT_ARROW_RADIUS, DEFAULT_ARROW_LENGTH, Colors.White);
	}
	
	protected void drawAABBs(int numBodies, Body[] bodies) {
		for (int i = 0; i < numBodies; i++) {
			Body body = bodies[i];
			AABB aabb = body.aabb;
			drawRect(aabb.min.x, aabb.min.y, aabb.max.x, aabb.max.y, 0x00FF00, false);
		}
	}

	protected void drawBodies(int numBodies, Body[] bodies) {
		int bodyColor = 0xFFFFFF;

		// Dynamische Objekte zeichnen
		for (int i = 0; i < numBodies; i++) {
			Body body = bodies[i];
			for (int shapeIndex = 0; shapeIndex < body.numShapes; shapeIndex++) {
				Shape shape = body.shapes[shapeIndex];
				Transform t = new Transform(shape.localPos, shape.localRotation).offset(body.pos);
				switch (shape.type) {
					case Circle:
						CircleShape circle = (CircleShape) shape;
						drawCircle(t.p.x, t.p.y, circle.radius, bodyColor, false);
						break;
					case LineSegment:
					case Box:
					case Polygon: {
						EdgeShape edge = (EdgeShape) shape;
						Vec2f[] localVertices = edge.getLocalVertices();
						int numVertices = edge.getVertexCount();
						for (int j = 0; j < numVertices; j++) {
							Vec2f v0 = new Vec2f(localVertices[j]).transform(t);
							Vec2f v1 = new Vec2f(localVertices[(j + 1) % numVertices]).transform(t);
							drawLine(v0.x, v0.y, v1.x, v1.y, bodyColor);
						}
						break;
					}
					case Plane: {
						PlaneShape plane = (PlaneShape) shape;
						Vec2f normal = t.q.col1;
						Vec2f center = t.p;
						Vec2f perp = new Vec2f(normal).perpRight();
						Vec2f startPoint = new Vec2f(center).addMultScalar(perp, plane.len * 0.5f);
						Vec2f endPoint = new Vec2f(center).addMultScalar(perp, -plane.len * 0.5f);
						drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, bodyColor);
						break;
					}
					default:
						break;
				}
				drawPoint(t.p.x, t.p.y, DEFAULT_POINT_RADIUS, Colors.Yellow);
			}
			drawPoint(body.pos.x, body.pos.y, DEFAULT_POINT_RADIUS, Colors.White);
		}
	}

	protected void drawContacts(float dt, int numPairs, BodyShapePair[] pairs, boolean drawDistance, boolean drawImpulses) {
		for (int i = 0; i < numPairs; i++) {
			BodyShapePair pair = pairs[i];
			for (int j = 0; j < pair.numContacts; j++) {
				Contact contact = pair.contacts[j];
				Vec2f normal = contact.normal;
				Vec2f closestPointOnA = new Vec2f(pair.a.pos).add(contact.pointA);
				Vec2f closestPointOnB = new Vec2f(closestPointOnA).addMultScalar(normal, contact.distance);
				// TODO: Better colors
				drawPoint(closestPointOnA.x, closestPointOnA.y, DEFAULT_POINT_RADIUS, Colors.GreenYellow);
				drawPoint(closestPointOnB.x, closestPointOnB.y, DEFAULT_POINT_RADIUS, Colors.MediumVioletRed);
				drawNormal(closestPointOnA, normal);
				if (drawDistance) {
					drawLine(closestPointOnA.x, closestPointOnA.y, closestPointOnB.x, closestPointOnB.y, Colors.Red);
				}
				if (drawImpulses) {
					drawLine(closestPointOnA.x, closestPointOnA.y, closestPointOnA.x + -contact.normal.x * contact.normalImpulse * dt * dt, closestPointOnA.y + -contact.normal.y * contact.normalImpulse * dt * dt, Colors.Yellow);
				}
			}
		}
	}
}
