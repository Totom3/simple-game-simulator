package me.totom3;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SimpleBallEngine extends AbstractEngine {

	private static final int TICK_DELAY = 1;
	private static final double GRAVITY = 0.0004;
	private static final double VELOCITY_LOSS = 0.95;
	private static final double RADIUS = 30;

	private final Circle ball;

	private volatile Vec2D position;
	private volatile Vec2D velocity;

	public SimpleBallEngine(Scene scene, Pane pane) {
		super(scene, pane);

		this.position = new Vec2D(50, 50);
		this.velocity = new Vec2D(0.5, 0.4);

		this.ball = new Circle(RADIUS);
		this.ball.setFill(Color.RED);
		this.ball.setCenterX(position.getX());
		this.ball.setCenterY(position.getY());

		pane.getChildren().add(ball);
	}

	@Override
	protected void tick0() {
		if (handleCollisions()) {
			return;
		}

		// 1. Compute acceleration
		Vec2D acceleration = new Vec2D(0, GRAVITY);

		// 2. Compute velocity
		Vec2D newVelocity = velocity.plus(acceleration);

		// 3. Compute position
		position = position.plus(velocity.plus(newVelocity).divide(2));

		// 4. Update velocity
		velocity = newVelocity;
	}

	private boolean handleCollisions() {
		boolean collision = false;
		double x = position.getX();
		double y = position.getY();
		double velX = velocity.getX() * VELOCITY_LOSS;
		double velY = velocity.getY() * VELOCITY_LOSS;

		if (y + RADIUS > screenHeight()) {
			velY = -Math.abs(velY);
			collision = true;
		} else if (y - RADIUS < 0) {
			velY = Math.abs(velY);
			collision = true;
		}

		if (x + RADIUS > screenWidth()) {
			velX = -Math.abs(velX);
			collision = true;
		} else if (x - RADIUS < 0) {
			velX = Math.abs(velX);
			collision = true;
		}

		if (collision) {
			velocity = new Vec2D(velX, velY);
			position = position.plus(velocity);
		}

		return collision;
	}

	@Override
	public void render() {
		// Update ball location
		ball.setCenterX(position.getX());
		ball.setCenterY(position.getY());
	}

	@Override
	public int getTickDelay() {
		return TICK_DELAY;
	}

}
