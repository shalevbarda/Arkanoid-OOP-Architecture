package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.gameobjects.HeartLifeDisc;

/**
 * Collision strategy that creates a falling heart disc to restore a life.
 * <p>
 * When a brick with this strategy is hit, it creates a heart disc that falls
 * downward. If caught by the main paddle, it restores one life.
 *
 * @author Omri Markovich, Shalev Barda
 * @see CollisionStrategy
 * @see HeartLifeDisc
 */
public class ExtraLifeStrategy implements CollisionStrategy {

	private final CollisionStrategy behavior;
	private final GameObjectCollection gameObjects;
	private final BrickerGameManager gameManager;
	private final Renderable discImage;
	private final Vector2 discSize;
	private final Vector2 discVelocity;

	/**
	 * Creates an extra life strategy.
	 *
	 * @param behavior      The base collision strategy to wrap
	 * @param gameObjects  The collection of game objects to add the disc to
	 * @param gameManager  The game manager to restore lives
	 * @param discImage    The image for the heart disc
	 * @param discSize     The size of the heart disc
	 * @param discVelocity The falling velocity of the heart disc
	 */
	public ExtraLifeStrategy(CollisionStrategy behavior,
							 GameObjectCollection gameObjects,
							 BrickerGameManager gameManager,
							 Renderable discImage,
							 Vector2 discSize,
							 Vector2 discVelocity) {
		this.behavior = behavior;
		this.gameObjects = gameObjects;
		this.gameManager = gameManager;
		this.discImage = discImage;
		this.discSize = discSize;
		this.discVelocity = discVelocity;
	}

	/**
	 * Executes the base behavior and creates a falling heart disc.
	 *
	 * @param brick    The brick that was hit
	 * @param collider The game object that collided with the brick
	 */
	@Override
	public void onCollision(GameObject brick, GameObject collider) {
		this.behavior.onCollision(brick, collider);
		// Create and add the heart life disc
		HeartLifeDisc disc = new HeartLifeDisc(
				brick.getCenter().subtract(discSize.mult(0.5f)),
				discSize,
				discImage,
				gameManager,
				gameObjects
		);

		disc.setVelocity(discVelocity);

		gameObjects.addGameObject(disc, Layer.DEFAULT);
	}
}

