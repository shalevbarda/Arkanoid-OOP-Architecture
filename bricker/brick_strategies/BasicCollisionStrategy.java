package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * Basic collision strategy that removes the brick and decrements the brick counter.
 * <p>
 * This is the default behavior for bricks. When a brick is hit, it is removed
 * from the game and the remaining brick count is decremented.
 *
 * @author Omri Markovich, Shalev Barda
 * @see CollisionStrategy
 */
public class BasicCollisionStrategy implements CollisionStrategy {

	private final GameObjectCollection gameObjects;
	private final Counter bricksCounter;

	/**
	 * Creates a basic collision strategy.
	 *
	 * @param gameObjects    The collection of game objects to remove the brick from
	 * @param bricksCounter  The counter tracking remaining bricks
	 */
	public BasicCollisionStrategy(GameObjectCollection gameObjects, Counter bricksCounter) {
		this.gameObjects = gameObjects;
		this.bricksCounter = bricksCounter;
	}

	/**
	 * Removes the brick from the game and decrements the brick counter.
	 *
	 * @param brick    The brick that was hit
	 * @param collider The game object that collided with the brick
	 */
	@Override
	public void onCollision(GameObject brick, GameObject collider) {
		if (gameObjects.removeGameObject(brick, Layer.STATIC_OBJECTS)) {
			bricksCounter.decrement();
		}
	}
}
