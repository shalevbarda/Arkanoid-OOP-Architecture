package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Strategy interface for handling brick collision behavior.
 * <p>
 * Implementations define what happens when a brick is hit by a game object,
 * typically a ball. Strategies can be combined using the decorator pattern.
 *
 * @author Omri Markovich, Shalev Barda
 * @see BasicCollisionStrategy
 */
public interface CollisionStrategy {
	/**
	 * Handles the collision between a brick and another game object.
	 *
	 * @param brick    The brick that was hit
	 * @param collider The game object that collided with the brick
	 */
	void onCollision(GameObject brick, GameObject collider);
}
