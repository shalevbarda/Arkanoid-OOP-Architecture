package bricker.brick_strategies;

import danogl.GameObject;
import bricker.gameobjects.Ball;
import danogl.collisions.GameObjectCollection;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

/**
 * Collision strategy that creates multiple extra balls when a brick is hit.
 * <p>
 * When a brick with this strategy is hit, it creates 2 additional balls (pucks)
 * with random velocities. These balls automatically remove themselves when
 * they fall into the death zone.
 *
 * @author Omri Markovich, Shalev Barda
 * @see CollisionStrategy
 */
public class ExtraBallsStrategy implements CollisionStrategy {
	private static final int NUM_OF_EXTRA_BALLS = 2;
	private final CollisionStrategy behavior;
	private final Vector2 dimensions;
	private final Renderable renderable;
	private final Sound collisionSound;
	private final GameObjectCollection gameObjects;
	private final float ballSpeed;

	/**
	 * Creates an extra balls strategy.
	 *
	 * @param behavior       The base collision strategy to wrap
	 * @param gameObjects    The collection of game objects to add balls to
	 * @param renderable     The image for the extra balls
	 * @param collisionSound The sound to play when balls collide
	 * @param dimensions     The size of the extra balls
	 * @param ballSpeed      The speed of the extra balls
	 */
	public ExtraBallsStrategy(CollisionStrategy behavior,
							  GameObjectCollection gameObjects,
							  Renderable renderable,
							  Sound collisionSound,
							  Vector2 dimensions,
							  float ballSpeed) {
		this.behavior = behavior;
		this.dimensions = dimensions;
		this.renderable = renderable;
		this.collisionSound = collisionSound;
		this.gameObjects = gameObjects;
		this.ballSpeed = ballSpeed;
	}

	private void setRandomPuckVelocity(Ball ball) {
		Random random = new Random();
		double angle = random.nextDouble() * 2 * Math.PI;
		float velocityX = (float) (Math.cos(angle) * ballSpeed);
		float velocityY = (float) (Math.sin(angle) * ballSpeed);
		ball.setVelocity(new Vector2(velocityX, velocityY));
	}

	/**
	 * Executes the base behavior and creates extra balls with random velocities.
	 *
	 * @param brick    The brick that was hit
	 * @param collider The game object that collided with the brick
	 */
	@Override
	public void onCollision(GameObject brick, GameObject collider) {
		this.behavior.onCollision(brick, collider);
		for (int i = 0; i < NUM_OF_EXTRA_BALLS; i++) {
			// Use an array to hold the reference so the lambda can capture it
			// This allows the lambda to remove the specific puck ball when it falls
			final Ball[] puckHolder = new Ball[1];
			puckHolder[0] = new Ball(
					brick.getCenter().subtract(dimensions.mult(0.5f)),
					dimensions,
					renderable,
					collisionSound,
					() -> gameObjects.removeGameObject(puckHolder[0]));


			setRandomPuckVelocity(puckHolder[0]);
			gameObjects.addGameObject(puckHolder[0]);
		}

	}
}
