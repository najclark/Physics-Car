import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Car extends JPanel implements Runnable {

	private static final long serialVersionUID = 007;
	private BufferedImage car = null;
	private Image dbImage = null;
	private Graphics dbg;
	private float x = 100F, y = 100F;
	private Thread driveThread = new Thread(this);
	private double currentAngle = 0; // angel of the car
	private static int[] key = new int[256]; // keyboard input
	private float MAX_SPEED = 10F;
	private float speed = 0F; // speed of our racing car
	private float acceleration = 0.15F;
	private int player;

	private double force = 0;
	private double traction = 10;
	private double mass = 10;
	private int wheelAngle = 0; // angel of the front wheels
	private double wheelBase = 100;
	private double turnDirection = 0;
	private boolean drifting = false;

	public Car(int player) {

		this.player = player;

		this.setSize(super.getHeight(), super.getWidth());
		this.setFocusable(true); // enables keyboard

		try {
			if (player == 1) {
				// red car
				car = ImageIO.read(this.getClass().getResource("car.png"));

				wheelBase = car.getHeight();
				System.out.println(car.getColorModel());
			} else if (player == 2) {
				// blue car
				car = ImageIO.read(this.getClass().getResource("car.png"));
				x = x + 30;
			}
			// background =
			// ImageIO.read(this.getClass().getResource("ressources/level1.png"));

		} catch (IOException e) {
			System.out.println("dupi");
		}

		// starts the drive thread
		startGame();

	}

	private void startGame() {
		// TODO Auto-generated method stub
		driveThread.start();
	}

	public void paint(Graphics g) {
		super.paint(g);

		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();
		paintComponent(dbg);
		g.drawImage(dbImage, 0, 0, this);
		force = mass * (speed * speed) / (wheelBase * speed / wheelAngle);
		if (force > traction) {
			drifting = true;
			if (turnDirection == 0) {
				turnDirection = Double.valueOf(currentAngle);
				System.out.println(turnDirection);
			}
			g.drawString(String.valueOf("Drift: " + force), 100, 100);
		} else {
			drifting = false;
			turnDirection = 0;
			g.drawString(String.valueOf("Not Drifting: " + force), 100, 100);
		}
		g.drawString(String.valueOf("Radius: " + (wheelBase * speed / wheelAngle)), 100, 150);
		g.drawString(String.valueOf("Wheel Angle: " + (wheelAngle)), 100, 200);
	}

	@Override
	protected void paintComponent(Graphics g) {

		// super.paintComponent(g);
		this.setOpaque(false);

		// rotation
		Graphics2D g2d = (Graphics2D) g;
		AffineTransform rot = g2d.getTransform();
		// Rotation at the center of the car
		float xRot = x + 12.5F;
		float yRot = y + 20F;
		rot.rotate(Math.toRadians(currentAngle), xRot, yRot);
		g2d.setTransform(rot);
		// Draws the cars new position and angle
		g2d.drawImage(car, (int) x, (int) y, 25, 40, this);
		// g2d.drawString(String.valueOf(currentAngle%360), 100, 100);
	}

	protected void calculateCarPosition() {

		// calculates the new X and Y - coordinates
		if (!drifting) {
			x += Math.sin(currentAngle * Math.PI / 180) * speed * 0.5;
			y += Math.cos(currentAngle * Math.PI / 180) * -speed * 0.5;
		} else {
			x += Math.sin(turnDirection * Math.PI / 180) * speed * 0.5;
			y += Math.cos(turnDirection * Math.PI / 180) * -speed * 0.5;
		}

		if (x < 0) {
			x = 0;
		} else if (x > this.getWidth()) {
			x = this.getWidth();
		}

		if (y < 0) {
			y = 0;
		} else if (y > this.getHeight()) {
			y = this.getHeight();
		}

	}

	protected void carMovement() {

		// Player One Key's

		if (drifting) {

			if (wheelAngle > 0) {
				currentAngle += 2;
			} else if (wheelAngle < 0) {
				currentAngle -= 2;
			}

//			if (turnDirection > 0) {
//				currentAngle += 2;
//			} else if (turnDirection < 0) {
//				currentAngle -= 2;
//			}
		}

		if (player == 1) {

			if (key[KeyEvent.VK_LEFT] != 0) {
				currentAngle -= 2;
				wheelAngle -= 1;

			} else if (key[KeyEvent.VK_RIGHT] != 0) {
				currentAngle += 2;
				wheelAngle += 1;

			} else {
				if (wheelAngle > 0) {
					wheelAngle -= 1;
				} else if (wheelAngle < 0) {
					wheelAngle += 1;
				}
			}

			if (wheelAngle > 30) {
				wheelAngle = 30;
			} else if (wheelAngle < -30) {
				wheelAngle = -30;
			}

			if (key[KeyEvent.VK_UP] != 0) {

				if (speed < MAX_SPEED) {

					speed += acceleration;
				}

			} else if (key[KeyEvent.VK_DOWN] != 0 && speed > -1) {
				speed = speed - 0.1F;
			}
			speed = speed * 0.99F;

		} else {

			// Player Two Key's

			if (key[KeyEvent.VK_A] != 0) {
				currentAngle -= 2;

			} else if (key[KeyEvent.VK_D] != 0) {
				currentAngle += 2;
			}

			if (key[KeyEvent.VK_W] != 0) {

				if (speed < MAX_SPEED) {

					speed += acceleration;
				}

			} else if (key[KeyEvent.VK_S] != 0 && speed > -1) {
				speed = speed - 0.1F;
			}
			// reduce speed when no key is pressed
			speed = speed * 0.99F;
		}

	}

	public void getUnderground() {

	}

	// get key events!
	final protected void processKeyEvent(KeyEvent e) {
		key[e.getKeyCode()] = e.getID() & 1;
	}

	@Override
	public void run() {
		int fps = 100;
		long start = System.currentTimeMillis();
		while (true) {
			start = System.currentTimeMillis();
			repaint();
			carMovement();
			calculateCarPosition();

			try {
				int sleep = (int) ((1000 / fps) - (System.currentTimeMillis() - start));

				if (sleep > 0) {
					Thread.sleep(sleep);
				} else {
					System.out.println("Computer can't keep up with " + fps + " fps!");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test 2d Car");
		Car c = new Car(1);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(c);
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

}