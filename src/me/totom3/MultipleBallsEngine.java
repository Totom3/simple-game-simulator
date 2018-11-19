package me.totom3;

import java.util.Random;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MultipleBallsEngine extends AbstractEngine {

	private static final int TICK_DELAY = 1;
	private static final double GRAVITY = 0.0004;
	private static final double VELOCITY_LOSS = 0.1;
	private static final double RADIUS = 30;

	private final GameBall[] balls;

	public MultipleBallsEngine(Scene scene, Pane pane) {
		super(scene, pane);

		this.balls = new GameBall[10];

		Random rand = new Random();
		for (int i = 0; i < balls.length; ++i) {
			balls[i] = new GameBall(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)),
					50 + 700 * rand.nextDouble(),
					50 + 700 * rand.nextDouble(),
					0.6 * (2 * rand.nextDouble() - 1),
					0.6 * (2 * rand.nextDouble() - 1));
		}

		ObservableList<Node> children = pane.getChildren();
		for (GameBall ball : balls) {
			children.add(ball.circle);
		}

	}

	@Override
	protected void tick0() {
		for (int i = 0; i < balls.length; ++i) {
			GameBall ball = balls[i];
			if (handleWallCollisions(ball)) {
				continue;
			}

			if (handleBallCollisions(i)) {
				continue;
			}

			// 1. Compute acceleration
			Vec2D acceleration = new Vec2D(0, GRAVITY);

			// 2. Compute velocity
			Vec2D newVelocity = ball.velocity.plus(acceleration);

			// 3. Compute position
			ball.position = ball.position.plus(ball.velocity.plus(newVelocity).divide(2));

			// 4. Update velocity
			ball.velocity = newVelocity;
		}

	}

	private boolean handleWallCollisions(GameBall ball) {
		boolean collision = false;
		double x = ball.position.getX();
		double y = ball.position.getY();
		double velX = ball.velocity.getX() * VELOCITY_LOSS;
		double velY = ball.velocity.getY() * VELOCITY_LOSS;

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
			ball.velocity = new Vec2D(velX, velY);
			ball.position = ball.position.plus(ball.velocity);
		}

		return collision;
	}

	private boolean handleBallCollisions(int i) {
		GameBall ball1 = balls[i];
		for (int j = i + 1; j < balls.length; ++j) {
			GameBall ball2 = balls[j];
			Vec2D vel1 = ball1.velocity;
			Vec2D vel2 = ball2.velocity;

			// Check for collision
			Vec2D normal = ball1.position.minus(ball2.position);
			if (normal.normSquared() > (2 * RADIUS) * (2 * RADIUS)) {
				continue;
			}

			// Find new velocities
			normal = normal.normalize();
			Vec2D tangent = new Vec2D(-normal.getY(), normal.getX());
			ball1.velocity = tangent.scale(vel1.dot(tangent)).plus(normal.scale(vel2.dot(normal)));
			ball2.velocity = tangent.scale(vel2.dot(tangent)).plus(normal.scale(vel1.dot(normal)));
			ball1.position = ball1.position.plus(ball1.velocity);
			ball2.position = ball2.position.plus(ball2.velocity);

			return true;
		}

		return false;
	}

	@Override
	public void render() {
		// Update ball location
		for (GameBall ball : balls) {
			ball.circle.setCenterX(ball.position.getX());
			ball.circle.setCenterY(ball.position.getY());
		}

	}

	@Override
	public int getTickDelay() {
		return TICK_DELAY;
	}

	private class GameBall {

		Circle circle;
		volatile Vec2D position, velocity;

		GameBall(Color color, double posX, double posY, double velX, double velY) {
			this.position = new Vec2D(posX, posY);
			this.velocity = new Vec2D(velX, velY);

			circle = new Circle(RADIUS);
			circle.setFill(color);
			circle.setCenterX(position.getX());
			circle.setCenterY(position.getY());
		}

	}
}
